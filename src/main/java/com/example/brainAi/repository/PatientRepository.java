package com.example.brainAi.repository;

import com.example.brainAi.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query(value = "SELECT p FROM Patient p WHERE p.email = :email")
    Patient findPatientByEmail(@Param("email") String email) throws Exception;
}