package com.song.projectboard.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FormStatus {

    CREATE("저장", false),
    UPDATE("수정", true);

    private final String description;
    private final boolean update;
}
