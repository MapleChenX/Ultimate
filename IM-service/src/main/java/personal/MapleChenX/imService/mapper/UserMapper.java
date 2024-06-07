package personal.MapleChenX.imService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import personal.MapleChenX.imService.dto.UserDTO;

@Mapper
public interface UserMapper extends BaseMapper<UserDTO> {

}
