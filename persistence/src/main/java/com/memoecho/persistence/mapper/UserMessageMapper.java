package com.memoecho.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memoecho.persistence.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMessageMapper extends BaseMapper<User> {
}
