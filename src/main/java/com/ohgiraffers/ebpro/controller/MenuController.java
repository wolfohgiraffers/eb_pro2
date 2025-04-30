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
        return "eb_proj is fine! dev/feature/cr!!!13";
    }

    @GetMapping("menus/{menuCode}")
    public MenuDTO findMenuByMenuCode(@PathVariable("menuCode") int menuCode) {
        MenuDTO returnMenu = menuService.findMenuByMenuCode(menuCode);
        return returnMenu;
    }
}













