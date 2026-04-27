package com.memoecho.memo_echo_apis.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfoVO {
    @JsonAlias("group_id")
    private Long groupId;

    @JsonAlias("group_name")
    private String groupName;

    @JsonAlias("member_count")
    private Long memberCount;
}
