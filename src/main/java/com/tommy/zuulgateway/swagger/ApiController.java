package com.tommy.zuulgateway.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class ApiController {

    @GetMapping("/api")
    public String home() { return "redirect:/swagger-ui.html";}
}
