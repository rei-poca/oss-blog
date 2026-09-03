package com.example.blog.config;

import com.example.blog.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 向所有模板注入当前登录用户名与是否管理员，用于导航与条件渲染。
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            model.addAttribute("currentUsername", authentication.getName());
            boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", admin);
        }
    }
}
