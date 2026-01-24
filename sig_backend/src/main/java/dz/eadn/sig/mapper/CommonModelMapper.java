package dz.eadn.sig.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author A.LAMOUR
 *
 */

@Component
public class CommonModelMapper<S, T> {

	@Autowired
	private ModelMapper modelMapper;

	public <S, T> List<T> mapList(List<S> source, Class<T> target) {
		return source.stream().map(element -> modelMapper.map(element, target)).collect(Collectors.toList());
	}

}
