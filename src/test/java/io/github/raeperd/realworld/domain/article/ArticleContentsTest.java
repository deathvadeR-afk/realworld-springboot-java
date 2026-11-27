package io.github.raeperd.realworld.domain.article;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.raeperd.realworld.domain.article.ArticleUpdateRequest.builder;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ArticleContentsTest {


    @Test
    void when_updateArticle_with_no_update_field_request_expect_not_changed() {
        final var articleContents = sampleArticleContents();
        final var emptyUpdateRequest = builder().build();

        articleContents.updateArticleContentsIfPresent(emptyUpdateRequest);

        assertThatEqualArticleContents(articleContents, sampleArticleContents());
    }

    @Test
    void when_updateArticle_with_all_field_expect_changed(@Mock ArticleTitle titleToUpdate) {
        final var articleContents = sampleArticleContents();
        final var fullUpdateRequest = builder().titleToUpdate(titleToUpdate)
                .descriptionToUpdate("descriptionToUpdate")
                .bodyToUpdate("bodyToUpdate")
                .build();

        articleContents.updateArticleContentsIfPresent(fullUpdateRequest);

        assertThat(articleContents.getTitle()).isEqualTo(titleToUpdate);
        assertThat(articleContents.getDescription()).isEqualTo("descriptionToUpdate");
        assertThat(articleContents.getBody()).isEqualTo("bodyToUpdate");
    }

    @DisplayName("[QP-SMGRADLE-2-1] returns a body unchanged when it is shorter than the limit")
    @Test
    void when_excerpt_with_body_shorter_than_limit_expect_body_unchanged() {
        final var body = "The quick brown fox jumps over the lazy dog";

        final var result = ArticleContents.excerpt(body, 100);

        assertThat(result).isEqualTo(body);
    }

    @DisplayName("[QP-SMGRADLE-2-2] returns a body unchanged when its length equals the limit")
    @Test
    void when_excerpt_with_body_length_equal_to_limit_expect_body_unchanged() {
        final var body = "The quick brown fox";

        final var result = ArticleContents.excerpt(body, 19);

        assertThat(result).isEqualTo(body);
    }

    @DisplayName("[QP-SMGRADLE-2-3] cuts at the last whitespace within the limit and appends an ellipsis")
    @Test
    void when_excerpt_with_body_longer_than_limit_expect_cut_at_last_whitespace_with_ellipsis() {
        final var body = "The quick brown fox jumps over the lazy dog";

        final var result = ArticleContents.excerpt(body, 12);

        assertThat(result).isEqualTo("The quick...");
    }

    @DisplayName("[QP-SMGRADLE-2-4] removes trailing whitespace before appending the ellipsis")
    @Test
    void when_excerpt_with_double_space_before_limit_expect_no_space_before_ellipsis() {
        final var body = "The quick  brown fox jumps over the lazy dog";

        final var result = ArticleContents.excerpt(body, 12);

        assertThat(result).isEqualTo("The quick...");
    }

    @DisplayName("[QP-SMGRADLE-2-5] cuts at the limit and appends an ellipsis when there is no whitespace within the limit")
    @Test
    void when_excerpt_with_no_whitespace_expect_cut_at_limit_with_ellipsis() {
        final var body = "abcdefghijklmnop";

        final var result = ArticleContents.excerpt(body, 5);

        assertThat(result).isEqualTo("abcde...");
    }

    @DisplayName("[QP-SMGRADLE-2-6] rejects a maximum length of zero as invalid input")
    @Test
    void when_excerpt_with_zero_max_length_expect_illegal_argument_exception() {
        final var body = "The quick brown fox jumps over the lazy dog";

        assertThatThrownBy(() -> ArticleContents.excerpt(body, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private ArticleContents sampleArticleContents() {
        return new ArticleContents("description", ArticleTitle.of("title"), "body", emptySet());
    }

    private void assertThatEqualArticleContents(ArticleContents left, ArticleContents right) {
        assertThat(equalsArticleContents(left, right)).isTrue();
    }

    private boolean equalsArticleContents(ArticleContents left, ArticleContents right) {
        if (!left.getTitle().equals(right.getTitle())) {
            return false;
        }
        if (!left.getDescription().equals(right.getDescription())) {
            return false;
        }
        if (!left.getBody().equals(right.getBody())) {
            return false;
        }
        return left.getTags().equals(right.getTags());
    }

}