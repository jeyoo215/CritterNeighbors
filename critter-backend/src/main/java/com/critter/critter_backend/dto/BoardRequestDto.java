package com.critter.critter_backend.dto;

import com.critter.critter_backend.domain.BoardCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardRequestDto {
    private Long writerId;
    private BoardCategory category;
    private String title;
    private String content;
}
