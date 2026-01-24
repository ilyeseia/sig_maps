package dz.eadn.sig.exceptions;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponce {

	private Date timestamp;
	private String message;
	private String details;

}
