package com.ohgiraffers.ebpro.dto;

import lombok.Data;

@Data
public class MenuDTO {
    private int menuCode;
    private String menuName;
    private String menuPrice;
    private String categoryCode;
    private String orderableStatus;
}
