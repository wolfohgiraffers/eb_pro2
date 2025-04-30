package com.ohgiraffers.ebpro.service;

import com.ohgiraffers.ebpro.dto.MenuDTO;
import com.ohgiraffers.ebpro.entity.Menu;
import com.ohgiraffers.ebpro.repository.MenuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private final ModelMapper mapper;
    private final MenuRepository menuRepository;

    @Autowired
    public MenuService(ModelMapper mapper, MenuRepository menuRepository) {
        this.mapper = mapper;
        this.menuRepository = menuRepository;
    }

    public MenuDTO findMenuByMenuCode(int menuCode) {

        Menu selectedMenu = menuRepository.findById(menuCode).orElseThrow(IllegalArgumentException::new);

        return mapper.map(selectedMenu, MenuDTO.class);
    }
}
