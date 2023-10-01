package com.song.projectboard.service;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.ArticleComment;
import com.song.projectboard.domain.UserAccount;
import com.song.projectboard.dto.ArticleCommentDto;
import com.song.projectboard.dto.UserAccountDto;
import com.song.projectboard.repository.ArticleCommentRepository;
import com.song.projectboard.repository.ArticleRepository;
import com.song.projectboard.repository.UserAccountRepository;
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
import static org.mockito.BDDMockito.*;

@DisplayName("게시글 댓글 로직 테스트")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService articleCommentService;

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleCommentRepository articleCommentRepository;
    @Mock
    private UserAccountRepository userAccountRepository;

    @DisplayName("게시글 댓글 리스트")
    @Test
    void search_article_articleComments() {
        //given
        Long articleId = 1L;
        ArticleComment expected = createArticleComment("content");
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));

        //when
        List<ArticleCommentDto> actual = articleCommentService.searchArticleComments(articleId);

        //then
        assertThat(actual)
            .hasSize(1)
            .first().hasFieldOrPropertyWithValue("content", expected.getContent());
        then(articleCommentRepository).should().findByArticle_Id(articleId);
        ;
    }

    @DisplayName("댓글 등록")
    @Test
    void save_articleComment() {
        // Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(userAccountRepository.findByUserId(dto.userAccountDto().userId())).willReturn(Optional.of(createUserAccount()));
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // When
        articleCommentService.saveArticleComment(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).should().findByUserId(dto.userAccountDto().userId());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("댓글 수정")
    @Test
    void update_articleComment() {
        //given
        String oldContent = "content";
        String updatedContent = "댓글";
        ArticleComment articleComment = createArticleComment(oldContent);
        ArticleCommentDto dto = createArticleCommentDto(updatedContent);
        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

        //when
        articleCommentService.updateArticleComment(dto);

        //then
        assertThat(articleComment.getContent())
            .isNotEqualTo(oldContent)
            .isEqualTo(updatedContent);
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("댓글 삭제")
    @Test
    void delete_articleComment() {
        //given
        Long articleCommentId = 1L;
        String userId = "song";
        willDoNothing().given(articleCommentRepository).deleteByIdAndUserAccount_UserId(articleCommentId, userId);

        //when
        articleCommentService.deleteArticleComment(articleCommentId, userId);

        //then
        then(articleCommentRepository).should().deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }


    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
            1L,
            1L,
            createUserAccountDto(),
            content,
            LocalDateTime.now(),
            "song",
            LocalDateTime.now(),
            "song"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
            "song",
            "password",
            "song@mail.com",
            "Song",
            "This is memo",
            LocalDateTime.now(),
            "song",
            LocalDateTime.now(),
            "song"
        );
    }

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
            Article.of(createUserAccount(), "title", "content", "hashtag"),
            createUserAccount(),
            content
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
            "song",
            "password",
            "song@email.com",
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
}