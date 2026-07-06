package com.critter.critter_backend.event;

import com.critter.critter_backend.domain.ActionType;
import com.critter.critter_backend.domain.LogTargetType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ActionLogEvents {
    @Getter
    @RequiredArgsConstructor
    public static class recordActionLog {
        private final Long userId;
        private final Long roomId;
        private final Long targetId;
        private final LogTargetType targetType;
        private final ActionType actionType;
    }
}
