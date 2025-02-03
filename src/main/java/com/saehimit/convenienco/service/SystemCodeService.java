package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.mapper.SystemCodeMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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




    public void updateCode(SystemCodeDto systemCodeDto) {
        // 현재 사용자 정보 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 수정 날짜와 수정자 설정
        systemCodeDto.setModifiedBy(currentUsername);  // 수정자 설정
        systemCodeDto.setLastModified(LocalDate.now()); // 현재 날짜 설정

        mapper.updateCode(systemCodeDto); // Mapper 호출
    }


    public List<SystemCodeDto> searchCodes(String codeIndex, String codeValue, String codeName) {
        return mapper.searchCodes(codeIndex, codeValue, codeName);
    }


    public void deleteCode(List<Integer> codeIds) {
        mapper.deleteCode(codeIds);
    }

    public boolean isCodeValueDuplicate(String codeValue) {
        Integer count = mapper.checkCodeValueDuplicate(codeValue);
        return count != null && count > 0;
    }


    public boolean isCodeNameDuplicate(String codeName) {
        Integer count = mapper.checkCodeNameDuplicate(codeName);
        return count != null && count > 0;
    }

    public void addCode(SystemCodeDto systemCodeDto) {
        mapper.addCode(systemCodeDto);
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
    public void updateCodeWithValidation(SystemCodeDto systemCodeDto) {
        // 현재 사용자 정보 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        systemCodeDto.setModifiedBy(currentUsername);  // 수정자 설정
        systemCodeDto.setLastModified(LocalDate.now()); // 현재 날짜 설정

        // 공통코드 중복 확인 (자신 제외)
        if (mapper.isCodeValueDuplicateExcludeSelf(systemCodeDto.getCodeValue(), systemCodeDto.getCodeId())) {
            throw new IllegalArgumentException("중복된 공통코드가 존재합니다.");
        }

        // 공통코드명 중복 확인 (자신 제외)
        if (mapper.isCodeNameDuplicateExcludeSelf(systemCodeDto.getCodeName(), systemCodeDto.getCodeId())) {
            throw new IllegalArgumentException("중복된 공통코드명이 존재합니다.");
        }

        // 중복 확인이 통과되면 업데이트
        mapper.updateCode(systemCodeDto);
    }







}