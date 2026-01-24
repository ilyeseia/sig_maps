package dz.eadn.sig.repository.impl;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import dz.eadn.sig.model.Layer;
import dz.eadn.sig.repository.common.impl.CommonRepositoryImpl;

@Repository
public class LayerRepositoryImpl extends CommonRepositoryImpl<Layer> {

	public LayerRepositoryImpl(EntityManager entityManager) {
		super(Layer.class, entityManager);
	}

}
