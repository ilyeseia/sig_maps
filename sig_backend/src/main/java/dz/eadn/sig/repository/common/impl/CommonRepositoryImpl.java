package dz.eadn.sig.repository.common.impl;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import dz.eadn.sig.repository.common.CommonRepository;
import dz.eadn.sig.util.WITHUUID;

/**
 * @author Achrouf Abdenour
 *
 * @param <CommonObject>
 */
@NoRepositoryBean
public abstract class CommonRepositoryImpl<CommonObject extends WITHUUID>
		extends SimpleJpaRepository<CommonObject, UUID> implements CommonRepository<CommonObject> {

	private static final long serialVersionUID = -3943656263780592037L;

	@PersistenceContext
	private EntityManager entityManager;

	// There are two constructors to choose from, either can be used.
	public CommonRepositoryImpl(Class<CommonObject> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

}
