package com.memoecho.persistence.controller.user.admin;

import com.memoecho.common.response.ApiResponse;
import com.memoecho.memo_echo_apis.vo.UnsafeGroupVO;
import com.memoecho.memo_echo_apis.vo.UnsafeMessageVO;
import com.memoecho.persistence.service.Impl.UnsafeMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 🛰️ 处理管理员监控全局的需求
 * <hr/>
 * 🧩 职责：
 * 🛡️ 关联：
 * 🗺️ 架构： (MemoEcho)
 *
 * @author Sisfuzues
 * &#064;date  2026/4/19 22:48
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOpsController {
    private final UnsafeMessageServiceImpl unsafeMessageService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/group/unsafe")
    public ApiResponse<List<UnsafeGroupVO>> getUnsafeGroup(){
        List<UnsafeGroupVO> unsafeGroupInfo = unsafeMessageService.getUnsafeGroupInfo();
        return ApiResponse.success("获取违规群组列表成功", unsafeGroupInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/group/unsafe/messages")
    public ApiResponse<List<UnsafeMessageVO>> getUnsafeMessageInfo(){
        List<UnsafeGroupVO> unsafeMessageInfo = unsafeMessageService.getUnafeGroupInfo();
        return ApiResponse.success("获取违规群组列表成功", unsafeMessageInfo);
    }
}
