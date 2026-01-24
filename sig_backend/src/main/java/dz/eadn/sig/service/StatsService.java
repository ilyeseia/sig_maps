package dz.eadn.sig.service;

import dz.eadn.sig.service.impl.StatsServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.Map;

public interface StatsService {

    Map<String, Long> getTotalCount();

    Map<String, ?> getMapStats();

    Map<String, ?> countUsersByStatus();

    List<Map<String, Object>> getStatsByEntityAndTime(String entity, String time);
    StatsServiceImpl.LayerStatsPaginator countEntityElementByLayer(@PageableDefault(size=10, page=0) Pageable pageable);

}
