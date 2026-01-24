package dz.eadn.sig.dto;


import dz.eadn.sig.model.TOPO;

import java.util.UUID;

public interface LayerProjection {

    UUID getId();

    String getName();

    String getSlug();

    String getTopo();

}
