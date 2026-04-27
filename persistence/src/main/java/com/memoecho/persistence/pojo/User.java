package com.memoecho.persistence.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 🛰️ User类
 * <hr/>
 * 🧩 职责：相应的，得把MySQL的建表语句发到群里哦。
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Nicky
 * &#064;date  2026/4/19 22:33
 */
@Data
@TableName("user")
public class User {
    @TableId
    private Long userId;
    private Long qqId;
    private String userName;
    private String email;
    private String phone;
    private String password;
    private boolean isKill; // 是否被拉黑

    private int userLevel; // 用户等级， 1为普通用户 ，10 为管理员
}
