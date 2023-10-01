package com.song.projectboard.repository;

import com.song.projectboard.config.JpaConfig;
import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testdb")
@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class) // Auditing 기능 on
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(
        @Autowired ArticleRepository articleRepository,
        @Autowired ArticleCommentRepository articleCommentRepository,
        @Autowired UserAccountRepository userAccountRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void selectTest() {
        //given

        //when
        List<Article> articles = articleRepository.findAll();

        //then
        assertThat(articles)
            .isNotNull()
            .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void insertTest() {
        //given
        long beforeCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.findById(1L).orElseThrow();
        //when
        articleRepository.save(Article.of(userAccount, "new article", "content", "#spring"));

        //then
        assertThat(articleRepository.count()).isEqualTo(beforeCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void updateTest() {
        //given
        Article article = articleRepository.findById(1L).orElseThrow();
        article.setHashtag("#spring");

        //when
        Article saved = articleRepository.saveAndFlush(article);

        //then
        assertThat(saved.getHashtag()).isEqualTo("#spring");
    }

    @DisplayName("delete 테스트")
    @Test
    void deleteTest() {
        //given
        Article article = articleRepository.findById(1L).orElseThrow();
        long beforeArticleCount = articleRepository.count();
        long beforeArticleCommentCount = articleCommentRepository.count();

        int commentCount = article.getArticleComments().size();

        //when
        articleRepository.delete(article);

        //then
        assertThat(articleRepository.count()).isEqualTo(beforeArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(beforeArticleCommentCount - commentCount);
    }

    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("song");
        }
    }
}