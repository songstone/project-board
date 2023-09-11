package com.song.projectboard.service;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.type.SearchType;
import com.song.projectboard.dto.ArticleDto;
import com.song.projectboard.dto.ArticleWithCommentsDto;
import com.song.projectboard.repository.ArticleRepository;
import com.song.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if(searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE ->
                articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT ->
                articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG ->
                articleRepository.findByHashtag(searchKeyword, pageable).map(ArticleDto::from);
            case ID ->
                articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME ->
                articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
            .map(ArticleWithCommentsDto::from)
            .orElseThrow(
                () -> new EntityNotFoundException("해당하는 게시글이 존재하지 않습니다. + articleId : " + articleId)
            );
    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            if(dto.title() != null) { article.setTitle(dto.title()); }
            if(dto.content() != null) { article.setContent(dto.content()); }
            if(dto.hashtag() != null) { article.setHashtag(dto.hashtag()); }
        } catch (EntityNotFoundException e) {
            log.warn("해당하는 게시글이 없습니다. 수정에 실패 했습니다. articleId : {}", dto.id());
        }
    }

    public void deleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }
}
