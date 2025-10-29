package com.barbearia.adapters.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HelloController {

    @GetMapping("/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui.html");
    }

    @GetMapping("/docs")
    public RedirectView redirectToSwaggerDocs() {
        return new RedirectView("/swagger-ui.html");
    }
}
