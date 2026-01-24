package dz.eadn.sig.api.v1;

import dz.eadn.sig.service.StatsService;
import dz.eadn.sig.service.impl.StatsServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author LOKBANI Chouaib
 *
 */

@RestController
@RequestMapping("/api/v1.0/stats")
public class StatsController {

    private StatsService statsService;

    public StatsController(StatsService statsService){
        this.statsService = statsService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/total")
    public ResponseEntity<?> getTotals() {
        return new ResponseEntity<Map<String, Long>>(statsService.getTotalCount() , HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/map")
    public ResponseEntity<?> getMapStats() {
        return new ResponseEntity<Map<String, ?>>(statsService.getMapStats() , HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users-status")
    public ResponseEntity<?> countUsersByStatus() {
        return new ResponseEntity<Map<String, ?>>(statsService.countUsersByStatus() , HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getStatsByEntityAndTime(@RequestParam("entity") String entity, @RequestParam("time") String time) {
        return new ResponseEntity<List<Map<String, Object>>>(statsService.getStatsByEntityAndTime(entity, time) , HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/entity-elements")
    public ResponseEntity<?> countEntityElementByLayer(@PageableDefault(size=10, page=0) Pageable pageable) {
        return new ResponseEntity<StatsServiceImpl.LayerStatsPaginator>(statsService.countEntityElementByLayer(pageable) , HttpStatus.OK);
    }
}
