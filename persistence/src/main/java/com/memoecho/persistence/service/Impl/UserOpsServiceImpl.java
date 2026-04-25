package com.memoecho.persistence.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.memoecho.persistence.dto.ScheduleGroupRequestDTO;
import com.memoecho.persistence.dto.ScheduleGroupRequestVO;
import com.memoecho.persistence.service.UserOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOpsServiceImpl implements UserOpsService {
    private static final String SCHEDULE_GROUP_REQUEST_KEY = "schedule:group:requests";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public ScheduleGroupRequestVO requestScheduleGroup(ScheduleGroupRequestDTO dto, String requesterUserId) {
        String reason = dto.getReason() == null ? "" : dto.getReason().trim();
        ScheduleGroupRequestVO request = new ScheduleGroupRequestVO(
                dto.getGroupId(),
                reason,
                requesterUserId,
                Instant.now().toEpochMilli()
        );

        stringRedisTemplate.opsForHash().put(
                SCHEDULE_GROUP_REQUEST_KEY,
                dto.getGroupId().toString(),
                JSON.toJSONString(request)
        );

        return request;
    }

    @Override
    public List<ScheduleGroupRequestVO> listScheduleGroupRequests() {
        return stringRedisTemplate.opsForHash()
                .values(SCHEDULE_GROUP_REQUEST_KEY)
                .stream()
                .map(Object::toString)
                .map(value -> JSON.parseObject(value, ScheduleGroupRequestVO.class))
                .sorted(Comparator.comparing(
                        ScheduleGroupRequestVO::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }
}
