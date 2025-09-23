package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.ItemPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPhotoRepository extends JpaRepository<ItemPhoto, Long> {}