package fan.summer.hmoneta.service.menu;

import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.menu.WebMenus;
import fan.summer.hmoneta.database.repository.menu.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/10/2
 */
@Service
public class MenuService {

    private final MenuRepository menuRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Map<WebMenus, List<WebMenus>> getMenuList(){
        Map<WebMenus, List<WebMenus>> webMenusTree = new HashMap<>();
        Set<WebMenus> menus = menuRepository.findAllFatherNode();
        Set<WebMenus> subMenus = menuRepository.findAllSubNode();
        for (WebMenus menu : menus) {
            webMenusTree.put(menu, new ArrayList<>());
            for (WebMenus subMenu : subMenus){
                if (Objects.equals(menu.getId(), subMenu.getParentId())){
                    webMenusTree.get(menu).add(subMenu);
                }
            }
        }
        return webMenusTree;
    }

    public void modifyMenus(WebMenus webMenus){
        // 检查是否为二级菜单
        if(webMenus.getParentId() !=0){
            Optional<WebMenus> webMenusOptional = menuRepository.findById(webMenus.getParentId());
            // 检查父菜单是否存在
            if(webMenusOptional.isEmpty()){
                throw new BusinessException(BusinessExceptionEnum.WEB_MENUS_NOT_FIND_FATHER_NODE);
            }
        }
        menuRepository.save(webMenus);}
}
