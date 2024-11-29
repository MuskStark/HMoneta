package fan.summer.hmoneta.controller;


import cn.hutool.core.bean.BeanUtil;
import fan.summer.hmoneta.database.entity.menu.WebMenus;
import fan.summer.hmoneta.service.menu.MenuService;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.menus.WebMenusReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/10/4
 */
@RestController
@RequestMapping("/hm/menus")
public class MenusController {

    private final MenuService menuService;

    @Autowired
    public MenusController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/modify")
    public ApiRestResponse<Object> modifyMenus(@RequestBody WebMenusReq req){
        menuService.modifyMenus(BeanUtil.copyProperties(req, WebMenus.class));
        return ApiRestResponse.success();
    }

    @GetMapping("/query")
    public ApiRestResponse<Map<String, List<WebMenus>>> queryMenus(){
        Map<WebMenus, List<WebMenus>> menuList = menuService.getMenuList();
        Map<String, List<WebMenus>> menuTree = new HashMap<>();
        for (WebMenus key : menuList.keySet()){
            menuTree.put(key.getName(), menuList.get(key));
        }
        return ApiRestResponse.success(menuTree);}

}
