package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.TeamMember;
import com.example.quanlybanvemaybay.repository.TeamMemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin/team")
public class AdminTeamController {

    private final TeamMemberRepository repository;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/team/";

    public AdminTeamController(TeamMemberRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("members", repository.findAll());
        return "admin/manage-team";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("name") String name,
                       @RequestParam("role") String role,
                       @RequestParam(value = "isActive", defaultValue = "false") Boolean isActive,
                       @RequestParam(value = "imageUrlStr", required = false) String imageUrlStr,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        TeamMember member;
        if (id != null) {
            member = repository.findById(id).orElse(new TeamMember());
        } else {
            member = new TeamMember();
        }
        member.setName(name);
        member.setRole(role);
        member.setIsActive(isActive);

        if (imageUrlStr != null && !imageUrlStr.trim().isEmpty()) {
            member.setImageUrl(imageUrlStr.trim());
        } else if (imageFile != null && !imageFile.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName));
            member.setImageUrl("/uploads/team/" + fileName);
        }
        repository.save(member);
        return "redirect:/admin/team";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin/team";
    }
}
