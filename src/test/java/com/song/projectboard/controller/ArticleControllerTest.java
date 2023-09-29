package com.song.projectboard.controller;

import com.song.projectboard.config.SecurityConfig;
import com.song.projectboard.domain.type.SearchType;
import com.song.projectboard.dto.ArticleDto;
import com.song.projectboard.dto.ArticleWithCommentsDto;
import com.song.projectboard.dto.UserAccountDto;
import com.song.projectboard.service.ArticleService;
import com.song.projectboard.service.PaginationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("게시글 View 컨트롤러 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private PaginationService paginationService;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 게시글 리스트 페이지")
    @Test
    void articles_list_view() throws Exception {
        //given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        //when & then
        mvc.perform(get("/articles"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attributeExists("paginationNumbers"));

        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 페이지 - 검색 적용")
    @Test
    void articles_list_view_search() throws Exception {
        //given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        given(articleService.searchArticles(eq(searchType), eq(searchValue), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        //when & then
        mvc.perform(get("/articles")
                .queryParam("searchType", searchType.name())
                .queryParam("searchValue", searchValue))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/index"))
            .andExpect(model().attributeExists("articles"))
            .andExpect(model().attributeExists("searchTypes"));

        then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
    }

    @DisplayName("[view][GET] 해시 태그 검색 페이지 - 검색어 없을 때")
    @Test
    void articles_list_view_search_using_hashtag_no_keyword() throws Exception {
        //given
        given(articleService.searchArticlesHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());

        //when & then
        mvc.perform(get("/articles/search-hashtag"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/search-hashtag"))
            .andExpect(model().attribute("articles", Page.empty()))
            .andExpect(model().attributeExists("hashtags"))
            .andExpect(model().attributeExists("paginationNumbers"));

        then(articleService).should().searchArticlesHashtag(eq(null), any(Pageable.class));
    }

    @DisplayName("[view][GET] 해시 태그 검색 페이지 - 검색어 존재 할 때")
    @Test
    void articles_list_view_search_using_hashtag() throws Exception {
        //given
        String searchTag = "#java";
        given(articleService.searchArticlesHashtag(eq(searchTag), any(Pageable.class))).willReturn(Page.empty());

        //when & then
        mvc.perform(get("/articles/search-hashtag")
                .queryParam("searchValue", searchTag))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/search-hashtag"))
            .andExpect(model().attribute("articles", Page.empty()))
            .andExpect(model().attributeExists("hashtags"))
            .andExpect(model().attributeExists("paginationNumbers"));

        then(articleService).should().searchArticlesHashtag(eq(searchTag), any(Pageable.class));
    }

    @DisplayName("[view][GET] 게시글 상세 페이지")
    @Test
    void articles_select_view() throws Exception {
        //given
        Long articleId = 1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentDto());

        //when & then
        mvc.perform(get("/articles/" + articleId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("articles/detail"))
            .andExpect(model().attributeExists("article"))
            .andExpect(model().attributeExists("articleComments"));

        then(articleService).should().getArticle(articleId);
    }

    private ArticleWithCommentsDto createArticleWithCommentDto() {
        return ArticleWithCommentsDto.of(
            1L,
            createUserAccountDto(),
            Set.of(),
            "title",
            "content",
            "#tag1",
            LocalDateTime.now(),
            "song",
            LocalDateTime.now(),
            "song"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
            "song",
            "song1234",
            "song@email.com",
            "Song",
            "bla bla bla",
            LocalDateTime.now(),
            "song",
            LocalDateTime.now(),
            "song"
        );
    }

}