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

    public static String excerpt(String body, int maxLength) {
        if (maxLength < 1) {
            throw new IllegalArgumentException("maxLength must be at least 1");
        }
        if (body.length() <= maxLength) {
            return body;
        }
        final String span = body.substring(0, maxLength);
        int cutAt = maxLength;
        for (int i = span.length() - 1; i >= 0; i--) {
            if (Character.isWhitespace(span.charAt(i))) {
                cutAt = i;
                break;
            }
        }
        final String kept = body.substring(0, cutAt);
        int end = kept.length();
        while (end > 0 && Character.isWhitespace(kept.charAt(end - 1))) {
            end--;
        }
        return kept.substring(0, end) + "...";
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
