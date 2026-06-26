package com.critter.critter_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 프론트에서 보낸 JSON과 필드명이 일치해야 함!
@Getter
@Setter
@NoArgsConstructor
public class GuestbookRequestDto {
    private Long writerId;
    private String content;
}