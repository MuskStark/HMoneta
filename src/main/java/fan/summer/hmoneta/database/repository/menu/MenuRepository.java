package fan.summer.hmoneta.database.repository.menu;

import fan.summer.hmoneta.database.entity.menu.WebMenus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface MenuRepository extends JpaRepository<WebMenus,Long> {

    @Query(value = """
select m from WebMenus m where m.parentId=0
""")
    Set<WebMenus> findAllFatherNode();
    @Query(value = """
select m from WebMenus m where m.parentId !=0
""")
    Set<WebMenus> findAllSubNode();
}
