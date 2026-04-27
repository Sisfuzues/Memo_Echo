package com.memoecho.sensitive_filter.service.Impl;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.pojo.SensitiveWords;
import com.memoecho.sensitive_filter.service.SensitiveWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j(topic = "sensitive-words-filter")
@RequiredArgsConstructor
public class SensitiveWordsServiceImpl implements SensitiveWordsService {
    private final MQServiceImpl mqService;

    private AhoCorasickDoubleArrayTrie<SensitiveWords> acdat;

    @Value("${bot.dict.cache_path:./sensitive_words.bin}")
    private String cachePath;

    @Value("${bot.dict.level3_path:dict/sensitive_words_level3.txt}")
    private String txtLevel3Path;

    @Value("${bot.dict.level2_path:dict/sensitive_words_level2.txt}")
    private String txtLevel2Path;

    @PostConstruct
    public void init() {
        File cacheFile = new File(cachePath);
        if (cacheFile.exists() && loadFromBin(cacheFile)) {
            return;
        }

        log.info("Sensitive words cache not found, building from text dictionaries");
        buildFromTxt();
        saveToCache(cacheFile);
    }

    boolean loadFromBin(File cacheFile) {
        long begin = System.currentTimeMillis();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            acdat = new AhoCorasickDoubleArrayTrie<>();
            acdat.load(ois);
            log.info("Sensitive words filter loaded in {} ms", System.currentTimeMillis() - begin);
            return true;
        } catch (Exception e) {
            log.error("Failed to initialize sensitive words filter", e);
            return false;
        }
    }

    void buildFromTxt() {
        long begin = System.currentTimeMillis();
        TreeMap<String, SensitiveWords> words = new TreeMap<>();
        loadFromTxt(txtLevel2Path, words);
        loadFromTxt(txtLevel3Path, words);

        acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(words);
        log.info("Sensitive words filter built in {} ms", System.currentTimeMillis() - begin);
    }

    void loadFromTxt(String path, TreeMap<String, SensitiveWords> words) {
        String baseName = FilenameUtils.getBaseName(path);
        SensitiveWords.Level level = SensitiveWords.Level.level1;
        int value = switch (baseName.charAt(baseName.length() - 1)) {
            case '3' -> {
                level = SensitiveWords.Level.level3;
                yield 5;
            }
            case '2' -> {
                level = SensitiveWords.Level.level2;
                yield 1;
            }
            default -> 0;
        };

        try (InputStream is = new ClassPathResource(path).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                words.put(line, new SensitiveWords(level, value));
            }
        } catch (Exception e) {
            log.error("Failed to load sensitive words file: {}", path, e);
        }
    }

    void saveToCache(File cacheFile) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            acdat.save(oos);
            log.info("Sensitive words cache saved");
        } catch (Exception e) {
            log.error("Failed to save sensitive words cache", e);
        }
    }

    @Override
    public boolean sensitiveWordsKill(ReceivedMessage msg) {
        String text = msg.getRawMessage();
        if (acdat == null || text == null || text.trim().isEmpty()) {
            return false;
        }

        List<AhoCorasickDoubleArrayTrie.Hit<SensitiveWords>> hits = acdat.parseText(text);
        int totalScore = 0;
        for (AhoCorasickDoubleArrayTrie.Hit<SensitiveWords> hit : hits) {
            totalScore += hit.value.getValue();
        }

        msg.setFilterScore(totalScore);
        return totalScore > SensitiveWords.Constant.DANGER_SCORE;
    }

    @Override
    public void handleConsumerMsg(String safeTopic, String safeTags,
                                  String unsafeTopic, String unsafeTags,
                                  ReceivedMessage payload) {
        String key = String.valueOf(payload.getMessageId());
        if (sensitiveWordsKill(payload)) {
            payload.setFilterStatus(ReceivedMessage.FilterStatus.UNSAFE);
            mqService.sendToMQ(payload, unsafeTopic, unsafeTags, key);
        } else {
            payload.setFilterStatus(ReceivedMessage.FilterStatus.SAFE);
        }

        mqService.sendToMQ(payload, safeTopic, safeTags, key);
    }
}
