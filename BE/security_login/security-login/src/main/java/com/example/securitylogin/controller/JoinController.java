package com.example.securitylogin.controller;

import com.example.securitylogin.dto.form.JoinDto;
import com.example.securitylogin.service.form.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {
    private final JoinService joinService;
    @PostMapping("/join")
    public String joinProc(@ModelAttribute JoinDto joinDto) {
        joinService.join(joinDto);
        return "ok";
    }
}
