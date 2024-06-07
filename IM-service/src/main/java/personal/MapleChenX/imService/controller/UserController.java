package personal.MapleChenX.imService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    @RequestMapping("/register")
    public ResponseEntity<?> register() {
        System.out.println("register");
        return ResponseEntity.ok().build();
    }


}
