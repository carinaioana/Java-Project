package com.example.lab4.service;

import com.example.lab4.entity.Pack;
import com.example.lab4.repository.PackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PackService {

    private final PackRepository packRepository;

    // Constructor injection
    public PackService(PackRepository packRepository) {
        this.packRepository = packRepository;
    }

    @Transactional
    public Pack createPack(Pack pack) {
        return packRepository.save(pack);
    }

    public Optional<Pack> findById(Long id) {
        return packRepository.findById(id);
    }

    public List<Pack> findAll() {
        return packRepository.findAll();
    }

    public List<Pack> findByYear(Integer year) {
        return packRepository.findByYear(year);
    }

    public List<Pack> findByYearAndSemester(Integer year, String semester) {
        return packRepository.findByYearAndSemester(year, semester);
    }

    public Optional<Pack> findByName(String name) {
        return packRepository.findByName(name);
    }

    @Transactional
    public Pack updatePack(Pack pack) {
        return packRepository.save(pack);
    }

    @Transactional
    public void deletePack(Long id) {
        packRepository.deleteById(id);
    }

    public long count() {
        return packRepository.count();
    }

    public boolean existsById(Long id) {
        return packRepository.existsById(id);
    }
}

