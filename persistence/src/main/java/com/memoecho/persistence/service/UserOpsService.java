package com.memoecho.persistence.service;

import com.memoecho.persistence.dto.ScheduleGroupRequestDTO;
import com.memoecho.persistence.dto.ScheduleGroupRequestVO;

import java.util.List;

public interface UserOpsService {
    ScheduleGroupRequestVO requestScheduleGroup(ScheduleGroupRequestDTO dto, String requesterUserId);

    List<ScheduleGroupRequestVO> listScheduleGroupRequests();

    boolean removeScheduleGroupRequest(Long groupId);
}
