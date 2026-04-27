package com.memoecho.persistence.pojo;

import lombok.Data;

/**
 * 🛰️ 群信息，关联用户
 * <hr/>
 * 🧩 职责：
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * &#064;date  2026/4/19 22:40
 */
@Data
public class UserGroup {
    private Long userId;

    private Long groupId;

    private int score;  // 敏感分

    private String groupName;
}
