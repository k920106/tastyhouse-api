package com.tastyhouse.adminapi.supply;

import com.tastyhouse.adminapi.supply.response.SupplyListItem;
import com.tastyhouse.core.service.SupplyCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyCoreService supplyCoreService;

    public List<SupplyListItem> findSupplyList() {
        return supplyCoreService.findAllSupplies()
                .stream()
                .map(supply -> new SupplyListItem(supply.getId(), supply.getName()))
                .toList();
    }
}
