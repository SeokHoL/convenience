package com.saehimit.convenienco.mapper;

import com.saehimit.convenienco.dto.ProductMasterDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMasterMapper {
    List<ProductMasterDto> findAll(); // 전체 데이터 조회

    List<ProductMasterDto> search(  // 조건 검색
                                    @Param("productType") String productType,
                                    @Param("productCode") String productCode,
                                    @Param("productName") String productName
    );

    void addProduct(ProductMasterDto productMasterDto); // 데이터 추가

    void updateProduct(ProductMasterDto productMasterDto); // 데이터 수정

    void deleteProducts(@Param("ids") List<Integer> ids); // 여러 데이터 삭제

    int checkProductNameDuplicate(String productName);

    int checkProductCodeDuplicate(String productCode);

    ProductMasterDto findById(@Param("masterId") int masterId);

    ProductMasterDto findByProductCode(@Param("productCode") String productCode);
}