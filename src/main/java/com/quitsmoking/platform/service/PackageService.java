package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.PackageRequest;
import com.quitsmoking.platform.dto.PackageResponse;
import com.quitsmoking.platform.entity.Package;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import com.quitsmoking.platform.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    public List<PackageResponse> getAll() {
        return packageRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PackageResponse getById(Long id) {
        Package pack = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalRequestException("Package not found"));
        return toResponse(pack);
    }

    public PackageResponse create(PackageRequest request) {
        Package pack = toEntity(request);
        return toResponse(packageRepository.save(pack));
    }

    public PackageResponse update(Long id, PackageRequest request) {
        Package pack = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalRequestException("Package not found"));

        pack.setCode(request.getCode());
        pack.setName(request.getName());
        pack.setDescription(request.getDescription());
        pack.setPrice(request.getPrice());
        pack.setDuration(request.getDuration());
        pack.setCoachSupport(request.getCoachSupport());

        return toResponse(packageRepository.save(pack));
    }

    public void delete(Long id) {
        Package pack = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalRequestException("Package not found"));
        packageRepository.delete(pack);
    }

    private PackageResponse toResponse(Package pack) {
        PackageResponse res = new PackageResponse();
        res.setId(pack.getId());
        res.setCode(pack.getCode());
        res.setName(pack.getName());
        res.setDescription(pack.getDescription());
        res.setPrice(pack.getPrice());
        res.setDuration(pack.getDuration());
        res.setCoachSupport(pack.getCoachSupport());
        return res;
    }

    private Package toEntity(PackageRequest req) {
        Package pack = new Package();
        pack.setCode(req.getCode());
        pack.setName(req.getName());
        pack.setDescription(req.getDescription());
        pack.setPrice(req.getPrice());
        pack.setDuration(req.getDuration());
        pack.setCoachSupport(req.getCoachSupport());
        return pack;
    }
}
