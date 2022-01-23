package com.cts.process.pension.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.process.pension.model.PensionerInput;

@Repository
public interface PensionerDetailsRepository extends JpaRepository<PensionerInput, String> {

}
