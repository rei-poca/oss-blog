package com.example.blog.util;

import com.example.blog.repository.PostRepository;

/**
 * 根据标题生成唯一、URL 友好的 slug。
 */
public final class SlugUtil {

    private SlugUtil() {
    }

    public static String slugify(String title, PostRepository repository) {
        String base = title == null
                ? ""
                : title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        if (base.isBlank()) {
            base = "post";
        }
        String slug = base;
        int i = 2;
        while (repository.existsBySlug(slug)) {
            slug = base + "-" + i;
            i++;
        }
        return slug;
    }
}
