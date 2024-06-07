package personal.MapleChenX.imService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import personal.MapleChenX.imService.dto.UserDTO;
import personal.MapleChenX.imService.mapper.UserMapper;
import personal.MapleChenX.imService.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDTO> implements UserService {

}
