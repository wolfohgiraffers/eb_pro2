package com.ohgiraffers.ebpro.controller;

import com.ohgiraffers.ebpro.dto.MenuDTO;
import com.ohgiraffers.ebpro.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("health")
    public String healthCheck() {
        
        /* 반환 문자열 수정 */
        return "eb_pro is online and healthy";
    }

    @GetMapping("menus/{menuCode}")
    public MenuDTO findMenuByMenuCode(@PathVariable("menuCode") int menuCode) {
        
        /* 한줄 코드로 수정 */
        return menuService.findMenuByMenuCode(menuCode);
    }
}













