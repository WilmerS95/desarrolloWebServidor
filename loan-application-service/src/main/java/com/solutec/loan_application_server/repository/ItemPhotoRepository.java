package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.Item;
import com.solutec.loan_application_server.entity.ItemPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPhotoRepository extends JpaRepository<ItemPhoto, Long> {
    List<ItemPhoto> findByItem(Item item);
}