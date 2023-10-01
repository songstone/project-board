package com.song.projectboard.controller;

import com.song.projectboard.domain.constant.FormStatus;
import com.song.projectboard.domain.constant.SearchType;
import com.song.projectboard.dto.UserAccountDto;
import com.song.projectboard.dto.request.ArticleRequest;
import com.song.projectboard.dto.response.ArticleResponse;
import com.song.projectboard.dto.response.ArticleWithCommentResponse;
import com.song.projectboard.dto.security.BoardPrincipal;
import com.song.projectboard.service.ArticleService;
import com.song.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;

    private final PaginationService paginationService;

    @GetMapping
    public String articles(@RequestParam(required = false) SearchType searchType,
                           @RequestParam(required = false) String searchValue,
                           @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                           ModelMap map) {
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::fromDto);
        List<Integer> paginationNumbers = paginationService.getPaginationNumbers(articles.getNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationNumbers", paginationNumbers);
        map.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(ModelMap map, @PathVariable Long articleId) {
        ArticleWithCommentResponse articleResponse = ArticleWithCommentResponse.fromDto(articleService.getArticleWithComments(articleId));
        map.addAttribute("article", articleResponse);
        map.addAttribute("articleComments", articleResponse.articleCommentResponses());

        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(
        @RequestParam(required = false) String searchValue,
        @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesHashtag(searchValue, pageable).map(ArticleResponse::fromDto);
        List<Integer> paginationNumbers = paginationService.getPaginationNumbers(articles.getNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();

        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationNumbers", paginationNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @PostMapping ("/form")
    public String postNewArticle(
        @AuthenticationPrincipal BoardPrincipal boardPrincipal,
        ArticleRequest articleRequest
    ) {
        articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.fromDto(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping ("/{articleId}/form")
    public String updateArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ArticleRequest articleRequest
    ) {
        articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(
        @PathVariable Long articleId,
        @AuthenticationPrincipal BoardPrincipal boardPrincipal
        ) {
        articleService.deleteArticle(articleId, boardPrincipal.getUsername());

        return "redirect:/articles";
    }
}
