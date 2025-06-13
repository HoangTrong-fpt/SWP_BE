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
@RequestMapping("api/user")
@SecurityRequirement(name = "api")
public class UserAPI {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Account>> getListUser() {
        List<Account> accounts = userService.getListUser();
        return ResponseEntity.ok(accounts);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Account> getUserById(@PathVariable Long id) {
        Account account = userService.getUserById(id);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Account> createUser(@Valid @RequestBody Account account) {
        Account newAccount = userService.createUser(account);
        return ResponseEntity.ok(newAccount);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateUser(@PathVariable Long id, @Valid @RequestBody Account account) {
        Account updatedAccount = userService.updateUser(id, account);
        return ResponseEntity.ok(updatedAccount);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
