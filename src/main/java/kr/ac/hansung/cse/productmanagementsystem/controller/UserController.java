package kr.ac.hansung.cse.productmanagementsystem.controller;

import kr.ac.hansung.cse.productmanagementsystem.entity.MyUser;
import kr.ac.hansung.cse.productmanagementsystem.service.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {

    private final CustomUserDetailsService userService;

    public UserController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ProductManagementApp/users")
    public String listUsers(Model model) {
        List<MyUser> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user_list";
    }
}
