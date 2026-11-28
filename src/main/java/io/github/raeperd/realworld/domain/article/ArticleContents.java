package io.github.raeperd.realworld.domain.article;

import io.github.raeperd.realworld.domain.article.tag.Tag;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;

@Embeddable
public class ArticleContents {

    @Embedded
    private ArticleTitle title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String body;

    @JoinTable(name = "articles_tags",
            joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = false))
    @ManyToMany(fetch = EAGER, cascade = PERSIST)
    private Set<Tag> tags = new HashSet<>();

    public ArticleContents(String description, ArticleTitle title, String body, Set<Tag> tags) {
        this.description = description;
        this.title = title;
        this.body = body;
        this.tags = tags;
    }

    protected ArticleContents() {
    }

    public ArticleTitle getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBody() {
        return body;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    void updateArticleContentsIfPresent(ArticleUpdateRequest updateRequest) {
        updateRequest.getTitleToUpdate().ifPresent(titleToUpdate -> title = titleToUpdate);
        updateRequest.getDescriptionToUpdate().ifPresent(descriptionToUpdate -> description = descriptionToUpdate);
        updateRequest.getBodyToUpdate().ifPresent(bodyToUpdate -> body = bodyToUpdate);
    }

    public static String excerpt(String body, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be at least 1");
        }
        if (body.length() <= limit) {
            return body;
        }
        int cut = limit;
        for (int i = limit - 1; i >= 0; i--) {
            if (Character.isWhitespace(body.charAt(i))) {
                cut = i;
                break;
            }
        }
        return body.substring(0, cut).stripTrailing() + "...";
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
