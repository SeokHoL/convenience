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
    public List<SystemCodeDto> searchCodes(String codeIndex, String codeValue, String codeName) {
        return mapper.searchCodes(codeIndex, codeValue, codeName);
    }


    public void deleteCode(List<Integer> codeIds) {
        mapper.deleteCode(codeIds);
    }

    public boolean isCodeValueDuplicate(String codeValue) {
        return mapper.checkCodeValueDuplicate(codeValue) > 0;
    }

    public boolean isCodeNameDuplicate(String codeName) {
        return mapper.checkCodeNameDuplicate(codeName) > 0;
    }


    public void addCodeWithValidation(SystemCodeDto systemCodeDto) {
        if (isCodeValueDuplicate(systemCodeDto.getCodeValue())) { //true 일때만 실행
            throw new IllegalArgumentException("중복된 공통코드가 존재합니다.");
        }
        if (isCodeNameDuplicate(systemCodeDto.getCodeName())) {  //true 일때만 실행
            throw new IllegalArgumentException("중복된 공통코드명이 존재합니다.");
        }
        mapper.addCode(systemCodeDto); // 중복 확인 후 추가
    }
}