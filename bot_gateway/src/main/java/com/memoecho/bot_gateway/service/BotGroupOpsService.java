package com.memoecho.bot_gateway.service;

import com.memoecho.bot_gateway.dto.BotGroupRequestDTO;
import com.memoecho.bot_gateway.dto.BotGroupRequestResult;

public interface BotGroupOpsService {
    BotGroupRequestResult handleGroupRequest(BotGroupRequestDTO dto);
}
