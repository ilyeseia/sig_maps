package dz.eadn.sig.api.v1;

import dz.eadn.sig.config.EntitiesLoader;
import dz.eadn.sig.controller.common.CommonController;
import dz.eadn.sig.dto.*;
import dz.eadn.sig.model.UserLoggedActions;
import dz.eadn.sig.service.UserLoggedActionsService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * &author: Chouaib LOKBANI
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/audit")
public class AuditController extends CommonController<UserLoggedActions, UserLoggedActionsDto> {

    @Autowired
    EntitiesLoader entitiesLoader;

    @Autowired
    UserLoggedActionsService userLoggedActionsService;

    public AuditController() {
        super(UserLoggedActions.class);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('AUDITING')")
    @PostMapping("/search")
    public ResponseEntity<?> findAllByFilter(
            @Parameter(description = "Provide a payload how has the attributes of layer", required = true) @RequestBody CommonFilter filter,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "100") Integer limit, @RequestParam(defaultValue = "createDate") String sort,
            @RequestParam(defaultValue = "desc") String dir) {
        PageDto<UserLoggedActionsDto> pageDto = userLoggedActionsService.findAllByFilter(filter,
                page, limit, sort, dir);
        return new ResponseEntity<PageDto<UserLoggedActionsDto>>(pageDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('AUDITING')")
    @GetMapping("/entities")
    public ResponseEntity<?> getAllEntities() {
        return new ResponseEntity<List<String>>(entitiesLoader.returnEntitiesList(), HttpStatus.OK);
    }

}
