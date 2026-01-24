package dz.eadn.sig.model;


import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author Chouaib LOKBANI
 *
 */
@Entity
@Table(schema = "sig", name = "theme")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Theme  extends WITHUUID {

    String name = "Theme par default";

    Boolean isDefault = false;

    @Schema(description = "The list of styles related to one theme.")
    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY, cascade= CascadeType.ALL)
    List<Style> styles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_map", foreignKey = @ForeignKey(name = "fk_theme_map_id"))
    private Map map;

}
