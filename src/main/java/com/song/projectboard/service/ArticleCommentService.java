package com.song.projectboard.service;

import com.song.projectboard.domain.ArticleComment;
import com.song.projectboard.dto.ArticleCommentDto;
import com.song.projectboard.repository.ArticleCommentRepository;
import com.song.projectboard.repository.ArticleRepository;
import com.song.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId).stream()
            .map(ArticleCommentDto::fromEntity)
            .toList(); // 수정 불가 리스트
    }

    public void saveArticleComment(ArticleCommentDto dto) {
        try {
            articleCommentRepository.save(dto.toEntity(articleRepository.getReferenceById(dto.articleId())));
        } catch (EntityNotFoundException e) {
            log.warn("해당하는 게시글이 존재하지 않습니다. articleId : {}", dto.articleId());
        }
    }


    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if(dto.content() != null) articleComment.setContent(dto.content());
        } catch (EntityNotFoundException e) {
            log.warn("해당하는 댓글이 존재하지 않습니다. articleCommentId : {}", dto.id());
        }
    }

    public void deleteArticleComment(Long articleCommentId) {
        articleCommentRepository.deleteById(articleCommentId);
    }
}
