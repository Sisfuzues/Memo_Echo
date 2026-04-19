package com.memoecho.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memoecho.persistence.pojo.UnsafeMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UnsafeMessageMapper extends BaseMapper<UnsafeMessage> {
}
