package com.memoecho.persistence.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("unsafe_message")
public class UnsafeMessage {
    private Long time;
    @JSONField(name = "post_type")
    private String postType;

    @JSONField(name = "message_type")
    private String messageType;

    @JSONField(name = "sub_type")
    private String subType;

    @TableId(type = IdType.INPUT)
    @JSONField(name = "message_id")
    private Long messageId;

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "raw_message")
    private String rawMessage;
    @JSONField(name = "filter_score")
    private int filterScore;

    private Long senderUserId;
    private String senderRole;
    private String senderCard;      // 群名片
    private String senderNickname;  // 全局昵称

    @TableField(exist = false)
    private Sender sender;

    // fastjson 通过反射，可以将传进来的信息"压扁"，就是通过这个方法。
    public void setSender(Sender sender){
        this.sender = sender;
        if(sender!=null){
            senderCard = sender.card;
            senderNickname = sender.nickname;
            senderRole = sender.role;
            senderUserId = sender.userId;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sender {
        @JSONField(name = "user_id")
        private Long userId;
        private String role;
        private String card;
        private String nickname;
    }
}
