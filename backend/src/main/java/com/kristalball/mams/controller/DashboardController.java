package com.kristalball.mams.controller;

import com.kristalball.mams.model.AssetTransaction;
import com.kristalball.mams.model.EquipmentType;
import com.kristalball.mams.model.TransactionType;
import com.kristalball.mams.repository.TransactionRepository;
import com.kristalball.mams.service.CurrentUserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Dashboard API - calculates opening balance, net movement, closing balance etc.
@RestController
@RequestMapping("/api")
public class DashboardController {

    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public DashboardController(TransactionRepository transactionRepository, CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    // only Admin and Base Commander can see the dashboard
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public Map<String, Object> getDashboard(
            @RequestParam(required = false) Long base,
            @RequestParam(required = false) EquipmentType equipmentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        // commander can only see his own base
        Long baseId = currentUserService.getBaseIdToUse(base);

        // get all rows till the "to" date
        List<AssetTransaction> allTransactions = transactionRepository.search(baseId, equipmentType, to);

        int openingBalance = 0;
        int purchases = 0;
        int transferIn = 0;
        int transferOut = 0;
        int assigned = 0;
        int expended = 0;

        for (AssetTransaction transaction : allTransactions) {
            boolean isBeforeFromDate = from != null && transaction.getTxnDate().isBefore(from);

            if (isBeforeFromDate) {
                // rows before the "from" date make the opening balance
                openingBalance += transaction.stockEffect();
            } else if (transaction.getType() == TransactionType.PURCHASE) {
                purchases += transaction.getQuantity();
            } else if (transaction.getType() == TransactionType.TRANSFER_IN) {
                transferIn += transaction.getQuantity();
            } else if (transaction.getType() == TransactionType.TRANSFER_OUT) {
                transferOut += transaction.getQuantity();
            } else if (transaction.getType() == TransactionType.ASSIGNED) {
                assigned += transaction.getQuantity();
            } else if (transaction.getType() == TransactionType.EXPENDED) {
                expended += transaction.getQuantity();
            }
        }

        // Net movement = purchases + transfer in - transfer out
        int netMovement = purchases + transferIn - transferOut;

        // Closing balance = opening balance + net movement - expended
        int closingBalance = openingBalance + netMovement - expended;

        Map<String, Object> result = new HashMap<>();
        result.put("openingBalance", openingBalance);
        result.put("netMovement", netMovement);
        result.put("purchases", purchases);
        result.put("transferIn", transferIn);
        result.put("transferOut", transferOut);
        result.put("assigned", assigned);
        result.put("expended", expended);
        result.put("closingBalance", closingBalance);
        return result;
    }
}
