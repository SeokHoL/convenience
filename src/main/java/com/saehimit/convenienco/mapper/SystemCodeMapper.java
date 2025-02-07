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



    int checkCodeValueDuplicateInIndex(@Param("codeIndex") String codeIndex, @Param("codeValue") String codeValue);

    int checkCodeNameDuplicateInIndex(@Param("codeIndex") String codeIndex, @Param("codeName") String codeName);

    int checkCodeValueDuplicateInIndexExcludeSelf(
            @Param("codeIndex") String codeIndex,
            @Param("codeValue") String codeValue,
            @Param("codeId") int codeId
    );

    int checkCodeNameDuplicateInIndexExcludeSelf(
            @Param("codeIndex") String codeIndex,
            @Param("codeName") String codeName,
            @Param("codeId") int codeId
    );

    List<SystemCodeDto> getCodesByIndex(@Param("codeIndex") String codeIndex);

    List<SystemCodeDto> getAllCodes();
}

