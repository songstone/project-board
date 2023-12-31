package com.song.projectboard.dto.response;

import com.song.projectboard.dto.ArticleWithCommentsDto;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentResponse(
    Long id,
    String title,
    String content,
    String hashtag,
    LocalDateTime createdAt,
    String userId,
    String email,
    String nickname,
    Set<ArticleCommentResponse> articleCommentResponses
) {
    public static ArticleWithCommentResponse of(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String userId,
        String email,
        String nickname,
        Set<ArticleCommentResponse> articleCommentResponses
    ) {
        return new ArticleWithCommentResponse(id, title, content, hashtag, createdAt, userId, email, nickname, articleCommentResponses);
    }

    public static ArticleWithCommentResponse fromDto(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.createdAt(),
            dto.userAccountDto().userId(),
            dto.userAccountDto().email(),
            nickname,
            dto.articleCommentDtos().stream()
                .map(ArticleCommentResponse::fromDto)
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
