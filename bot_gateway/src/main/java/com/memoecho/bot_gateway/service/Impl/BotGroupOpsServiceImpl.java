package com.memoecho.bot_gateway.service.Impl;

import com.memoecho.bot_gateway.client.NapcatClient;
import com.memoecho.bot_gateway.dto.BotGroupRequestDTO;
import com.memoecho.bot_gateway.dto.BotGroupRequestResult;
import com.memoecho.bot_gateway.service.BotGroupOpsService;
import com.memoecho.common.response.NapCatResponse;
import com.memoecho.memo_echo_apis.vo.GroupInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BotGroupOpsServiceImpl implements BotGroupOpsService {
    private final NapcatClient napcatClient;

    @Override
    public BotGroupRequestResult handleGroupRequest(BotGroupRequestDTO dto) {
        if (dto.getGroupId() != null && isBotInGroup(dto.getGroupId())) {
            return new BotGroupRequestResult(dto.getGroupId(), "ALREADY_IN_GROUP", "机器人已在该群。");
        }

        String flag = dto.getFlag() == null ? "" : dto.getFlag().trim();
        if (flag.isEmpty()) {
            return new BotGroupRequestResult(
                    dto.getGroupId(),
                    "NEED_INVITE",
                    "当前 NapCat 接口无法只凭群号主动加群，请先邀请机器人入群，或传入平台请求 flag 后处理。"
            );
        }

        Map<String, Object> params = new HashMap<>();
        params.put("flag", flag);
        params.put("sub_type", normalizeSubType(dto.getSubType()));
        params.put("approve", dto.getApprove() == null || dto.getApprove());
        params.put("reason", dto.getReason() == null ? "" : dto.getReason().trim());

        NapCatResponse<Void> response = napcatClient.setGroupAddRequest(params);
        if (response == null || response.getRetcode() == null) {
            return new BotGroupRequestResult(dto.getGroupId(), "NAPCAT_NO_RESPONSE", "NapCat 未返回有效结果。");
        }

        if (response.getRetcode() != 0) {
            String message = response.getMessage() == null ? "处理入群请求失败。" : response.getMessage();
            return new BotGroupRequestResult(dto.getGroupId(), "NAPCAT_FAILED", message);
        }

        return new BotGroupRequestResult(dto.getGroupId(), "REQUEST_HANDLED", "入群请求已提交给 NapCat。");
    }

    private boolean isBotInGroup(Long groupId) {
        NapCatResponse<List<GroupInfoVO>> response = napcatClient.getGroupList();
        if (response == null || response.getData() == null) {
            return false;
        }

        return response.getData().stream()
                .anyMatch(group -> Objects.equals(group.getGroupId(), groupId));
    }

    private String normalizeSubType(String subType) {
        String normalized = subType == null ? "" : subType.trim();
        return normalized.isEmpty() ? "invite" : normalized;
    }
}
