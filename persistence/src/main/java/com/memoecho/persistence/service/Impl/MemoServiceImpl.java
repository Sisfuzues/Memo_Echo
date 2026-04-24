package com.memoecho.persistence.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memoecho.memo_echo_apis.dto.ExtractedMessage;
import com.memoecho.persistence.mapper.MemoMapper;
import com.memoecho.persistence.pojo.Memo;
import com.memoecho.persistence.service.MemoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class MemoServiceImpl extends ServiceImpl<MemoMapper,Memo> implements MemoService {
    @Override
    public List<ExtractedMessage> getByIds(List<Long> msgIds) {
        if(msgIds==null||msgIds.isEmpty()){
            return Collections.emptyList();
        }

        List<Memo> res = this.baseMapper.selectList(
                new LambdaQueryWrapper<Memo>()
                        .in(Memo::getMessageId,msgIds)
        );
        if (res==null||res.isEmpty()){
            return Collections.emptyList();
        }

        List<ExtractedMessage> ans = new ArrayList<>();
        for(var it:res){
            ExtractedMessage.MemoScheduled memo = ExtractedMessage.MemoScheduled.builder()
                    .content(it.getContent())
                    .endTime(it.getEndTime())
                    .participants(it.getParticipants())
                    .startTime(it.getStartTime())
                    .introduce(it.getIntroduce())
                    .location(it.getLocation())
                    .build();

            ExtractedMessage cur = ExtractedMessage.builder()
                    .originalMsgId(it.getMessageId())
                    .userId(it.getSenderId()).groupId(it.getGroupId())
                    .memo(memo)
                    .build();
            ans.add(cur);
        }
        return ans;
    }
}
