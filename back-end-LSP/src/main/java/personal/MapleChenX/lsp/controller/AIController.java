package personal.MapleChenX.lsp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    @RequestMapping("/test")
    public String test() {
        System.out.println("test");
        return "test";
    }
}
