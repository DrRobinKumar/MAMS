package com.kristalball.mams.controller;

import com.kristalball.mams.dto.TransactionRequest;
import com.kristalball.mams.model.AssetTransaction;
import com.kristalball.mams.model.EquipmentType;
import com.kristalball.mams.model.Role;
import com.kristalball.mams.model.TransactionType;
import com.kristalball.mams.repository.BaseRepository;
import com.kristalball.mams.repository.TransactionRepository;
import com.kristalball.mams.service.CurrentUserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// APIs for purchases, assignments, expenditures and the transaction list
@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final BaseRepository baseRepository;
    private final CurrentUserService currentUserService;

    public TransactionController(TransactionRepository transactionRepository,
                                 BaseRepository baseRepository,
                                 CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.baseRepository = baseRepository;
        this.currentUserService = currentUserService;
    }

    // Get the list of transactions with optional filters
    @GetMapping("/txns")
    public List<AssetTransaction> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long base,
            @RequestParam(required = false) EquipmentType equipmentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Long baseId = currentUserService.getBaseIdToUse(base);
        boolean isLogisticsOfficer = currentUserService.getCurrentUser().getRole() == Role.LOGISTICS_OFFICER;

        List<AssetTransaction> allTransactions = transactionRepository.search(baseId, equipmentType, to);
        List<AssetTransaction> result = new ArrayList<>();

        for (AssetTransaction transaction : allTransactions) {
            // filter by type
            if (type != null && transaction.getType() != type) {
                continue;
            }
            // filter by from date
            if (from != null && transaction.getTxnDate().isBefore(from)) {
                continue;
            }
            // logistics officer can only see purchases and transfers
            if (isLogisticsOfficer && !isPurchaseOrTransfer(transaction)) {
                continue;
            }
            result.add(transaction);
        }
        return result;
    }

    // all roles can add a purchase
    @PostMapping("/purchases")
    public AssetTransaction addPurchase(@RequestBody TransactionRequest request) {
        AssetTransaction transaction = buildTransaction(TransactionType.PURCHASE, request);
        return transactionRepository.save(transaction);
    }

    // only Admin and Base Commander can assign assets
    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public AssetTransaction addAssignment(@RequestBody TransactionRequest request) {
        AssetTransaction transaction = buildTransaction(TransactionType.ASSIGNED, request);
        return transactionRepository.save(transaction);
    }

    // only Admin and Base Commander can add expenditures
    @PostMapping("/expenditures")
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
    public AssetTransaction addExpenditure(@RequestBody TransactionRequest request) {
        AssetTransaction transaction = buildTransaction(TransactionType.EXPENDED, request);
        return transactionRepository.save(transaction);
    }

    private boolean isPurchaseOrTransfer(AssetTransaction transaction) {
        return transaction.getType() == TransactionType.PURCHASE
                || transaction.getType() == TransactionType.TRANSFER_IN
                || transaction.getType() == TransactionType.TRANSFER_OUT;
    }

    // Checks the request and creates the AssetTransaction object (not saved yet)
    private AssetTransaction buildTransaction(TransactionType type, TransactionRequest request) {
        if (request.getQuantity() <= 0
                || request.getAssetName() == null
                || request.getAssetName().isBlank()
                || request.getEquipmentType() == null) {
            throw badRequest("assetName, equipmentType and quantity>0 required");
        }

        // non admin users always use their own base
        Long baseId = currentUserService.getBaseIdToUse(request.getBaseId());
        if (baseId == null) {
            throw badRequest("baseId required");
        }

        AssetTransaction transaction = new AssetTransaction();
        transaction.setType(type);
        transaction.setEquipmentType(request.getEquipmentType());
        transaction.setAssetName(request.getAssetName().trim());
        transaction.setQuantity(request.getQuantity());
        transaction.setBase(baseRepository.findById(baseId).orElseThrow(() -> badRequest("Unknown base")));
        // if date is not given, use today
        transaction.setTxnDate(request.getDate() == null ? LocalDate.now() : request.getDate());
        transaction.setPersonnel(request.getPersonnel());
        transaction.setCreatedBy(currentUserService.getCurrentUser().getUsername());
        return transaction;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
