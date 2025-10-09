package com.tastyhouse.adminapi.supply;

import com.tastyhouse.adminapi.supply.response.SupplyListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supplies")
public class SupplyApiController {

    private final SupplyService supplyService;

    @GetMapping
    public ResponseEntity<List<SupplyListItem>> getSupplyList() {
        List<SupplyListItem> supplies = supplyService.findSupplyList();
        return ResponseEntity.ok(supplies);
    }
}
