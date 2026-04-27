package com.memoecho.persistence.controller.user;

import com.memoecho.common.response.ApiResponse;
import com.memoecho.persistence.dto.ScheduleGroupRequestDTO;
import com.memoecho.persistence.dto.ScheduleGroupRequestVO;
import com.memoecho.persistence.service.UserOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 🛰️ 用户需求处理
 * <hr/>
 * 🧩 职责： 处理用户对于机器人的请求
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Jack
 * &#064;date  2026/4/19 22:46
 */
@RestController
@RequestMapping("/userops")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class UserOpsController {
    private final UserOpsService userOpsService;

    @PostMapping("/schedule/group/request")
    public ApiResponse<ScheduleGroupRequestVO> requestScheduleGroup(
            @RequestBody ScheduleGroupRequestDTO dto,
            Authentication authentication
    ) {
        if (dto == null || dto.getGroupId() == null || dto.getGroupId() <= 0) {
            return ApiResponse.fail(400, "groupId 非法。");
        }

        String reason = dto.getReason() == null ? "" : dto.getReason().trim();
        if (reason.length() > 200) {
            return ApiResponse.fail(400, "原因最多 200 个字符。");
        }

        ScheduleGroupRequestVO request = userOpsService.requestScheduleGroup(
                dto,
                authentication == null ? null : authentication.getName()
        );
        return ApiResponse.success("已提交群日程管理申请。", request);
    }
}
