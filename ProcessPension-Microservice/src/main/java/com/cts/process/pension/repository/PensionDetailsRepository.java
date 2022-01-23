package com.cts.process.pension.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.process.pension.model.PensionAmountDetail;

@Repository
public interface PensionDetailsRepository extends JpaRepository<PensionAmountDetail, String>{

}
