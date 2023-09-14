package com.song.projectboard.service;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.UserAccount;
import com.song.projectboard.domain.type.SearchType;
import com.song.projectboard.dto.ArticleDto;
import com.song.projectboard.dto.ArticleWithCommentsDto;
import com.song.projectboard.dto.UserAccountDto;
import com.song.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("게시글 로직 테스트")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;


    @DisplayName("키워드 없이 게시글 검색 -> 게시글 리스트 페이지네이션 반환")
    @Test
    void no_keyword_searching_articles() {
        //given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        //when
        Page<ArticleDto> articles = articleService.searchArticles(null, null, pageable);

        //then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("게시글 검색 -> 게시글 리스트 페이지네이션 반환")
    @Test
    void searching_articles() {
        // given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = articleService.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("검색용 해시 태그 리스트 반환")
    @Test
    void hashtagList_for_search() {
        // given
        List<String> givenHashtags = List.of("#java", "#spring", "#jpa");
        given(articleRepository.findAllHashtags()).willReturn(givenHashtags);

        // When
        List<String> hashtags = articleService.getHashtags();

        // Then
        assertThat(hashtags).isEqualTo(givenHashtags);
        then(articleRepository).should().findAllHashtags();
    }

    @DisplayName("해시 태그 검색어 없이 해시태그를 통한 게시글 검색 -> 빈 페이지 반환")
    @Test
    void hashtag_searching_articles_no_searchKeyword() {
        // given
        Pageable pageable = Pageable.ofSize(20);

        // When
        Page<ArticleDto> articles = articleService.searchArticlesHashtag(null, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("해시 태그를 통한 게시글 검색 -> 게시글 리스트 페이지네이션 반환")
    @Test
    void hashtag_searching_articles() {
        // given
        String  searchTag = "#java";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtag(searchTag, pageable)).willReturn(Page.empty(pageable));

        // When
        Page<ArticleDto> articles = articleService.searchArticlesHashtag(searchTag, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtag(searchTag, pageable);
    }

    @DisplayName("게시글 조회 -> 게시글 반환")
    @Test
    void select_article() {
        //given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        //when
        ArticleWithCommentsDto dto = articleService.getArticle(articleId);

        //then
        assertThat(dto)
            .hasFieldOrPropertyWithValue("title", article.getTitle())
            .hasFieldOrPropertyWithValue("content", article.getContent())
            .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 조회 -> 게시글 없을 시 예외 발생")
    @Test
    void select_article_exception() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> articleService.getArticle(articleId));

        // Then
        assertThat(t)
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("해당하는 게시글이 존재하지 않습니다. + articleId : " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 생성")
    @Test
    void create_article() {
        //given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        //when
        articleService.saveArticle(dto);

        //then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 수정")
    @Test
    void updateArticle() {
        //given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("new title", "new content", "#tag");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        //when
        articleService.updateArticle(dto);

        //then
        assertThat(article)
            .hasFieldOrPropertyWithValue("title", dto.title())
            .hasFieldOrPropertyWithValue("content", dto.content())
            .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글 삭제")
    @Test
    void deleteArticle() {
        //given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        //when
        articleService.deleteArticle(1L);

        //then
        then(articleRepository).should().deleteById(articleId);
    }


    private UserAccount createUserAccount() {
        return UserAccount.of(
            "song",
            "password",
            "song@eemail.com",
            "Song",
            null
        );
    }

    private Article createArticle() {
        return Article.of(
            createUserAccount(),
            "title",
            "content",
            "#java"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
            createUserAccountDto(),
            title,
            content,
            hashtag,
            LocalDateTime.now(),
            "song",
            LocalDateTime.now(),
            "song");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
            1L,
            "song",
            "password",
            "song@email.com",
            "Song",
            "This is memo",
            LocalDateTime.now(),
            "song",
            LocalDateTime.now(),
            "song"
        );
    }
}