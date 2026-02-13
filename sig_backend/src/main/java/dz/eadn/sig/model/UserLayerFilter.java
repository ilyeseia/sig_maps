package dz.eadn.sig.model;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(schema = "sig", name = "user_layer_filter")
public class UserLayerFilter  {

    @EmbeddedId
    private UserLayerFilterId userLayerFilterId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @MapsId("layerId")
    @JoinColumn(name="layer_id")
    private Layer layer;

    @ManyToOne
    @MapsId("filterId")
    @JoinColumn(name="filter_id")
    private Filter filter;

    @Column(name = "filter_cloned_from")
    private UUID filterClonedFrom;

    public UserLayerFilter(User user, Layer layer, Filter filter, UUID filterClonedFrom) {
        this.userLayerFilterId = new UserLayerFilterId(user.getId(), layer.getId(), filter.getId());
        this.user = user;
        this.layer = layer;
        this.filter = filter;
        this.filterClonedFrom = filterClonedFrom;
    }

}
