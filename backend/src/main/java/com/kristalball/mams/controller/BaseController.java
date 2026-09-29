package com.kristalball.mams.controller;

import com.kristalball.mams.model.Base;
import com.kristalball.mams.repository.BaseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Gives the list of all bases (used in the dropdowns on the frontend)
@RestController
@RequestMapping("/api")
public class BaseController {

    private final BaseRepository baseRepository;

    public BaseController(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    @GetMapping("/bases")
    public List<Base> getAllBases() {
        return baseRepository.findAll();
    }
}
