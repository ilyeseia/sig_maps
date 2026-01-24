package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.List;

import dz.eadn.sig.util.SearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonFilter {

	private String condition = "";
	private List<SearchCriteria> rules = new ArrayList<>();

}
