package com.song.projectboard.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.song.projectboard.domain.ArticleComment;
import com.song.projectboard.domain.QArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleCommentRepository extends
    JpaRepository<ArticleComment, Long>,
    QuerydslPredicateExecutor<ArticleComment>,
    QuerydslBinderCustomizer<QArticleComment> {
    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true); // 모든 필드에 대한 검색 -> 리스트에 포함된 필드만 검색 조건 걸기
        bindings.including(root.content, root.createdAt, root.createdBy); // 검색 필드 지정

        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);  // 검색 방식 지정
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
    }
}
