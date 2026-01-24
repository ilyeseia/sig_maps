package dz.eadn.sig.repository.impl;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import dz.eadn.sig.model.User;
import dz.eadn.sig.repository.common.impl.CommonRepositoryImpl;

/**
 * @author Achrouf Abdenour
 *
 */
@Repository
public class UserRepositorympl extends CommonRepositoryImpl<User> {

	public UserRepositorympl(EntityManager entityManager) {
		super(User.class, entityManager);
	}

}
