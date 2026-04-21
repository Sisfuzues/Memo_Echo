package com.memoecho.persistence.service.Impl;


import com.memoecho.persistence.pojo.CodeDetails;
import com.memoecho.persistence.service.CodeMessageService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class CodeMessageServiceImpl implements CodeMessageService {
    // 验证码存活时间
    private static final long CODE_EXPIRATION_TIME = 5 * 60 * 1000;
    // 存储验证码信息
    private static final Map<String, CodeDetails> codeMap = new HashMap<>();


    @Override
    public void saveCode(String toEmail, String code) {
        codeMap.put(toEmail, new CodeDetails(toEmail, code,System.currentTimeMillis() + CODE_EXPIRATION_TIME));
    }

    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @Override
    public boolean verifyCode(String email, String code) {
        return false;
    }
}
