package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.mapper.SystemCodeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemCodeService {
    private final SystemCodeMapper mapper;

    public SystemCodeService(SystemCodeMapper mapper) {
        this.mapper = mapper;
    }

    public List<SystemCodeDto> getAllCodes() {
        return mapper.findAll();
    }

    public List<SystemCodeDto> getCodesByIndex(String codeIndex) {
        return mapper.findByIndex(codeIndex);
    }

    public void addCode(SystemCodeDto systemCodeDto) {
        mapper.addCode(systemCodeDto);
    }

    public void updateCode(SystemCodeDto systemCodeDto) {
        mapper.updateCode(systemCodeDto);
    }
}