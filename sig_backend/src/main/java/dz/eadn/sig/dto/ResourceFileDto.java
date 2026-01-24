/**
 * 
 */
package dz.eadn.sig.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Achrouf Abdenour
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResourceFileDto {
	private int columnStart;
	private int columnEnd;
	private MultipartFile file;
	private String fileType;
}
