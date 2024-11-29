package fan.summer.hmoneta.webEntity.req.menus;

import lombok.Data;

@Data
public class WebMenusReq {
    private Long parentId;
    private String name;
    private String routeName;
}
