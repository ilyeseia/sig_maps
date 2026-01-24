package dz.eadn.sig.dto;



import java.util.UUID;

/**
 * @author LOKBANI Chouaib
 *
 */

public interface UserSimpleDtoWithFilterProjection {
    String getId();
    String getUsername();
    String getFirstName();
    String getLastName();
    String getAvatar();
    String getEmail();
    String getFilterClonedFrom();

}
