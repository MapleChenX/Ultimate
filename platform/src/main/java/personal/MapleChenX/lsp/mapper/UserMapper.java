package personal.MapleChenX.lsp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import personal.MapleChenX.lsp.common.model.dto.UserBasicInfoDTO;

@Mapper
public interface UserMapper extends BaseMapper<UserBasicInfoDTO> {
}
