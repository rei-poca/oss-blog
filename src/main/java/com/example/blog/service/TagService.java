package com.example.blog.service;

import com.example.blog.model.Tag;
import com.example.blog.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 标签查询。
 */
@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }
}
