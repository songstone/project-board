package com.song.projectboard.dto.response;

import com.song.projectboard.dto.ArticleCommentDto;

import java.time.LocalDateTime;

public record ArticleCommentResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    String userId,
    String email,
    String nickname
) {
    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String userId, String email, String nickname) {
        return new ArticleCommentResponse(id, content, createdAt, userId, email, nickname);
    }

    public static ArticleCommentResponse fromDto(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleCommentResponse(
            dto.id(),
            dto.content(),
            dto.createdAt(),
            dto.userAccountDto().userId(),
            dto.userAccountDto().email(),
            nickname
        );
    }
}
