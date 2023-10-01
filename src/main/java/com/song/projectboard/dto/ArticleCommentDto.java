package com.song.projectboard.dto;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.ArticleComment;
import com.song.projectboard.domain.UserAccount;

import java.time.LocalDateTime;

public record ArticleCommentDto(
    Long id,
    Long articleId,
    UserAccountDto userAccountDto,
    String content,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
    public static ArticleCommentDto of(Long id, Long articleId, UserAccountDto userAccountDto, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDto(id, articleId, userAccountDto, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto of(Long articleId, UserAccountDto userAccountDto, String content) {
        return new ArticleCommentDto(null, articleId, userAccountDto, content, null, null, null , null);
    }

    public static ArticleCommentDto fromEntity(ArticleComment entity) {
        return new ArticleCommentDto(
            entity.getId(),
            entity.getArticle().getId(),
            UserAccountDto.fromEntity(entity.getUserAccount()),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article entity, UserAccount userAccount) {
        return ArticleComment.of(
            entity,
            userAccount,
            content
        );
    }

}
