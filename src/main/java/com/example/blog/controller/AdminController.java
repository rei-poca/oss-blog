package com.example.blog.controller;

import com.example.blog.dto.PostForm;
import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import com.example.blog.model.User;
import com.example.blog.service.PostService;
import com.example.blog.service.TagService;
import com.example.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 管理端：文章列表、新建、编辑、删除。
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PostService postService;
    private final TagService tagService;
    private final UserService userService;

    public AdminController(PostService postService, TagService tagService, UserService userService) {
        this.postService = postService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("posts", postService.findAllForAdmin());
        return "admin/dashboard";
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("allTags", tagService.findAll());
        return "admin/post-form";
    }

    @PostMapping("/posts/new")
    public String create(@Valid @ModelAttribute("postForm") PostForm form,
                         BindingResult bindingResult,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", tagService.findAll());
            return "admin/post-form";
        }
        User author = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        Post post = postService.create(form, author);
        redirectAttributes.addFlashAttribute("adminSuccess",
                post.isPublished() ? "文章已发布" : "文章已保存为草稿");
        return "redirect:/admin";
    }

    @GetMapping("/posts/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
        PostForm form = toForm(post);
        model.addAttribute("postForm", form);
        model.addAttribute("postId", id);
        model.addAttribute("allTags", tagService.findAll());
        return "admin/post-form";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("postForm") PostForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id);
            model.addAttribute("allTags", tagService.findAll());
            return "admin/post-form";
        }
        postService.update(post, form);
        redirectAttributes.addFlashAttribute("adminSuccess", "文章已更新");
        return "redirect:/admin";
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.findById(id).ifPresent(postService::delete);
        redirectAttributes.addFlashAttribute("adminSuccess", "文章已删除");
        return "redirect:/admin";
    }

    private PostForm toForm(Post post) {
        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());
        form.setExcerpt(post.getExcerpt());
        form.setPublished(post.isPublished());
        form.setFeatured(post.isFeatured());
        form.setTagIds(post.getTags().stream().map(Tag::getId).toList());
        return form;
    }
}
