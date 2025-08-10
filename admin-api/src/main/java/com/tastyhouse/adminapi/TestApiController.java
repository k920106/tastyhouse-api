package com.tastyhouse.adminapi;

import com.tastyhouse.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TestApiController {

    private final TestService testService;

    @GetMapping("/")
    public String index() {
        List<User> all = testService.findAll();
        return "admin";
    }
}
