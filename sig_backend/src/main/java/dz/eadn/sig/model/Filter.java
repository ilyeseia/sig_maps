package dz.eadn.sig.model;


/**
 * @author LOKBANI Chouaib
 *
 */
import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "sig", name = "filter")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Filter  extends WITHUUID {

    @NotNull
    private String name;

    private String description;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String filterConfig;

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLayerFilter> userLayerFilters = new ArrayList<>();

}
