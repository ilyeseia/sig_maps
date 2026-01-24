package dz.eadn.sig.constants;


import dz.eadn.sig.util.GeoServerRest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

/**
 * @author Achrouf Abdenour
 */
public class Constants {

    public final static String SERVER_ADDRESS_CODE = "SERVER_ADDRESS";
    public static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    public static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    public static final String fileDownloadServer = "/api/v1.0/download/";
    public static final String FOLDER_IMAGES_IMAGES = "images";
    public static final String FOLDER_IMAGES_ICONS = "icons";
    public static final String FOLDER_IMAGES_PHOTO = "photo";
    public static final String FOLDER_IMAGES_LAYERS = "layers";
    public static final String FOLDER_HELP = "help";
    public static final String SIG_DEFAULT_DATE_FORMAT = "SIG_DEFAULT_DATE_FORMAT";
    public static final String GEO_SERVER_WORKSPACE = "limite_admin";
}
