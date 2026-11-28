package io.github.raeperd.realworld.domain.article;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    @Test
    void when_excerpt_with_body_within_limit_expect_body_unchanged() throws Throwable {
        final var excerpt = callExcerpt("The quick brown fox jumps over the lazy dog", 100);

        assertThat(excerpt).isEqualTo("The quick brown fox jumps over the lazy dog");
    }

    @Test
    void when_excerpt_with_body_length_equal_to_limit_expect_body_unchanged() throws Throwable {
        final var excerpt = callExcerpt("The quick", 9);

        assertThat(excerpt).isEqualTo("The quick");
    }

    @Test
    void when_excerpt_with_body_longer_than_limit_expect_cut_at_last_whitespace_and_ellipsis() throws Throwable {
        final var excerpt = callExcerpt("The quick brown fox jumps over the lazy dog", 12);

        assertThat(excerpt).isEqualTo("The quick...");
    }

    @Test
    void when_excerpt_with_trailing_whitespace_before_cut_expect_trailing_whitespace_removed() throws Throwable {
        final var excerpt = callExcerpt("The quick  brown fox jumps over the lazy dog", 12);

        assertThat(excerpt).isEqualTo("The quick...");
    }

    @Test
    void when_excerpt_with_no_whitespace_within_limit_expect_cut_at_limit_and_ellipsis() throws Throwable {
        final var excerpt = callExcerpt("abcdefghijklmnop", 5);

        assertThat(excerpt).isEqualTo("abcde...");
    }

    @Test
    void when_excerpt_with_zero_limit_expect_rejected_as_invalid_input() throws Throwable {
        final var excerpt = excerptMethod();

        assertThatThrownBy(() -> invokeExcerpt(excerpt, "The quick brown fox", 0))
                .isInstanceOf(Throwable.class);
    }

    @Test
    void when_excerpt_with_limit_of_one_expect_one_character_and_ellipsis() throws Throwable {
        final var excerpt = callExcerpt("The quick brown fox", 1);

        assertThat(excerpt).isEqualTo("T...");
    }

    // Looked up by reflection so this file compiles before the excerpt operation exists.
    private static Method excerptMethod() throws NoSuchMethodException {
        return ArticleContents.class.getMethod("excerpt", String.class, int.class);
    }

    private static String callExcerpt(String body, int limit) throws Throwable {
        return invokeExcerpt(excerptMethod(), body, limit);
    }

    private static String invokeExcerpt(Method excerpt, String body, int limit) throws Throwable {
        try {
            return (String) excerpt.invoke(null, body, limit);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
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