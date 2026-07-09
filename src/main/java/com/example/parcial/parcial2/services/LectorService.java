package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.LectorRequestDto;
import com.example.parcial.parcial2.domain.entities.Lector;
import com.example.parcial.parcial2.repositories.LectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LectorService {

    private final LectorRepository lectorRepository;

    public LectorService(LectorRepository lectorRepository) {
        this.lectorRepository = lectorRepository;
    }

    public Lector registerLector(LectorRequestDto dto) {
        Lector lector = new Lector();
        lector.setName(dto.getName().trim().toLowerCase());
        lector.setLastname(dto.getLastname().trim().toLowerCase());
        lector.setDui(dto.getDui());
        lector.setEmail(buildEmail(dto.getName(), dto.getLastname()));
        lector.setActive(true);
        return lectorRepository.save(lector);
    }

    public Lector getLectorById(UUID id) {
        return lectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lector not found"));
    }

    public List<Lector> getAllLectors() {
        return lectorRepository.findAll();
    }

    public Lector updateLector(UUID id, LectorRequestDto dto) {
        Lector lector = lectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lector not found"));
        lector.setName(dto.getName().trim().toLowerCase());
        lector.setLastname(dto.getLastname().trim().toLowerCase());
        lector.setDui(dto.getDui());
        lector.setEmail(buildEmail(dto.getName(), dto.getLastname()));
        return lectorRepository.save(lector);
    }

    public void deleteLector(UUID id) {
        Lector lector = lectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lector not found"));
        lector.setActive(false);
        lectorRepository.save(lector);
    }

    private String buildEmail(String name, String lastname) {
        String normalizedName = name.trim().toLowerCase().replace(" ", "");
        String normalizedLastname = lastname.trim().toLowerCase().replace(" ", "");
        return normalizedName + "." + normalizedLastname + "@library.com";
    }
}
