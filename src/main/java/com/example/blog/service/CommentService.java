package com.example.blog.service;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论读写。
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment add(Post post, User author, String content) {
        return commentRepository.save(new Comment(content, post, author));
    }

    public List<Comment> listByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
