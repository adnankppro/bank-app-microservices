package com.adnan.bankapp.authentication.controller;

import com.adnan.bankapp.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    @PatchMapping("/approve/{id}")
    public ResponseEntity<Void> approve(@PathVariable Long id){
        userService.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<Void> reject(@PathVariable Long id){
        userService.reject(id);
        return ResponseEntity.noContent().build();
    }

}
