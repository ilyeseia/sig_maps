package dz.eadn.sig.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dz.eadn.sig.model.Group;
import dz.eadn.sig.model.User;

/**
 * @author Ameur LAMOUR
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private User user;

	private String username;

	private List<Group> groups;

	private String email;

	@JsonIgnore
	private String password;

	private Boolean enabled;

	private Collection<? extends GrantedAuthority> authorities;

	public static UserDetailsImpl build(User user) {

		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Group group : user.getGroups()) {

			authorities.addAll(group.getPermissions().stream().map(p -> new SimpleGrantedAuthority(p.getName()))
					.collect(Collectors.toList()));

			authorities.add(new SimpleGrantedAuthority(group.getName()));
		}

		return new UserDetailsImpl(user, user.getUsername(), user.getGroups(), user.getEmail(), user.getPassword(),
				user.getEnabled(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmail() {
		return email;
	}

	public User getUser() {
		return user;
	}

	public List<Group> getGroups() {
		return groups;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
