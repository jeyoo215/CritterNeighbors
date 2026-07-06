package com.critter.critter_backend.domain;

public enum LogTargetType {
    CRITTER("크리터"),
    BOARD("게시글"),
    COMMENT("댓글"),
    GUESTBOOK("방명록"),
    ROOM("방");

    private final String description;

    LogTargetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
