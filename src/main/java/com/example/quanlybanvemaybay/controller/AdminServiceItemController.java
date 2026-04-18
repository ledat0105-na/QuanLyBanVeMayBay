package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.FlightServiceItem;
import com.example.quanlybanvemaybay.repository.FlightServiceItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceItemController {

    private final FlightServiceItemRepository repository;

    public AdminServiceItemController(FlightServiceItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", repository.findAll());
        return "admin/manage-services";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute FlightServiceItem item) {
        if (item.getIsActive() == null) item.setIsActive(false);
        repository.save(item);
        return "redirect:/admin/services";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin/services";
    }
}
