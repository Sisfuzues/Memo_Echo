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
public class FriendInfoVO {
    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "nickname")
    private String nickName;

    @JSONField(name = "remark")
    private String remark;
}
