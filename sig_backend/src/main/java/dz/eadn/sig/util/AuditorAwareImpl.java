package dz.eadn.sig.util;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Ameur LAMOUR
 *
 */
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			return Optional.of(currentUserName);
		}

		return Optional.of("System");

	}
}
