package dz.eadn.sig.service;

import java.util.UUID;

import dz.eadn.sig.dto.CommonFilter;
import dz.eadn.sig.dto.GroupCompleteDto;
import dz.eadn.sig.dto.GroupDto;
import dz.eadn.sig.dto.GroupSimpleDto;
import dz.eadn.sig.dto.GroupSimpleWithOthersDto;
import dz.eadn.sig.dto.PageDto;
import dz.eadn.sig.dto.PermissionSimpleDto;
import dz.eadn.sig.dto.UserSimpleDto;
import dz.eadn.sig.model.Group;
import dz.eadn.sig.service.common.CommonService;

/**
 * @author Achrouf Abdenour & LAMOUR AMEUR
 *
 */
public interface GroupService extends CommonService<Group, GroupDto> {

	Group findByName(String name);

	public PageDto<GroupCompleteDto> getAllGroupsCompleteByPage(Integer page, Integer limit, String sort, String dir);

	public PageDto<GroupSimpleDto> findAllGroupsSimpleByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir);

	public PageDto<GroupCompleteDto> findAllGroupsCompleteByFilter(CommonFilter filter, Integer page, Integer limit,
			String sort, String dir);

	public PageDto<UserSimpleDto> getUsersOfGroup(Group group, Integer page, Integer limit, String sort, String dir);

	public PageDto<PermissionSimpleDto> getPermissionsOfGroup(Group group, Integer page, Integer limit, String sort,
			String dir);

	public GroupSimpleWithOthersDto getGroupWithOthers(UUID id, String source, Integer page, Integer limit, String sort,
			String dir);

}
