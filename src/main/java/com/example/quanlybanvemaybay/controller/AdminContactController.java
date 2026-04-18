package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.ContactMessage;
import com.example.quanlybanvemaybay.repository.ContactMessageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/contacts")
public class AdminContactController {

    private final ContactMessageRepository repository;

    public AdminContactController(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("messages", repository.findAllByOrderByCreatedAtDesc());
        return "admin/manage-contacts";
    }

    @PostMapping("/mark-read/{id}")
    public String markAsRead(@PathVariable Long id) {
        ContactMessage msg = repository.findById(id).orElse(null);
        if (msg != null) {
            msg.setIsRead(true);
            repository.save(msg);
        }
        return "redirect:/admin/contacts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin/contacts";
    }
}
