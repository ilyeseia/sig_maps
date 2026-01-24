package dz.eadn.sig.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import dz.eadn.sig.dto.CustomJoinFilter;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.Map;
import dz.eadn.sig.model.Tag;
import dz.eadn.sig.repository.common.impl.CommonRepositoryImpl;
import dz.eadn.sig.repository.custom.CustomTagRepository;

/**
 * @author Ameur LAMOUR
 *
 */
@Repository
public class TagRepositoryImpl extends CommonRepositoryImpl<Tag> implements CustomTagRepository {

	public TagRepositoryImpl(EntityManager entityManager) {
		super(Tag.class, entityManager);
	}

	@Override
	public List<Object> findAllByType(CustomJoinFilter customJoinFilter) {

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Object> query = cb.createQuery(Object.class);

		Root<Tag> rootTag = query.from(Tag.class);

		Join<Tag, Layer> joinLayer = rootTag.join("layers", JoinType.LEFT);
		Join<Tag, EntityElement> joinEE = rootTag.join("entityElements", JoinType.LEFT);
		Join<Tag, Map> joinMap = rootTag.join("maps", JoinType.LEFT);

		TypedQuery<Object> typedQuery = null;

		if (customJoinFilter.getTarget().equals("Layer")) {
			typedQuery = getEntityManager()
					.createQuery(query.select(joinLayer).where(cb.equal(rootTag.get("id"), customJoinFilter.getId())));

		} else if (customJoinFilter.getTarget().equals("EntityElement")) {
			typedQuery = getEntityManager()
					.createQuery(query.select(joinEE).where(cb.equal(rootTag.get("id"), customJoinFilter.getId())));

		} else if (customJoinFilter.getTarget().equals("Map")) {
			typedQuery = getEntityManager()
					.createQuery(query.select(joinMap).where(cb.equal(rootTag.get("id"), customJoinFilter.getId())));
		}

		if (customJoinFilter.getTarget() == "") {

			typedQuery = getEntityManager().createQuery(query.multiselect(joinLayer, joinEE, joinMap)
					.where(cb.equal(rootTag.get("id"), customJoinFilter.getId())));
		}

		return typedQuery.getResultList();

	}

}
