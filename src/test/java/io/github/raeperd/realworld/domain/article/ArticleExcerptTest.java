package io.github.raeperd.realworld.domain.article;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArticleExcerptTest {

    private static final String QUICK_FOX = "The quick brown fox jumps over the lazy dog";

    @Test
    @DisplayName("[QP-SMGRADLE-5-1] returns a body unchanged when it is shorter than the limit")
    void when_body_shorter_than_limit_expect_body_unchanged() {
        var excerpt = ArticleExcerpt.excerpt(QUICK_FOX, 100);

        assertThat(excerpt).isEqualTo("The quick brown fox jumps over the lazy dog");
    }

    @Test
    @DisplayName("[QP-SMGRADLE-5-2] returns a body unchanged when its length equals the limit exactly")
    void when_body_length_equals_limit_expect_body_unchanged() {
        var excerpt = ArticleExcerpt.excerpt(QUICK_FOX, 43);

        assertThat(excerpt).isEqualTo("The quick brown fox jumps over the lazy dog");
    }

    @Test
    @DisplayName("[QP-SMGRADLE-5-3] cuts at the last whitespace within the limit and appends an ellipsis")
    void when_body_longer_than_limit_expect_cut_at_last_whitespace_and_ellipsis() {
        var excerpt = ArticleExcerpt.excerpt(QUICK_FOX, 12);

        assertThat(excerpt).isEqualTo("The quick...");
    }

    @Test
    @DisplayName("[QP-SMGRADLE-5-4] removes trailing whitespace left by the cut before appending the ellipsis")
    void when_cut_leaves_trailing_whitespace_expect_whitespace_removed_before_ellipsis() {
        var excerpt = ArticleExcerpt.excerpt("The quick   brown fox", 12);

        assertThat(excerpt).isEqualTo("The quick...");
    }

    @Test
    @DisplayName("[QP-SMGRADLE-5-5] cuts at the limit when no whitespace falls within it")
    void when_no_whitespace_within_limit_expect_cut_at_limit_and_ellipsis() {
        var excerpt = ArticleExcerpt.excerpt("abcdefghijklmnop", 4);

        assertThat(excerpt).isEqualTo("abcd...");
    }

    @Test
    @DisplayName("[QP-SMGRADLE-5-6] accepts a limit of exactly 1 and returns the cut excerpt")
    void when_limit_is_one_expect_cut_excerpt_returned() {
        var excerpt = ArticleExcerpt.excerpt("abc", 1);

        assertThat(excerpt).isEqualTo("a...");
    }

    @Test
    @DisplayName("[QP-SMGRADLE-5-7] rejects a limit of 0 as invalid input and returns no excerpt")
    void when_limit_is_zero_expect_invalid_input_and_no_excerpt() {
        assertThatThrownBy(() -> ArticleExcerpt.excerpt(QUICK_FOX, 0))
                .isInstanceOf(Exception.class);
    }
}
