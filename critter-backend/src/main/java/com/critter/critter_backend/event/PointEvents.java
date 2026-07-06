package com.critter.critter_backend.event;

import com.critter.critter_backend.domain.PointReason;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class PointEvents {

    @Getter
    @RequiredArgsConstructor
    public static class Earn {
        private final Long userId;
        private final Long amount;
        private final PointReason reason;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Spend {
        private final Long userId;
        private final Long amount;
        private final PointReason reason;
    }
}