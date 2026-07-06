package com.critter.critter_backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointReason {
    POST_BOARD(LogTargetType.BOARD, ActionType.POST_BOARD),
    POST_COMMENT(LogTargetType.COMMENT, ActionType.POST_COMMENT),
    POST_GUESTBOOK(LogTargetType.GUESTBOOK, ActionType.POST_GUESTBOOK),
    DAILY_VISIT_REWARD(null, ActionType.ENTER_ROOM),
    ADOPT_CRITTER(LogTargetType.CRITTER, ActionType.ADOPT_CRITTER),
    CREATE_ROOM(LogTargetType.ROOM, ActionType.CREATE_ROOM);

    private final LogTargetType targetType;
    private final ActionType actionType;
}
