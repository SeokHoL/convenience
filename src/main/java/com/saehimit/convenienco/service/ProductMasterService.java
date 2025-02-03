package com.saehimit.convenienco.service;

import com.saehimit.convenienco.dto.ProductMasterDto;
import com.saehimit.convenienco.mapper.ProductMasterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductMasterService {

    private final ProductMasterMapper productMasterMapper;





    public List<ProductMasterDto> getAllProducts() {
        return productMasterMapper.findAll();
    }

    public List<ProductMasterDto> searchProducts(String productType, String productCode, String productName) {
        return productMasterMapper.search(productType, productCode, productName);
    }

    public void addProduct(ProductMasterDto productMasterDto) {
        productMasterMapper.addProduct(productMasterDto);
    }

    public void updateProduct(ProductMasterDto productMasterDto) {
        // 현재 수정하려는 데이터의 기존 정보 가져오기
        ProductMasterDto existingProduct = productMasterMapper.findById(productMasterDto.getMasterId());

        // 입력된 품목 코드가 기존 데이터와 다를 경우 중복 검사 실행
        if (!existingProduct.getProductCode().equals(productMasterDto.getProductCode()) &&
                isProductCodeDuplicate(productMasterDto.getProductCode())) {
            throw new IllegalArgumentException("중복된 품목 코드가 존재합니다.");
        }

        // 입력된 품목명이 기존 데이터와 다를 경우 중복 검사 실행
        if (!existingProduct.getProductName().equals(productMasterDto.getProductName()) &&
                isProductNameDuplicate(productMasterDto.getProductName())) {
            throw new IllegalArgumentException("중복된 품목명이 존재합니다.");
        }

        // 업데이트 수행
        productMasterMapper.updateProduct(productMasterDto);
    }


    public void deleteProducts(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("삭제할 항목이 없습니다.");
        }
        try {
            productMasterMapper.deleteProducts(ids);
        } catch (Exception e) {
            throw new RuntimeException("삭제 중 오류가 발생했습니다.", e);
        }
    }


    public void addProductWithValidation(ProductMasterDto productMasterDto) {
        if (isProductCodeDuplicate(productMasterDto.getProductCode())){
            throw new IllegalArgumentException("중복된 품목코드가 존재합니다.");
        }
        if (isProductNameDuplicate(productMasterDto.getProductName())){
            throw new IllegalArgumentException("중복된 품목명이 존재합니다.");
        }

        productMasterMapper.addProduct(productMasterDto);
    }


    public boolean isProductCodeDuplicate(String productCode) {
       Integer count = productMasterMapper.checkProductCodeDuplicate(productCode);
       return count !=null && count >0 ;
    }

    public   boolean isProductNameDuplicate(String productName) {
        Integer count = productMasterMapper.checkProductNameDuplicate(productName);
        return count != null && count > 0;
    }


    public ProductMasterDto findByProductCode(String productCode) {
        return productMasterMapper.findByProductCode(productCode);
    }

}
