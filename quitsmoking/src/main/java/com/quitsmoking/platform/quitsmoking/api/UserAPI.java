package com.quitsmoking.platform.quitsmoking.api;

import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/user")
@SecurityRequirement(name = "api")
public class UserAPI {


    @Autowired
    private UserService userService;

        @PreAuthorize("hasAuthority('ADMIN')")
        @GetMapping
        public ResponseEntity getListUser(){
            List<Account> accounts = userService.getListUser();
            return ResponseEntity.ok(accounts);
        }

//        @PostMapping
//        public ResponseEntity createNewUser(@Valid @RequestBody User user){
//
//            return ResponseEntity.ok(user);
//        }
//
//        @GetMapping("{id}")
//        public void getUserById(@PathVariable int id){
//        }


}
