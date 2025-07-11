package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.PackageRequest;
import com.quitsmoking.platform.dto.PackageResponse;
import com.quitsmoking.platform.service.PackageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package")
@SecurityRequirement(name = "api")
@Tag(name = "Package")
public class PackageAPI {

    @Autowired
    private PackageService packageService;

    @GetMapping
    public ResponseEntity<List<PackageResponse>> getAll() {

        List<PackageResponse> response = packageService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageResponse> getById(@PathVariable Long id) {

        PackageResponse response = packageService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ✅ Chỉ ADMIN được phép tạo gói mới
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PackageResponse> create(@RequestBody PackageRequest request) {

        PackageResponse response = packageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Chỉ ADMIN được phép cập nhật thông tin gói
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PackageResponse> update(@PathVariable Long id,
                                                  @RequestBody PackageRequest request) {

        PackageResponse response = packageService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // ✅ Chỉ ADMIN được phép xóa gói
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        packageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
