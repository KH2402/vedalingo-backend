package com.vedalingo.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TestController {

    @PostMapping("/test")
    public void testApi(@RequestBody String name ){
        System.out.println(name);
    }

}
