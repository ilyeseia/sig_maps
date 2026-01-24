package dz.eadn.sig.dto;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LAMOUR AMEUR
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class NotificationMessagesDto {

	private Map<String, String> messages = new HashMap<>();

	@PostConstruct
	public void init() {

		// map messages
		messages.put("NM_MAP_CREATE", "La carte <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_UPDATE", "La carte <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_DELETE", "La carte <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_ARCHIVE", "La carte <strong> %s </strong> a été archivé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_UNARCHIVE", "La carte <strong> %s </strong> a été désarchivé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_SHARE", "La carte <strong> %s </strong> a été partatgé avec vous par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_UNSHARE", "La carte <strong> %s </strong> a été départatgé avec vous par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_PRIVACY",
				"La carte <strong> %s </strong> a été changé du status par l'utilisateur <strong> %s </strong> est devenu <strong> %s </strong>");
		messages.put("NM_MAP_ATTACH_LAYER",
				"La couche <strong> %s </strong> a été attaché dans la carte <strong> %s </strong> par l'utilisateur <strong> %s </strong>");
		messages.put("NM_MAP_DETACH_LAYER",
				"La couche <strong> %s </strong> a été déttaché de la carte <strong> %s </strong> par l'utilisateur <strong> %s </strong>");

		// layer messages
		messages.put("NM_LAYER_CREATE", "La couche <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_LAYER_UPDATE", "La couche <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_LAYER_DELETE", "La couche <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_LAYER_SHARE", "La couche <strong> %s </strong> a été partagé avec vous par l'utilisateur <strong> %s </strong>");
		messages.put("NM_LAYER_UNSHARE", "La couche <strong> %s </strong> a été départagé avec vous par l'utilisateur <strong> %s </strong>");
		messages.put("NM_LAYER_CHANGE_STYLE",
				"Le style <strong> %s </strong> de la couche <strong> %s </strong> a été changé avec succés par l'utilisateur <strong> %s </strong>");

		// settings messages
		messages.put("NM_SETTINGS_CREATE", "Le paramettre <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_SETTINGS_UPDATE",
				"Le paramettre <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_SETTINGS_DELETE", "Le paramettre <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");

		// settings_type messages
		messages.put("NM_SETTINGS_TYPE_CREATE",
				"Le type de paramettre <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_SETTINGS_TYPE_UPDATE",
				"Le type de paramettre <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_SETTINGS_TYPE_DELETE",
				"Le type de paramettre <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");

		// settings messages
		messages.put("NM_RESOURCE_CREATE", "La ressource <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_RESOURCE_UPDATE", "La ressource <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_RESOURCE_DELETE", "La ressource <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");

		// user messages
		messages.put("NM_USER_CREATE", "L'utilisateur <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_USER_UPDATE", "L'utilisateur <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_USER_DELETE", "L'utilisateur <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_USER_RESET_PASSWORD",
				"Le mot de passe de l'utilisateur <strong> %s </strong> a été rénitialisé avec succés par l'utilisateur <strong> %s </strong>");

		// group messages
		messages.put("NM_GROUP_CREATE", "Le groupe <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_GROUP_UPDATE", "Le groupe <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_GROUP_DELETE", "Le groupe <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_GROUP_ADD_USER", "Tu a été affecté au group <strong> %s </strong> avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_GROUP_REMOVE_USER",
				"Tu a été désaffecté du group <strong> %s </strong> avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_GROUP_ADD_PERMISSION",
				"La permission <strong> %s </strong> a été affecté au group <strong> %s </strong> avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_GROUP_REMOVE_PERMISSION",
				"La permission <strong> %s </strong> a été désaffecté du group <strong> %s </strong> avec succés par l'utilisateur <strong> %s </strong>");


		// Settings messages
		messages.put("NM_FILTER_CREATE", "Le filtre <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_FILTER_UPDATE", "Le filtre <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_FILTER_DELETE", "Le filtre <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("NM_FILTER_SHARE", "Le filtre <strong> %s </strong> a été partagé avec vous par l'utilisateur <strong> %s </strong>");
		messages.put("NM_FILTER_UNSHARE", "Le filtre <strong> %s </strong> a été départagé avec vous par l'utilisateur <strong> %s </strong>");

		// Styles messages
		messages.put("STYLE_CREATE", "Le style <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong> dans la couche <strong> %s </strong>");
		messages.put("STYLE_UPDATE", " Le style <strong> %s </strong> de la couche <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("STYLE_DELETE", "Le style <strong> %s </strong> de la couche <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("STYLE_AS_DEFAULT", "Le style <strong> %s </strong> de la couche <strong> %s </strong> est défini par défaut par l'utilisateur <strong> %s </strong>");

		// Themes messages
		messages.put("THEME_CREATE", "Le theme <strong> %s </strong> a été crée avec succés par l'utilisateur <strong> %s </strong> dans la carte <strong> %s </strong>");
		messages.put("THEME_UPDATE", "Le theme <strong> %s </strong> de la carte <strong> %s </strong> a été mis à jour avec succés par l'utilisateur <strong> %s </strong>");
		messages.put("THEME_DELETE", "Le theme <strong> %s </strong> de la carte <strong> %s </strong> a été supprimé avec succés par l'utilisateur <strong> %s </strong>");

	}

}
