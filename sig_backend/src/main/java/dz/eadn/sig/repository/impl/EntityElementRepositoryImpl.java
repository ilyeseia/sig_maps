package dz.eadn.sig.repository.impl;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.repository.common.impl.CommonRepositoryImpl;

@Repository
public class EntityElementRepositoryImpl extends CommonRepositoryImpl<EntityElement> {

	public EntityElementRepositoryImpl(EntityManager entityManager) {
		super(EntityElement.class, entityManager);
	}

}
