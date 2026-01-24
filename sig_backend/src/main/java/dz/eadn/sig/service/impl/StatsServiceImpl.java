package dz.eadn.sig.service.impl;

import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.UserProjection;
import dz.eadn.sig.model.Privacy;
import dz.eadn.sig.repository.*;
import dz.eadn.sig.security.RedisUtil;
import dz.eadn.sig.service.StatsService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LOKBANI Chouaib
 *
 */
@Service
public class StatsServiceImpl implements StatsService {

    private LayerRepository layerRepository;

    private MapRepository mapRepository;

    private GroupRepository groupRepository;

    private UserRepository userRepository;

    private EntityElementRepository entityElementRepository;

    private JdbcTemplate jdbcTemplate;

    private RedisUtil redisUtil;

    public StatsServiceImpl(LayerRepository layerRepository, MapRepository mapRepository, GroupRepository groupRepository, UserRepository userRepository, EntityElementRepository entityElementRepository, JdbcTemplate jdbcTemplate, RedisUtil redisUtil){
        this.layerRepository = layerRepository;
        this.mapRepository = mapRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.entityElementRepository = entityElementRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.redisUtil = redisUtil;
    }

    @Override
    public Map<String, Long> getTotalCount() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("layerNB", layerRepository.count());
        stats.put("mapNB", mapRepository.count());
        stats.put("groupNB", groupRepository.count());
        stats.put("userNB", userRepository.count());
        return stats;
    }

    @Override
    public Map<String, ?> getMapStats() {
        Map<String, Long> mapsStats = new HashMap<>();
        mapsStats.put("Partagée avec lien", mapRepository.countMapByPrivacy(Privacy.PUBLIC_WITH_LINK));
        mapsStats.put("Public", mapRepository.countMapByPrivacy(Privacy.PUBLIC));
        mapsStats.put("Privée", mapRepository.countMapByPrivacy(Privacy.PRIVATE));
        mapsStats.put("Archivée", mapRepository.countMapByPrivacy(Privacy.ARCHIVED));
        return mapsStats;
    }

    @Override
    public Map<String, ?> countUsersByStatus() {
        List<UserProjection> users = userRepository.findBy();
        Map<String, Long> stats = new HashMap<>();
        stats.put("Connectées", 0l);
        stats.put("Non connectées", 0l);
        for(UserProjection u: users){
            Set<String> userTokens = redisUtil.findConnectedUserTokens(u.getUsername());
            boolean tokenExpired = userTokens.isEmpty() ? false : true;
           if(!tokenExpired){
               stats.put("Non connectées", stats.get("Non connectées") + 1);
           }else{
               stats.put("Connectées", stats.get("Connectées") + 1);
           }
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getStatsByEntityAndTime(String entity, String time) {
        String query = null;
        if(time.equals("Year")){
            query = "SELECT to_char(m.create_date, '"+time+"') as time, COUNT(*) AS total FROM sig."+entity+" m group by  1,extract(year from m.create_date) order by extract(year from m.create_date)   asc";
        }else if(time.equals("Mon")){
            query = "SELECT to_char(m.create_date, '"+time+"') as time, COUNT(*) AS total FROM sig."+entity+" m group by  1,extract(mon from m.create_date) order by extract(mon from m.create_date)   asc";
        }
        try{
            return jdbcTemplate.queryForList(query);
        }catch (Exception e){
            throw  new RuntimeException("Impossible de récupérer les données !");
        }
    }

    @Override
    public LayerStatsPaginator countEntityElementByLayer(@PageableDefault(size=10, page=0) Pageable pageable) {
        LayerStatsPaginator layerStatsPagination = new LayerStatsPaginator();
        List<Map<String, ?>> layersStats = entityElementRepository.countEntityElementByLayer(pageable);

        layerStatsPagination.setLayerStats(layersStats);

        layerStatsPagination.nbrTotal = (int) layerRepository.count();
        layerStatsPagination.page = pageable.getPageNumber();
        layerStatsPagination.size = pageable.getPageSize();
        return layerStatsPagination;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class LayerStatsPaginator {

        List<Map<String, ?>> layerStats;
        int nbrTotal;
        int size;
        int page;
    }
}
