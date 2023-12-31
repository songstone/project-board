package com.song.projectboard.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.QArticle;
import com.song.projectboard.repository.querydsl.CustomArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
    JpaRepository<Article, Long>,
    CustomArticleRepository,
    QuerydslPredicateExecutor<Article>,
    QuerydslBinderCustomizer<QArticle> {
    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true); // 모든 필드에 대한 검색 -> 리스트에 포함된 필드만 검색 조건 걸기
        bindings.including(root.title, root.hashtag, root.createdAt, root.createdBy); // 검색 필드 지정

        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  // 검색 방식 지정
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
    }

    Page<Article> findByTitleContaining(String searchKeyword, Pageable pageable);

    Page<Article> findByContentContaining(String searchKeyword, Pageable pageable);

    Page<Article> findByHashtag(String searchKeyword, Pageable pageable);

    Page<Article> findByUserAccount_NicknameContaining(String searchKeyword, Pageable pageable);

    Page<Article> findByUserAccount_UserIdContaining(String searchKeyword, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long articleId, String userId);
}
