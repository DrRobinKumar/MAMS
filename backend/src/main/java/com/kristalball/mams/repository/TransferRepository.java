package com.kristalball.mams.repository;

import com.kristalball.mams.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    // if baseId is null we return all transfers, otherwise only the transfers of that base
    @Query("select t from Transfer t where :baseId is null or t.fromBase.id = :baseId or t.toBase.id = :baseId order by t.createdAt desc")
    List<Transfer> history(@Param("baseId") Long baseId);
}
