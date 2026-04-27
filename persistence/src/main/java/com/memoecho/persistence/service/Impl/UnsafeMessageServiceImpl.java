package com.memoecho.persistence.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memoecho.memo_echo_apis.vo.UnsafeGroupVO;
import com.memoecho.memo_echo_apis.vo.UnsafeMessageVO;
import com.memoecho.persistence.mapper.UnsafeMessageMapper;
import com.memoecho.persistence.pojo.UnsafeMessage;
import com.memoecho.persistence.service.UnsafeMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnsafeMessageServiceImpl extends
        ServiceImpl<UnsafeMessageMapper, UnsafeMessage> implements UnsafeMessageService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class) // 遇到异常回滚
    public boolean updateMsgAndScore(UnsafeMessage msg) {
        UnsafeMessage existMessage = this.getById(msg.getMessageId());
        // 将敏感消息记录进 MySql 数据库
        if(existMessage==null){
            this.save(msg);
        }else{
            log.info("消息已经存入数据库");
            return false;
        }

        Long groupId = msg.getGroupId();
        int Score = msg.getFilterScore();
        // 将redis中缓存的群组敏感分增加
        if(groupId!=null){
            stringRedisTemplate.opsForHash().increment(
                    "group_unsafe_score",
                    groupId.toString(),
                    Score
            );
        }
        return true;
    }

    @Override
    public List<UnsafeGroupVO> getUnsafeGroupInfo() {
        Map<Object,Object> rawEntries =
                stringRedisTemplate.opsForHash().entries("group_unsafe_score");

        Map<Long,Long> curRes = rawEntries.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> Long.valueOf((String)e.getKey()),
                    e -> Long.valueOf((String)e.getValue())
                ));

        return curRes.entrySet().stream()
                .map(e -> UnsafeGroupVO.builder()
                        .groupId(e.getKey())
                        .score(e.getValue())
                        .build()
                ).toList();
    }

    @Override
    public List<UnsafeMessageVO> getUnsafeMessageInfo(Long groupId) {
        LambdaQueryWrapper<UnsafeMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnsafeMessage::getGroupId,groupId);
        List<UnsafeMessageVO> res = this.list(queryWrapper).stream().map(
                e -> UnsafeMessageVO.builder()
                        .time(e.getTime())
                        .postType(e.getPostType())
                        .messageType(e.getMessageType())
                        .messageId(e.getMessageId())
                        .groupId(e.getGroupId()).userId(e.getUserId())
                        .rawMessage(e.getRawMessage())
                        .filterScore(e.getFilterScore()).senderUserId(e.getSenderUserId())
                        .senderRole(e.getSenderRole()).senderCard(e.getSenderCard())
                        .senderNickname(e.getSenderNickname())
                        .build()
        ).toList();
        return res;
    }
}
