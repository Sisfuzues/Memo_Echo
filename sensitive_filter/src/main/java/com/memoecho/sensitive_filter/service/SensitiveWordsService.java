package com.memoecho.sensitive_filter.service;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.memoecho.sensitive_filter.pojo.SensitiveWords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

@Service
@Slf4j(topic = "敏感词筛查")
public class SensitiveWordsService {

    private AhoCorasickDoubleArrayTrie<SensitiveWords> acdat;

    @Value("${bot.dict.cache_path:./sensitive_words.bin}")
    private String cachePath;
    @Value("${bot.dict.level3_path:dict/sensitive_words_level3}")
    private String txtLevel3Path;
    @Value("${bot.dict.level2_path:dict/sensitive_words_level3}")
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

        acdat.build(tmp);
        log.info("敏感词过滤器加载完毕，耗时:{} ms。",System.currentTimeMillis() - begin);
    }

    public void loadFromTxt(String path,TreeMap<String,SensitiveWords> tmp){
        try(InputStream is =
                    new ClassPathResource(txtLevel2Path).getInputStream();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
            String line;
            while((line = reader.readLine())!=null){
                line = line.trim();
                if(line.isEmpty() || line.startsWith("#")){
                    continue;
                }
                tmp.put(line,new SensitiveWords(SensitiveWords.Level.level2,1));
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
}
