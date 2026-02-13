package dz.eadn.sig.repository.impl;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import dz.eadn.sig.repository.common.impl.CommonRepositoryImpl;

@Repository
public class MapRepositoryImpl extends CommonRepositoryImpl<dz.eadn.sig.model.Map> {

	public MapRepositoryImpl(EntityManager entityManager) {
		super(dz.eadn.sig.model.Map.class, entityManager);
	}

}
