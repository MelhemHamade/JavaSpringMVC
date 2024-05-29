package org.melhem.hopital.repository;

import org.melhem.hopital.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	Page<Patient> findByNomContainingIgnoreCase(String keyword, PageRequest of);

}
