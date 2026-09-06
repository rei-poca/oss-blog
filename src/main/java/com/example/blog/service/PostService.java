package com.example.blog.service;

import com.example.blog.dto.PostForm;
import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.TagRepository;
import com.example.blog.util.BlogUtils;
import com.example.blog.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文章读写、搜索与相关推荐（自主扩展功能）。
 */
@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public List<Post> publishedPosts() {
        return postRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<Post> featuredPosts() {
        return postRepository.findByPublishedTrueAndFeaturedTrueOrderByCreatedAtDesc();
    }

    public List<Post> findAllForAdmin() {
        return postRepository.findAllByOrderByUpdatedAtDesc();
    }

    public List<Post> findByTag(String tagSlug) {
        return postRepository.findByPublishedTrueAndTagsSlugOrderByCreatedAtDesc(tagSlug);
    }

    public Optional<Post> findPublishedBySlug(String slug) {
        return postRepository.findBySlugAndPublishedTrue(slug);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    /** 本地全文搜索：命中标题或正文。 */
    public List<Post> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return postRepository.searchPublished(keyword.strip());
    }

    /** 本地全文搜索 + 按标签过滤（tagSlug 为空时不过滤）。 */
    public List<Post> search(String keyword, String tagSlug) {
        List<Post> results = search(keyword);
        if (tagSlug == null || tagSlug.isBlank()) {
            return results;
        }
        Set<Long> taggedIds = postRepository
                .findByPublishedTrueAndTagsSlugOrderByCreatedAtDesc(tagSlug)
                .stream()
                .map(Post::getId)
                .collect(Collectors.toSet());
        return results.stream()
                .filter(p -> taggedIds.contains(p.getId()))
                .toList();
    }

    @Transactional
    public Post create(PostForm form, User author) {
        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setSlug(SlugUtil.slugify(form.getTitle(), postRepository));
        post.setContent(form.getContent());
        post.setExcerpt(resolveExcerpt(form));
        post.setPublished(form.isPublished());
        post.setFeatured(form.isFeatured());
        post.setAuthor(author);
        post.setTags(resolveTags(form.getTagIds()));
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public Post update(Post post, PostForm form) {
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setExcerpt(resolveExcerpt(form));
        post.setPublished(form.isPublished());
        post.setFeatured(form.isFeatured());
        post.setTags(resolveTags(form.getTagIds()));
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public void delete(Post post) {
        postRepository.delete(post);
    }

    /** 估算阅读时长（分钟）。 */
    public int readingMinutes(Post post) {
        return BlogUtils.readingMinutes(post.getContent());
    }

    /**
     * 自主扩展功能：按“共享标签数量 + 时间”生成相关推荐。
     */
    @Transactional(readOnly = true)
    public List<Post> related(Post post, int limit) {
        Set<Long> tagIds = post.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        if (tagIds.isEmpty()) {
            return List.of();
        }
        return postRepository.findByPublishedTrueOrderByCreatedAtDesc().stream()
                .filter(p -> !p.getId().equals(post.getId()))
                .sorted(Comparator
                        .comparingInt((Post p) -> (int) p.getTags().stream()
                                .map(Tag::getId).filter(tagIds::contains).count())
                        .reversed()
                        .thenComparing(Post::getCreatedAt, Comparator.reverseOrder()))
                .limit(limit)
                .toList();
    }

    private String resolveExcerpt(PostForm form) {
        if (form.getExcerpt() != null && !form.getExcerpt().isBlank()) {
            return form.getExcerpt().strip();
        }
        return BlogUtils.excerpt(form.getContent(), 150);
    }

    private Set<Tag> resolveTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(tagRepository.findAllById(tagIds));
    }
}
