package dz.eadn.sig.dto;

import dz.eadn.sig.model.SymbologyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StyleDto {

    SymbologyType symbologyType;

    Boolean labelEnabled = false;

    Boolean customIcon = false;

    String iconUrl;

    String selectedMode;

    String selectedMethod;

    int classes;

    int precision;

    HashMap<String, String> transformation = null;

    List<HashMap<String, HashMap<String, String>>> rules = new ArrayList<>();

}
