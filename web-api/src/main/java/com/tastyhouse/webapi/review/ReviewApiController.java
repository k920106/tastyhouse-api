package com.tastyhouse.webapi.review;

import com.tastyhouse.webapi.review.request.ReviewCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    @PostMapping
//    public void create(@RequestBody ReviewCreateRequest reviewCreateRequest) {
    public void create(@ModelAttribute ReviewCreateRequest reviewCreateRequest) {
        System.out.println("hi");
    }
}
