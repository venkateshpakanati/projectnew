package com.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.institution.Institution;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Integer> {

}
