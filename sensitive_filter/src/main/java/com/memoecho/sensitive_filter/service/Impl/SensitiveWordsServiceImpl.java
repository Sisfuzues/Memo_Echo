package com.memoecho.sensitive_filter.service.Impl;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.memoecho.memo_echo_apis.dto.ReceivedMessage;
import com.memoecho.sensitive_filter.pojo.SensitiveWords;
import com.memoecho.sensitive_filter.service.SensitiveWordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FilenameUtils;
import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j(topic = "敏感词筛查")
public class SensitiveWordsServiceImpl implements SensitiveWordsService {
    @Autowired
    private MQServiceImpl mqService;
    private AhoCorasickDoubleArrayTrie<SensitiveWords> acdat;

    @Value("${bot.dict.cache_path:./sensitive_words.bin}")
    private String cachePath;
    @Value("${bot.dict.level3_path:dict/sensitive_words_level3.txt}")
    private String txtLevel3Path;
    @Value("${bot.dict.level2_path:dict/sensitive_words_level2.txt}")
    private String txtLevel2Path;

    @PostConstruct
    public void init(){
        File cacheFile = new File(cachePath);

        if(cacheFile.exists()){
            if(loadFromBin(cacheFile)){
                return ;
            }
        }

        log.info("未缓存Bin文件，马上从TXT文本中构建。");
        buildFromTxt();

        saveToCache(cacheFile);
    }

    public Boolean loadFromBin(File cacheFile){
        long begin = System.currentTimeMillis();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))){
            acdat = new AhoCorasickDoubleArrayTrie<>();
            acdat.load(ois);
            log.info("敏感词筛查器加载完毕,耗时 {} ms。",System.currentTimeMillis()-begin);
        } catch (Exception e){
            log.error("敏感词筛查器初始化失败：",e);
            return false;
        }
        return true;
    }

    public void buildFromTxt(){
        long begin = System.currentTimeMillis();
        TreeMap<String,SensitiveWords> tmp = new TreeMap<>();

        loadFromTxt(txtLevel2Path,tmp);
        loadFromTxt(txtLevel3Path,tmp);

        acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(tmp);
        log.info("敏感词过滤器加载完毕，耗时:{} ms。",System.currentTimeMillis() - begin);
    }

    public void loadFromTxt(String path,TreeMap<String,SensitiveWords> tmp){
        // 初始化解析级别
        String res = FilenameUtils.getBaseName(path);
        SensitiveWords.Level needLevel = SensitiveWords.Level.level1;
        int needValue = switch (res.charAt(res.length() - 1)) {
            case '3' -> {
                needLevel = SensitiveWords.Level.level3;
                yield 5;
            }
            case '2' -> {
                needLevel = SensitiveWords.Level.level2;
                yield 1;
            }
            default -> 0;
        };
        // 开始读文件
        try(InputStream is =
                    new ClassPathResource(path).getInputStream();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
            String line;
            while((line = reader.readLine())!=null){
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")){
                    continue;
                }
                tmp.put(line,new SensitiveWords(needLevel,needValue));
            }
        } catch (Exception e){
            log.error("{}路径文件敏感词读取失败，请查看原因：",path,e);
        }
    }

    public void saveToCache(File cacheFile){
        try(ObjectOutputStream oos =
                    new ObjectOutputStream(new FileOutputStream(cacheFile));) {
            acdat.save(oos);
            log.info("保存缓存成功。");
        } catch (Exception e){
            log.error("保存缓存失败,错误信息：",e);
        }
    }

    @Override
    public boolean sensitiveWordsKill(ReceivedMessage msg) {
        String text = msg.getRawMessage();
        if(acdat==null || text==null || text.trim().isEmpty()){
            return false;
        }
        List<AhoCorasickDoubleArrayTrie.Hit<SensitiveWords>> res =
                acdat.parseText(text);

        int resTotal = 0;
        for(AhoCorasickDoubleArrayTrie.Hit<SensitiveWords> hit:res){
            resTotal += hit.value.getValue();
        }
//        System.out.println(resTotal);
        // 是否被斩杀
        msg.setFilterScore(resTotal);
        return resTotal > SensitiveWords.Constant.DANGER_SCORE;
    }

    @Override
    public void handleConsumerMsg(String safeTopic, String safeTags, String unsafeTopic, String unsafeTags,
                                  ReceivedMessage payload) {
        // 是否被斩杀
        String topic,tag,key;
        key = payload.getMessageId().toString();
        if(sensitiveWordsKill(payload)){
            payload.setFilterStatus(ReceivedMessage.FilterStatus.UNSAFE);
            topic = unsafeTopic;
            tag = unsafeTags;
            mqService.sendToMQ(payload,topic,tag,key);
        }else{
            payload.setFilterStatus(ReceivedMessage.FilterStatus.SAFE);
        }

        // 不管有没有筛选过，都发送给AI
        topic = safeTopic;
        tag = safeTags;
        mqService.sendToMQ(payload,topic,tag,key);
    }
}
