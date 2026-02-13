/**
 * 
 */
package dz.eadn.sig.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;

import dz.eadn.sig.model.Layer;
import dz.eadn.sig.service.EntityElementService;
import dz.eadn.sig.service.ReportsService;

/**
 * @author Achrouf Abdenour
 *
 */
public class ReportsServiceImpl implements ReportsService {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private EntityElementService eeService;

	public int countByLayer(Layer layer) {
		TypedQuery<Integer> query = entityManager
				.createQuery("SELECT count(e) FROM EntityElement e where e.layer = :layer", Integer.class);
		query.setParameter("layer", layer);
		return query.getSingleResult();
	}

	public HashMap<String, Integer> countByLayerAndProperty(String fieldName, UUID layerId) {
		List<String> resultList = eeService.findAllByProperty(fieldName, layerId);

		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();

		for (String item : resultList) {
			if (repetitions.containsKey(item))
				repetitions.put(item, repetitions.get(item) + 1);
			else
				repetitions.put(item, 1);
		}

		return repetitions;
	}

	public HashMap<String, Integer> calculateRate() {
		int totalSum = 0;
		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
		HashMap<String, Integer> rates = new HashMap<String, Integer>();

		for (String item : repetitions.keySet()) {
			totalSum += repetitions.get(item);
		}

		for (String item : repetitions.keySet()) {
			int rep = repetitions.get(item);
			int rate = rep * 100 / totalSum;
			rates.put(item, rate);
		}

		return rates;
	}
}
