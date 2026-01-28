package dz.eadn.sig.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "layer", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name" }, name = "uk_layer_name") })
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Layer extends WITHUUID implements Serializable {
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	private LayerType type = LayerType.VECTOR;

	// @NotBlank(message = "Please enter name!")
	@Schema(description = "Name of the Layer", example = "MSAN", required = true)
	private String name;

	private String slug;

	@Schema(description = "Layer geometry", example = "Point", required = true)
	private String topo;

	private String identifiant;

	@Enumerated(EnumType.STRING)
	@Schema(description = "Type of administrative boundary", example = "Client limit", required = true)
	private TypeLimit typeLimit = TypeLimit.LAYER;

	@Schema(description = "List of tags that a user can search with to find layers", example = "MSAN", required = true)
	@ManyToMany(mappedBy = "layers", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<Tag> tags;

	@Schema(description = "List of layer fields.", example = "Capacite", required = true)
	@OneToMany(mappedBy = "layer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Field> fields = new ArrayList<>();

	@OneToMany(mappedBy = "layer", orphanRemoval = true, fetch = FetchType.LAZY)
	private List<EntityElement> entityElements = new ArrayList<>();

	@JoinColumn(foreignKey = @ForeignKey(name = "fk_layer_viewElement_id"))
	@OneToOne(fetch = FetchType.LAZY)
	private EntityElement viewElement;

	/*
	 * @ManyToMany(mappedBy = "layers", fetch = FetchType.LAZY) private List<Map>
	 * maps;
	 */

	@OneToMany(mappedBy = "layer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MapLayer> maps = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_layer_user_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_user_layer_id")) })
	private List<User> users = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_layer_group_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_group_layer_id")) })
	private List<Group> groups = new ArrayList<>();

	@OneToOne(mappedBy = "layer", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Notification notification;

	@OneToMany(mappedBy = "layer", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<UserLayerFilter> userLayerFilters = new ArrayList<>();
}
