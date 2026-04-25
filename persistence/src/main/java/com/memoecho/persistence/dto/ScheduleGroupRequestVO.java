package com.memoecho.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleGroupRequestVO {
    private Long groupId;
    private String reason;
    private String requesterUserId;
    private Long createdAt;
}
