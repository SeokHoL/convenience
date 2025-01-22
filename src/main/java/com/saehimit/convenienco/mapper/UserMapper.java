package com.saehimit.convenienco.mapper;

import com.saehimit.convenienco.dto.UsersDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    void createAdmin(UsersDto user);  // XML에 정의된 쿼리를 호출

    UsersDto findByLoginId(String loginId); // login_id로 사용자 조회

    void saveUser(UsersDto userDto);


    UsersDto findUserByLoginId(String loginId); // 사용자 검색
    UsersDto findUserByEmail(String email);
    UsersDto findUserByPhoneNumber(String phoneNumber);

    List<UsersDto> searchUsers(@Param("loginId") String loginId,
                               @Param("username") String username,
                               @Param("branch") String branch);

    void deleteUsers(@Param("loginIds") List<Long> loginIds);

    void updateUser(UsersDto userDto);



    void incrementLoginFailCount(String loginId); // 로그인 실패 횟수 증가

    void lockAccount(String loginId); // 계정 잠금
    void unlockAccount(String loginId);

    void resetLoginFailCount(String loginId); // 로그인 실패 횟수 초기화


}