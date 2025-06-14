package com.quitsmoking.platform.quitsmoking.service;

import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    public List<Account> getListUser() {
        return authenticationRepository.findAll();
    }

//    // ✅ Lấy user theo username (dùng trong JWT principal)
//    public Account getByUsername(String username) {
//        Account user = authenticationRepository.findAccountByUsername(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//        return user;
//    }
//
//    // ✅ Cập nhật thông tin user theo username
//    public Account updateByUsername(String username, Account updatedData) {
//        Account existing = getByUsername(username);
//        existing.setFullName(updatedData.getFullName());
//        existing.setPhone(updatedData.getPhone());
//        existing.setBirthDate(updatedData.getBirthDate());
//        // Không cập nhật username/password/role ở đây
//        return authenticationRepository.save(existing);
//    }
//
//    // ✅ Admin xoá user theo ID
//    public void deleteUserById(Long id) {
//        if (!authenticationRepository.existsById(id)) {
//            throw new IllegalArgumentException("User not found with id: " + id);
//        }
//        authenticationRepository.deleteById(id);
//    }
//
//    // ✅ Admin lấy user theo ID
//    public Account getUserById(Long id) {
//        return authenticationRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
//    }
}
