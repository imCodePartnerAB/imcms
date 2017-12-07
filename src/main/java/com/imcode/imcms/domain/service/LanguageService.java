package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.LanguageDTO;

import java.util.List;

public interface LanguageService {
    /**
     * Get language by it's two-letter ISO-639-1 code like "en" or "sv"
     *
     * @param code ISO-639-1 code
     * @return language DTO
     */
    LanguageDTO findByCode(String code);

    List<LanguageDTO> getAll();
}
