package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.mapper.SystemCodeMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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




    public List<SystemCodeDto> searchCodes(String codeIndex, String codeValue, String codeName) {
        return mapper.searchCodes(codeIndex, codeValue, codeName);
    }


    public void deleteCode(List<Integer> codeIds) {
        mapper.deleteCode(codeIds);
    }

    public boolean isCodeValueDuplicateInIndex(String codeIndex, String codeValue) {
        Integer count = mapper.checkCodeValueDuplicateInIndex(codeIndex, codeValue);
        return count != null && count > 0;
    }

    public boolean isCodeNameDuplicateInIndex(String codeIndex, String codeName) {
        Integer count = mapper.checkCodeNameDuplicateInIndex(codeIndex, codeName);
        return count != null && count > 0;
    }

    public void addCode(SystemCodeDto systemCodeDto) {
        mapper.addCode(systemCodeDto);
    }


    public void addCodeWithValidation(SystemCodeDto systemCodeDto) {
        if (isCodeValueDuplicateInIndex(systemCodeDto.getCodeIndex(), systemCodeDto.getCodeValue())) {
            throw new IllegalArgumentException("같은 유형 내에서 중복된 공통코드가 존재합니다.");
        }
        if (isCodeNameDuplicateInIndex(systemCodeDto.getCodeIndex(), systemCodeDto.getCodeName())) {
            throw new IllegalArgumentException("같은 유형 내에서 중복된 공통코드명이 존재합니다.");
        }
        mapper.addCode(systemCodeDto);
    }
    public boolean isCodeValueDuplicateInIndexExcludeSelf(String codeIndex, String codeValue, int codeId) {
        Integer count = mapper.checkCodeValueDuplicateInIndexExcludeSelf(codeIndex, codeValue, codeId);
        return count != null && count > 0;
    }

    public boolean isCodeNameDuplicateInIndexExcludeSelf(String codeIndex, String codeName, int codeId) {
        Integer count = mapper.checkCodeNameDuplicateInIndexExcludeSelf(codeIndex, codeName, codeId);
        return count != null && count > 0;
    }

    public void updateCodeWithValidation(SystemCodeDto systemCodeDto) {
        // 현재 사용자 정보 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        systemCodeDto.setModifiedBy(currentUsername);
        systemCodeDto.setLastModified(LocalDate.now());

        // 자신을 제외한 동일 유형(codeIndex) 내 중복 검사
        if (isCodeValueDuplicateInIndexExcludeSelf(systemCodeDto.getCodeIndex(), systemCodeDto.getCodeValue(), systemCodeDto.getCodeId())) {
            throw new IllegalArgumentException("같은 유형 내에서 중복된 공통코드가 존재합니다.");
        }
        if (isCodeNameDuplicateInIndexExcludeSelf(systemCodeDto.getCodeIndex(), systemCodeDto.getCodeName(), systemCodeDto.getCodeId())) {
            throw new IllegalArgumentException("같은 유형 내에서 중복된 공통코드명이 존재합니다.");
        }

        // 중복이 없으면 수정 진행
        mapper.updateCode(systemCodeDto);
    }


    public Map<String, Map<String, String>> getCommonCodeMap() {
        List<SystemCodeDto> codeList = mapper.getAllCodes();

        return codeList.stream()
                .collect(Collectors.groupingBy(
                        SystemCodeDto::getCodeIndex, // 컬럼명 그대로 사용
                        Collectors.toMap(SystemCodeDto::getCodeValue, SystemCodeDto::getCodeName)
                ));
    }







}