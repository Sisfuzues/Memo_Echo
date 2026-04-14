package com.memoecho.sensitive_filter.service;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
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

    private AhoCorasickDoubleArrayTrie<String> acdat;

    @Value("${}")
    private String cachePath;
    @Value("${}")
    private String txtPath;

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
        TreeMap<String,String> tmp = new TreeMap<>();
        try(InputStream is =
                    new ClassPathResource(txtPath).getInputStream();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){



        } catch (Exception e){

        }

    }

    public void saveToCache(File cacheFile){

    }
}
