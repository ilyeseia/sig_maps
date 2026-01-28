package dz.eadn.sig.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chouaib LOKBANI
 *
 */
@Entity
@Table(schema = "sig", name = "style")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Style extends WITHUUID {

    private String name = "default";

    private String displayName;

    private String style;

    private Boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Type of Symbologie", example = "Simple", required = true)
    private SymbologyType symbologyType = SymbologyType.Simple;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_layer_map_style"), name = "layer_map_style", referencedColumnName = "mapLayerId")
    private MapLayer mapLayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_theme_style_id"), name = "theme_style_id")
    private Theme theme;

}