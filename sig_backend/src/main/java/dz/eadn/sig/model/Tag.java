package dz.eadn.sig.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "tag")
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Tag extends WITHUUID {

	@Schema(description = "The name of the tag.", required = true)
	private String name;
	@Schema(description = "The type of the tag.", required = true)
	private String type;

	@Schema(description = "The message of the tag.", required = true)
	private String message;

	@JsonIgnore
	@Schema(description = "The list of layers related to one tag.", required = true)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_layer_tag_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_tag_layer_id")) })
	private List<Layer> layers;

	@JsonIgnore
	@Schema(description = "The list of entityElements related to one tag.", required = true)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_entity_element_tag_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_tag_entity_element_id")) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<EntityElement> entityElements;

	@JsonIgnore
	@Schema(description = "The list of maps related to one tag.", required = true)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_map_tag_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_tag_map_id")) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Map> maps;
}
