package com.example.blog.util;

import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * 文本展示相关的纯函数工具：摘要、阅读时长、搜索高亮。
 */
public final class BlogUtils {

    private BlogUtils() {
    }

    /** 截取摘要。 */
    public static String excerpt(String content, int maxLength) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String text = content.strip();
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "…";
    }

    /** 按中文阅读速度约 400 字/分钟估算阅读时长（不足 1 分钟按 1 分钟）。 */
    public static int readingMinutes(String content) {
        if (content == null || content.isBlank()) {
            return 1;
        }
        int chars = content.length();
        return Math.max(1, (int) Math.ceil(chars / 400.0));
    }

    /**
     * 对文本做 HTML 转义后，将命中的关键词用 &lt;mark&gt; 高亮。
     * 先转义再包裹，避免把用户输入当作 HTML 注入。
     */
    public static String highlight(String text, String keyword) {
        String safe = HtmlUtils.htmlEscape(text == null ? "" : text);
        if (keyword == null || keyword.isBlank()) {
            return safe;
        }
        String safeKeyword = HtmlUtils.htmlEscape(keyword);
        Pattern pattern = Pattern.compile(Pattern.quote(safeKeyword), Pattern.CASE_INSENSITIVE);
        return pattern.matcher(safe).replaceAll("<mark>$0</mark>");
    }
}
