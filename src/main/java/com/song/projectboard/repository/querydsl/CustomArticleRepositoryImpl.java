package com.song.projectboard.repository.querydsl;

import com.song.projectboard.domain.Article;
import com.song.projectboard.domain.QArticle;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CustomArticleRepositoryImpl extends QuerydslRepositorySupport implements CustomArticleRepository {

    public CustomArticleRepositoryImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllHashtags() {
        QArticle article = QArticle.article;

        return from(article)
            .distinct()
            .select(article.hashtag)
            .where(article.hashtag.isNotNull())
            .fetch();
    }
}
