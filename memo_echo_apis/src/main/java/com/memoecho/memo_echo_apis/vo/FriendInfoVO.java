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
public class FriendInfoVO {
    @JsonAlias("user_id")
    private Long userId;

    @JsonAlias("nickname")
    private String nickName;

    @JsonAlias("remark")
    private String remark;
}
