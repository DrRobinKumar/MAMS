package com.kristalball.mams.repository;

import com.kristalball.mams.model.AssetTransaction;
import com.kristalball.mams.model.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<AssetTransaction, Long> {

    // Each filter is optional. If a value is null then that filter is ignored.
    @Query("select t from AssetTransaction t where (:baseId is null or t.base.id = :baseId) and (:equipmentType is null or t.equipmentType = :equipmentType) and (:toDate is null or t.txnDate <= :toDate) order by t.txnDate desc, t.id desc")
    List<AssetTransaction> search(@Param("baseId") Long baseId,
                                  @Param("equipmentType") EquipmentType equipmentType,
                                  @Param("toDate") LocalDate toDate);
}
