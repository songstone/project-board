package com.song.projectboard.service;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.type.SearchType;
import com.song.projectboard.dto.ArticleDto;
import com.song.projectboard.dto.ArticleUpdateDto;
import com.song.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("게시글 로직 테스트")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;


    @DisplayName("게시글 검색 -> 게시글 리스트 페이지네이션 반환")
    @Test
    void searching_articles() {
        //given

        //when
        Page<ArticleDto> articles = articleService.searchArticles(SearchType.TITLE, "keyword");

        //then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글 조회 -> 게시글 반환")
    @Test
    void select_article() {
        //given

        //when
        ArticleDto article = articleService.searchArticle(1L);

        //then
        assertThat(article).isNotNull();
    }

    @DisplayName("게시글 생성")
    @Test
    void createArticle() {
        //given
        given(articleRepository.save(any(Article.class))).willReturn(any(Article.class));

        //when
        articleService.saveArticle(ArticleDto.of("title", "content", "#hashtag", LocalDateTime.now(), "song"));

        //then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 수정")
    @Test
    void updateArticle() {
        //given
        given(articleRepository.save(any(Article.class))).willReturn(any(Article.class));

        //when
        articleService.updateArticle(ArticleUpdateDto.of("title", "content", "#hashtag"));

        //then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 삭제")
    @Test
    void deleteArticle() {
        //given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        //when
        articleService.deleteArticle(1L);

        //then
        then(articleRepository).should().delete(any(Article.class));
    }
}