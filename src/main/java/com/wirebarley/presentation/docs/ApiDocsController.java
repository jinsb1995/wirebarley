package com.wirebarley.presentation.docs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiDocsController {

    @GetMapping("/api-docs")
    public String apiDocs() {
        return "/docs/index.html";
    }
}
