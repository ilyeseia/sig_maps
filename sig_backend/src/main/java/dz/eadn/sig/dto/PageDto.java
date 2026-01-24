package dz.eadn.sig.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageDto<CommonDto> {
	private List<CommonDto> content=new ArrayList<>();
	private long totalElements=0;

}
