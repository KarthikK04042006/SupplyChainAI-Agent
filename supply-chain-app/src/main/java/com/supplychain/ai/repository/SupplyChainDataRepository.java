package com.supplychain.ai.repository;

import com.supplychain.ai.model.SupplyChainData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SupplyChainDataRepository extends JpaRepository<SupplyChainData, Long> {
    @Query("SELECT DISTINCT s.region FROM SupplyChainData s")
    List<String> findDistinctRegions();

    @Query("SELECT DISTINCT s.category FROM SupplyChainData s")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT s.segment FROM SupplyChainData s")
    List<String> findDistinctSegments();
}
