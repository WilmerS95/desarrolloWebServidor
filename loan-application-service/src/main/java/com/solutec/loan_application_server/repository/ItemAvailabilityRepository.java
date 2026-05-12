package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.Item;
import com.solutec.loan_application_server.entity.ItemAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemAvailabilityRepository extends JpaRepository<ItemAvailability, Long> {

    Optional<ItemAvailability> findByItem(Item item);

    @Query("SELECT ia FROM ItemAvailability ia WHERE ia.item.itemID = :itemId")
    Optional<ItemAvailability> findByItemId(Long itemId);

    List<ItemAvailability> findByStatus(String status);

    List<ItemAvailability> findByStatusOrderByAvailableSinceDesc(String status);

    List<ItemAvailability> findByTransferReasonAndStatus(String transferReason, String status);

    boolean existsByItem(Item item);

    @Query("SELECT COUNT(ia) > 0 FROM ItemAvailability ia WHERE ia.item.itemID = :itemId AND ia.status = 'AVAILABLE'")
    boolean isItemAvailable(Long itemId);
}