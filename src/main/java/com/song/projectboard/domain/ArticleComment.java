package com.song.projectboard.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
    @Index(columnList = "content"),
    @Index(columnList = "createdAt"),
    @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    @ToString.Exclude
    private Article article;

    @Setter
    @Column(nullable = false, length = 500)
    private String content;

    public ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    public static ArticleComment of(Article article, String comment) {
        return new ArticleComment(article, comment);
    }
}
