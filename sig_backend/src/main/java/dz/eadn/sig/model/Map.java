package dz.eadn.sig.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dz.eadn.sig.util.WITHUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author Ameur LAMOUR
 *
 */
@Entity
@Table(schema = "sig", name = "map", uniqueConstraints = {
		@UniqueConstraint(columnNames = "name", name = "uk_map_name") })
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Map extends WITHUUID {

	private String name;
	private String slug;
	private String image;

	@Enumerated(EnumType.STRING)
	private Privacy privacy = Privacy.PRIVATE;

	@Schema(description = "List of tags that a user can search with to find maps", required = true)
	@ManyToMany(mappedBy = "maps", fetch = FetchType.LAZY)
	private List<Tag> tags = new ArrayList<>();

	/*
	 * @ManyToMany(fetch = FetchType.LAZY)
	 * 
	 * @JoinTable(schema = "sig", joinColumns = {
	 * 
	 * @JoinColumn(foreignKey = @ForeignKey(name = "fk_map_layer_id")) },
	 * inverseJoinColumns = {
	 * 
	 * @JoinColumn(foreignKey = @ForeignKey(name = "fk_layer_map_id")) }) private
	 * List<Layer> layers;
	 */

	@OneToMany(mappedBy = "map", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MapLayer> layers = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_map_user_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_user_map_id")) })
	private List<User> users = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "sig", joinColumns = {
			@JoinColumn(foreignKey = @ForeignKey(name = "fk_map_group_id")) }, inverseJoinColumns = {
					@JoinColumn(foreignKey = @ForeignKey(name = "fk_group_map_id")) })
	private List<Group> groups = new ArrayList<>();

	@OneToMany(mappedBy = "map", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Theme> themes = new ArrayList<>();

}
