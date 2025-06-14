package com.quitsmoking.platform.quitsmoking.api;

import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/user")
@SecurityRequirement(name = "api")
public class UserAPI {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity getListUser() {
        List<Account> accounts = userService.getListUser();
        return ResponseEntity.ok(accounts);
    }

//    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
//    @GetMapping("/profile")
//    public ResponseEntity<Account> getOwnProfile(org.springframework.security.core.Authentication authentication) {
//        String email = authentication.getName(); // Lấy từ JWT
//        Account user = userService.getByEmail(email);
//        return ResponseEntity.ok(user);
//    }
//
//    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
//    @PutMapping("/profile")
//    public ResponseEntity<Account> updateOwnProfile(@RequestBody Account updatedInfo, org.springframework.security.core.Authentication auth) {
//        String email = auth.getName();
//        Account updated = userService.updateByEmail(email, updatedInfo);
//        return ResponseEntity.ok(updated);
//    }
//
//    @PreAuthorize("hasAuthority('ADMIN')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
//        userService.deleteUserById(id);
//        return ResponseEntity.noContent().build();
//    }

}
