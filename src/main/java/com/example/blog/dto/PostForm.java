package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章新建/编辑表单。
 */
public class PostForm {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题过长")
    private String title;

    @NotBlank(message = "正文不能为空")
    private String content;

    @Size(max = 500, message = "摘要过长")
    private String excerpt;

    private boolean published;

    private boolean featured;

    private List<Long> tagIds = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
