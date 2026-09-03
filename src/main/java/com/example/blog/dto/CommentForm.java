package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 评论表单。
 */
public class CommentForm {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论过长")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
