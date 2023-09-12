package com.song.projectboard.controller;

import com.song.projectboard.domain.type.SearchType;
import com.song.projectboard.dto.response.ArticleResponse;
import com.song.projectboard.dto.response.ArticleWithCommentResponse;
import com.song.projectboard.service.ArticleService;
import com.song.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                           ModelMap map)
    {
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::fromDto);
        List<Integer> paginationNumbers = paginationService.getPaginationNumbers(articles.getNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationNumbers", paginationNumbers);

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(ModelMap map, @PathVariable Long articleId) {
        ArticleWithCommentResponse articleResponse = ArticleWithCommentResponse.fromDto(articleService.getArticle(articleId));
        map.addAttribute("article", articleResponse);
        map.addAttribute("articleComments", articleResponse.articleCommentResponses());

        return "articles/detail";
    }
}
