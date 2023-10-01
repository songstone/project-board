package com.song.projectboard.service;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.UserAccount;
import com.song.projectboard.domain.constant.SearchType;
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
import java.util.List;

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
            return articleRepository.findAll(pageable).map(ArticleDto::fromEntity);
        }

        return switch (searchType) {
            case TITLE ->
                articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::fromEntity);
            case CONTENT ->
                articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::fromEntity);
            case HASHTAG ->
                articleRepository.findByHashtag(searchKeyword, pageable).map(ArticleDto::fromEntity);
            case ID ->
                articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::fromEntity);
            case NICKNAME ->
                articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::fromEntity);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
            .map(ArticleWithCommentsDto::fromEntity)
            .orElseThrow(
                () -> new EntityNotFoundException("해당하는 게시글이 존재하지 않습니다. + articleId : " + articleId)
            );
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
            .map(ArticleDto::fromEntity)
            .orElseThrow(
                () -> new EntityNotFoundException("해당하는 게시글이 존재하지 않습니다. + articleId : " + articleId)
            );
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.findByUserId(dto.userAccountDto().userId())
            .orElseThrow(
                () -> new EntityNotFoundException("해당하는 회원이 존재하지 않습니다. + userId : " + dto.userAccountDto().userId())
            );
        articleRepository.save(dto.toEntity(userAccount));
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.findByUserId(dto.userAccountDto().userId())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 존재하지 않습니다. + userId : " + dto.userAccountDto().userId()));

            if(article.getUserAccount().equals(userAccount)) {
                if(dto.title() != null) { article.setTitle(dto.title()); }
                if(dto.content() != null) { article.setContent(dto.content()); }
                if(dto.hashtag() != null) { article.setHashtag(dto.hashtag()); }
            }
        } catch (EntityNotFoundException e) {
            log.warn("게시글을 수정 할 수 없습니다. - {}", e.getMessage());
        }
    }

    public void deleteArticle(Long articleId, String userId) {
        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesHashtag(String hashtag, Pageable pageable) {
        if(hashtag == null || hashtag.isBlank()) {
            return Page.empty(pageable);
        }

        return articleRepository.findByHashtag(hashtag, pageable).map(ArticleDto::fromEntity);
    }

    public List<String> getHashtags() {
        return articleRepository.findAllHashtags();
    }
}
