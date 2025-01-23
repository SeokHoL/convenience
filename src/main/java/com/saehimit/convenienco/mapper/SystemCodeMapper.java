package com.saehimit.convenienco.mapper;

import com.saehimit.convenienco.dto.SystemCodeDto;
import com.saehimit.convenienco.dto.UsersDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemCodeMapper {
    List<SystemCodeDto> findAll();
    List<SystemCodeDto> findByIndex(String codeIndex);

    List<SystemCodeDto> searchCodes(@Param("codeIndex") String codeIndex,
                                    @Param("codeValue") String codeValue,
                                    @Param("codeName") String codeName);

    void addCode(SystemCodeDto systemCodeDto);
    void updateCode(SystemCodeDto systemCodeDto);



    void deleteCode(@Param("codeIds") List<Integer> codeIds);

    int checkCodeValueDuplicate(String codeValue);

    int checkCodeNameDuplicate(String codeName);

    boolean isCodeValueDuplicateExcludeSelf(String codeValue, int codeId);

    boolean isCodeNameDuplicateExcludeSelf(String codeName, int codeId);
}
