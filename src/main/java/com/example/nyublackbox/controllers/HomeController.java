package com.example.nyublackbox.controllers;

import com.example.nyublackbox.models.Account;
import com.example.nyublackbox.models.Post;
import com.example.nyublackbox.services.AccountService;
import com.example.nyublackbox.services.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    private PostService postService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String home(Model model) {
        List<Post> posts = postService.getAll();
        model.addAttribute("posts", posts);
        return "home";
    }
    @GetMapping("/login")
    public String getLogin() {

        return "login";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {

        Account account = new Account();
        model.addAttribute("account", account);
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute Account account) {
        accountService.save(account);
        return "redirect:/";
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model) {

        // find post by id
        Optional<Post> optionalPost = this.postService.getById(id);

        // if post exists put it in model
        // once the model is added model carries the post
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            model.addAttribute("post", post);
            return "post";
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id, Post post, BindingResult result, Model model) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());
            existingPost.setDepartment(post.getDepartment());
            existingPost.setUserUpdate(post.getUserUpdate());
            existingPost.setAdminUpdate(post.getAdminUpdate());

            postService.save(existingPost);
        }

        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(Model model, Principal principal) {

        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }

        Optional<Account> optionalAccount = accountService.findOneByEmail(authUsername);
        if (optionalAccount.isPresent()) {
            Post post = new Post();
            post.setAccount(optionalAccount.get());
            model.addAttribute("post", post);
            return "post_new";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(@ModelAttribute Post post, Principal principal) {
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        if (post.getAccount().getEmail().compareToIgnoreCase(authUsername) < 0) {
            System.err.println("error");
            // our account email on the Post not equal to current logged in account!
        }
        post.setUserUpdate("Raised");
        post.setAdminUpdate("Pending");
        postService.save(post);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getPostForEdit(@PathVariable Long id, Model model) throws JsonProcessingException {

        // find post by id
        Optional<Post> optionalPost = postService.getById(id);



        // if post exist put it in model
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();



            model.addAttribute("post", post);

            return "post_edit";
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/{id}/user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getPostUserupdate(@PathVariable Long id, Model model) throws JsonProcessingException {

        // find post by id
        Optional<Post> optionalPost = postService.getById(id);



        // if post exist put it in model
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();



            model.addAttribute("post", post);

            return "post_user";
        } else {
            return "404";
        }


    }

    @GetMapping("/posts/{id}/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getPostadminUpdate(@PathVariable Long id, Model model) throws JsonProcessingException {

        // find post by id
        Optional<Post> optionalPost = postService.getById(id);



        // if post exist put it in model
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();



            model.addAttribute("post", post);

            return "post_admin";
        } else {
            return "404";
        }


    }


    @GetMapping("/posts/{id}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deletePost(@PathVariable Long id) {

        // find post by id
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            postService.delete(post);
            return "redirect:/";
        } else {
            return "404";
        }
    }

}
