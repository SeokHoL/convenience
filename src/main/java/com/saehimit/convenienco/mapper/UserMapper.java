package com.saehimit.convenienco.mapper;

import com.saehimit.convenienco.dto.UsersDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void createAdmin(UsersDto user); // XML에 정의된 쿼리를 호출

    UsersDto findByLoginId(String loginId); // login_id로 사용자 조회
}