package dz.eadn.sig.model;

import lombok.*;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Embeddable
public class UserLayerFilterId implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID userId;

    private UUID layerId;

    private UUID filterId;


}
