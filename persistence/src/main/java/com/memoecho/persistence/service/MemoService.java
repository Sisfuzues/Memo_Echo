package com.memoecho.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.persistence.pojo.Memo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemoService extends IService<Memo> {
    List<ExtractedMessage> getByIds(List<Long> msgIds);
}
