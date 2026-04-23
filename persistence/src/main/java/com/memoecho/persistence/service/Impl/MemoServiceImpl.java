package com.memoecho.persistence.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memoecho.persistence.mapper.MemoMapper;
import com.memoecho.persistence.pojo.Memo;
import com.memoecho.persistence.service.MemoService;
import org.springframework.stereotype.Service;

@Service
public class MemoServiceImpl extends ServiceImpl<MemoMapper,Memo> implements MemoService {
}
