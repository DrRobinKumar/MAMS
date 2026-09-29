package com.kristalball.mams.controller;

import com.kristalball.mams.dto.TransferRequest;
import com.kristalball.mams.model.AssetTransaction;
import com.kristalball.mams.model.Base;
import com.kristalball.mams.model.Transfer;
import com.kristalball.mams.model.TransactionType;
import com.kristalball.mams.repository.BaseRepository;
import com.kristalball.mams.repository.TransactionRepository;
import com.kristalball.mams.repository.TransferRepository;
import com.kristalball.mams.service.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

// APIs for transferring assets from one base to another
@RestController
@RequestMapping("/api")
public class TransferController {

    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;
    private final BaseRepository baseRepository;
    private final CurrentUserService currentUserService;

    public TransferController(TransferRepository transferRepository,
                              TransactionRepository transactionRepository,
                              BaseRepository baseRepository,
                              CurrentUserService currentUserService) {
        this.transferRepository = transferRepository;
        this.transactionRepository = transactionRepository;
        this.baseRepository = baseRepository;
        this.currentUserService = currentUserService;
    }

    // Do a new transfer.
    // @Transactional means if something fails in the middle, nothing gets saved.
    @PostMapping("/transfers")
    @Transactional
    public Transfer createTransfer(@RequestBody TransferRequest request) {
        if (request.getQuantity() <= 0
                || request.getFromBaseId() == null
                || request.getToBaseId() == null
                || request.getFromBaseId().equals(request.getToBaseId())) {
            throw badRequest("Invalid transfer");
        }

        // only admin can transfer from any base, others only from their own base
        boolean isAdmin = currentUserService.isAdmin();
        Long myBaseId = isAdmin ? null : currentUserService.getCurrentUser().getBase().getId();
        if (!isAdmin && !request.getFromBaseId().equals(myBaseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can only transfer out of your own base");
        }

        Base fromBase = baseRepository.findById(request.getFromBaseId())
                .orElseThrow(() -> badRequest("Unknown from base"));
        Base toBase = baseRepository.findById(request.getToBaseId())
                .orElseThrow(() -> badRequest("Unknown to base"));

        // 1. save the transfer record (for history)
        Transfer transfer = new Transfer();
        transfer.setFromBase(fromBase);
        transfer.setToBase(toBase);
        transfer.setEquipmentType(request.getEquipmentType());
        transfer.setAssetName(request.getAssetName());
        transfer.setQuantity(request.getQuantity());
        transfer.setCreatedBy(currentUserService.getCurrentUser().getUsername());
        transfer = transferRepository.save(transfer);

        // 2. save two rows in transaction table: one for from base and one for to base
        saveTransferRow(TransactionType.TRANSFER_OUT, fromBase, transfer);
        saveTransferRow(TransactionType.TRANSFER_IN, toBase, transfer);

        return transfer;
    }

    // Get the transfer history
    @GetMapping("/transfers")
    public List<Transfer> getTransferHistory(@RequestParam(required = false) Long base) {
        Long baseId = currentUserService.getBaseIdToUse(base);
        return transferRepository.history(baseId);
    }

    private void saveTransferRow(TransactionType type, Base base, Transfer transfer) {
        AssetTransaction row = new AssetTransaction();
        row.setType(type);
        row.setEquipmentType(transfer.getEquipmentType());
        row.setAssetName(transfer.getAssetName());
        row.setQuantity(transfer.getQuantity());
        row.setBase(base);
        row.setTxnDate(LocalDate.now());
        row.setTransferId(transfer.getId());
        row.setCreatedBy(transfer.getCreatedBy());
        transactionRepository.save(row);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
