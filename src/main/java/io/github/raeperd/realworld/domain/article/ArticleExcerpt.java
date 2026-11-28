package io.github.raeperd.realworld.domain.article;

public final class ArticleExcerpt {

    private static final String ELLIPSIS = "...";

    private ArticleExcerpt() {
    }

    public static String excerpt(String body, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Excerpt limit must be at least 1: " + limit);
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
        return body.substring(0, cut).stripTrailing() + ELLIPSIS;
    }
}
