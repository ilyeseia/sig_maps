package dz.eadn.sig.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dz.eadn.sig.dto.GroupDto;
import dz.eadn.sig.dto.GroupSimpleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import dz.eadn.sig.dto.DivisionDto;
import dz.eadn.sig.dto.UserDto;
import dz.eadn.sig.model.EntityElement;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.Layer;
import dz.eadn.sig.model.User;
import dz.eadn.sig.service.EntityElementService;
import dz.eadn.sig.service.LayerService;
import dz.eadn.sig.service.UserService;

/**
 * @author Achrouf Abdenour 
 *
 */
/**
 * @author LAMOUR AMEUR
 *
 */
@Component
@Primary
public class UserMapper extends CommonMapper<User, UserDto> {

	@Autowired
	private UserService userService;

	@Autowired
	private LayerService layerService;

	@Autowired
	private GroupMapper groupMapper;

	@Autowired
	private EntityElementService entityElementService;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	/*
	 * @Autowired private UserNotificationMapper userNotificationMapper;
	 */

	@Autowired
	private LayerMapper layerMapper;

	@Override
	protected UserDto mapEntityToDto(User entity) {
		UserDto userDto = new UserDto();
		userDto.setFirstName(entity.getFirstName());
		userDto.setLastName(entity.getLastName());
		userDto.setHomePhone(entity.getHomePhone());
		userDto.setMobile(entity.getMobile());
		userDto.setFax(entity.getFax());
		userDto.setActivationDate(entity.getActivationDate());
		userDto.setDesactivationDate(entity.getDesactivationDate());
		userDto.setUsername(entity.getUsername());
		userDto.setEmail(entity.getEmail());
		userDto.setEnabled(entity.getEnabled());
		userDto.setAvatar(entity.getAvatar());


		 if(entity.getGroups() != null){
			 userDto.setGroupDtos(cModelMapper.mapList(entity.getGroups(), GroupSimpleDto.class));
		 }



		if (entity.getLayers() != null) {
			userDto.setLayersDtos(layerMapper.entitysToDtos(entity.getLayers()));
		}

		if (entity.getEntityElements() != null) {

			List<DivisionDto> divisionDtos = new ArrayList<>();

			for (EntityElement entityElement : entity.getEntityElements()) {

				DivisionDto divisionDto = new DivisionDto();
				Layer layer = layerService.findById(entityElement.getLayer().getId());

				if (layer != null) {
					divisionDto.setValue(entityElement.getId());
					divisionDto.setText(entityElement.getProperties().get(layer.getIdentifiant()));
					divisionDto.setLayerSlug(layer.getSlug());
					divisionDtos.add(divisionDto);
				}

			}
			userDto.setDivisionDtos(divisionDtos);
		}

		if (entity.getNotifications() != null) {
			// userDto.setNotificationDtos(userNotificationMapper.entitysToDtos(entity.getNotifications()));
		}

		List<String> claims = new ArrayList<String>();

		if (entity.getGroups() != null) {
			for (Group group : entity.getGroups()) {
				claims.addAll(group.getPermissions().stream().map(p -> p.getName()).collect(Collectors.toList()));
				if (group.getName().equals("ROLE_ADMIN"))
					claims.add(group.getName());
			}
		}

		userDto.setClaims(claims);
		return userDto;
	}

	@Override
	protected User mapDtoToEntity(UserDto dto) {
		User user = userService.findById(dto.getId(), true);

		if (user == null) {
			if (dto.getId() != null) {
				return null;
			}
			user = new User();
		} else {
			if (user.getDeleted())
				throw new RuntimeException("can't do operation on deleted user");
		}

		if (dto.getFirstName() != null) {
			user.setFirstName(dto.getFirstName().trim());
		}
		if (dto.getLastName() != null) {
			user.setLastName(dto.getLastName().trim());
		}
		if (dto.getHomePhone() != null) {
			user.setHomePhone(dto.getHomePhone());
		}
		if (dto.getMobile() != null) {
			user.setMobile(dto.getMobile());
		}
		if (dto.getFax() != null) {
			user.setFax(dto.getFax());
		}
		if (dto.getActivationDate() != null) {
			user.setActivationDate(dto.getActivationDate());
		}
		if (dto.getDesactivationDate() != null) {
			user.setDesactivationDate(dto.getDesactivationDate());
		}

		if (dto.getUsername() != null) {
			user.setUsername(dto.getUsername().trim().replaceAll("\\s+", ""));
		}

		if (dto.getPassword() != null) {
			String regex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$.%]).{6,20})";
			if (!dto.getPassword().matches(regex))
				throw new RuntimeException("Password not valid");
			else
				user.setPassword(dto.getPassword());
		}

		if (dto.getEnabled() != null) {
			user.setEnabled(dto.getEnabled());
		}

		if (dto.getEmail() != null) {
			user.setEmail(dto.getEmail());
		}

		if (dto.getAvatar() != null) {
			user.setAvatar(dto.getAvatar());
		}

		if (dto.getGroupDtos() != null) {
			user.setGroups(groupMapper.dtosToEntitys(cModelMapper.mapList(dto.getGroupDtos(), GroupDto.class)));
		}

		if (dto.getDivisionDtos() != null) {

			List<EntityElement> entityElements = new ArrayList<>();
			for (DivisionDto divisionDto : dto.getDivisionDtos()) {

				EntityElement entityElement = entityElementService.findById(divisionDto.getValue());
				if (entityElement != null) {
					entityElements.add(entityElement);
				}
			}

			user.setEntityElements(entityElements);
		}

		if (dto.getNotificationDtos() != null) {
			// user.setNotifications(userNotificationMapper.dtosToEntitys(dto.getNotificationDtos()));
		}

		if (dto.getLayersDtos() != null) {
			user.setLayers(layerMapper.dtosToEntitys(dto.getLayersDtos()));
		}

		return user;
	}
}
