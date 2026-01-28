/**
 * 
 */
package dz.eadn.sig.config;

import java.security.Principal;

/**
 * @author Achrouf Abdenour
 *
 */
class StompPrincipal implements Principal {
	private String name;

	public StompPrincipal(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
