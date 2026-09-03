package com.example.blog.controller;

import com.example.blog.dto.CommentForm;
import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import com.example.blog.model.User;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import com.example.blog.service.TagService;
import com.example.blog.service.UserService;
import com.example.blog.util.BlogUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台页面：首页、文章详情、标签、搜索、评论。
 */
@Controller
public class HomeController {

    private final PostService postService;
    private final TagService tagService;
    private final CommentService commentService;
    private final UserService userService;

    public HomeController(PostService postService,
                          TagService tagService,
                          CommentService commentService,
                          UserService userService) {
        this.postService = postService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredPosts", postService.featuredPosts());
        model.addAttribute("posts", postService.publishedPosts());
        model.addAttribute("tags", tagService.findAll());
        return "index";
    }

    @GetMapping("/post/{slug}")
    public String postDetail(@PathVariable String slug, Model model) {
        Post post = postService.findPublishedBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
        preparePostModel(model, post);
        return "post";
    }

    @PostMapping("/post/{slug}/comment")
    public String addComment(@PathVariable String slug,
                             CommentForm commentForm,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        Post post = postService.findPublishedBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));

        if (commentForm.getContent() == null || commentForm.getContent().isBlank()) {
            redirectAttributes.addFlashAttribute("commentError", "评论内容不能为空");
            return "redirect:/post/" + slug;
        }
        User author = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        commentService.add(post, author, commentForm.getContent().strip());
        redirectAttributes.addFlashAttribute("commentSuccess", "评论发布成功");
        return "redirect:/post/" + slug;
    }

    @GetMapping("/tag/{slug}")
    public String tag(@PathVariable String slug, Model model) {
        Tag tag = tagService.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "标签不存在"));
        model.addAttribute("tag", tag);
        model.addAttribute("posts", postService.findByTag(slug));
        model.addAttribute("tags", tagService.findAll());
        return "tag";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false, defaultValue = "") String q, Model model) {
        List<Post> posts = postService.search(q);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> item = new HashMap<>();
            item.put("post", post);
            item.put("highlightedTitle", BlogUtils.highlight(post.getTitle(), q));
            item.put("highlightedExcerpt", BlogUtils.highlight(post.getExcerpt(), q));
            results.add(item);
        }
        model.addAttribute("q", q);
        model.addAttribute("results", results);
        model.addAttribute("tags", tagService.findAll());
        return "search";
    }

    private void preparePostModel(Model model, Post post) {
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.listByPost(post.getId()));
        model.addAttribute("readingMinutes", postService.readingMinutes(post));

        List<Map<String, Object>> related = new ArrayList<>();
        for (Post r : postService.related(post, 3)) {
            Map<String, Object> item = new HashMap<>();
            item.put("post", r);
            item.put("readingMinutes", postService.readingMinutes(r));
            related.add(item);
        }
        model.addAttribute("related", related);
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute("commentForm", new CommentForm());
    }
}
