package personal.MapleChenX.lsp.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.web.bind.annotation.*;
import personal.MapleChenX.lsp.common.model.req.AddBlacklistReq;
import personal.MapleChenX.lsp.common.model.req.LoginReq;
import personal.MapleChenX.lsp.common.model.req.PasswordModifyReq;
import personal.MapleChenX.lsp.common.model.req.RegisterReq;
import personal.MapleChenX.lsp.common.model.ret.ResponseTemplate;
import personal.MapleChenX.lsp.service.UserService;

@RestController
@RequestMapping("/v1/user")
public class AccountController {
    @Resource
    UserService userService;

    @GetMapping("/auth/sendCode")
    public ResponseTemplate<?> sendCode(@RequestParam("email") @Email String email, @RequestParam("type") int type){
        return userService.sendCode(email,type);
    }

    @PostMapping("/auth/register")
    public ResponseTemplate<?> register(@RequestBody @Valid RegisterReq req){
        String code = req.getCode();
        return userService.register(req, code);
    }

    /**
     * [deprecated]
     */
    @PostMapping("/auth/login")
    public ResponseTemplate<?> login(@RequestBody @Valid LoginReq req){
        return userService.login(req);
    }

    // 内部接口
    @PostMapping("/userBan")
    public ResponseTemplate<?> addBlacklist(@RequestBody @Valid AddBlacklistReq req){
        return userService.addBlacklist(req);
    }

    @PostMapping("/auth/passwordModify")
    public ResponseTemplate<?> passwordModify(@RequestBody @Valid PasswordModifyReq req){
        String code = req.getCode();
        return userService.passwordModify(req, code);
    }



    @PostMapping("/test")
    public ResponseTemplate<?> test(){
        return ResponseTemplate.success("test11");
    }







}
