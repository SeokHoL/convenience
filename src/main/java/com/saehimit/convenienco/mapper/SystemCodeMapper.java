package com.saehimit.convenienco.mapper;

import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.dto.UsersDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemCodeMapper {
    List<SystemCodeDto> findAll();
    List<SystemCodeDto> findByIndex(String codeIndex);
    void addCode(SystemCodeDto systemCodeDto);
    void updateCode(SystemCodeDto systemCodeDto);



}
