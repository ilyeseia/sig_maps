package dz.eadn.sig.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.*;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class MapLayerId implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID randomId;

	private UUID mapId;

	private UUID layerId;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
		result = prime * result + ((layerId == null) ? 0 : layerId.hashCode());
		result = prime * result + ((randomId == null) ? 0 : randomId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapLayerId other = (MapLayerId) obj;
		return Objects.equals(getMapId(), other.getMapId())
				&& Objects.equals(getLayerId(), other.getLayerId())
				&& Objects.equals(getRandomId(), other.getRandomId());
	}

}
