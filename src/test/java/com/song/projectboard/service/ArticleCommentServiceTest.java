package com.song.projectboard.service;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.ArticleComment;
import com.song.projectboard.dto.ArticleCommentDto;
import com.song.projectboard.repository.ArticleCommentRepository;
import com.song.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("게시글 댓글 로직 테스트")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService articleCommentService;

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleCommentRepository articleCommentRepository;

    @DisplayName("게시글 댓글 리스트")
    @Test
    void searchArticleComments() {

        Long articleId = 1L;

        given(articleRepository.findById(articleId)).willReturn(
            Optional.of(Article.of("title", "content", "#hashtag" )));

        List<ArticleCommentDto> articleComments = articleCommentService.searchArticleComments(articleId);

        assertThat(articleComments).isNotNull();
        then(articleRepository).should().findById(articleId);
    }
    @DisplayName("댓글 등록")
    @Test
    void givenArticleCommentInfo_whenSavingArticleComment_thenSavesArticleComment() {
        // Given
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // When
        articleCommentService.saveArticleComment(ArticleCommentDto.of("comment", LocalDateTime.now(), "song", LocalDateTime.now(), "song"));

        // Then
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }


}