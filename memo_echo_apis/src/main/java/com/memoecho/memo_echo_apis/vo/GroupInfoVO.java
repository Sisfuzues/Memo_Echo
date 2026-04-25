package com.memoecho.memo_echo_apis.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfoVO {
    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "group_name")
    private String groupName;

    @JSONField(name = "member_count")
    private Long memberCount;
}
