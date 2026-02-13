package dz.eadn.sig.repository.impl;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import dz.eadn.sig.model.Group;
import dz.eadn.sig.repository.common.impl.CommonRepositoryImpl;

@Repository
public class GroupRepositoryImpl extends CommonRepositoryImpl<Group> {
	public GroupRepositoryImpl(EntityManager entityManager) {
		super(Group.class, entityManager);
	}

}
