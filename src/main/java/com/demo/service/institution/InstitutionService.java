package com.demo.service.institution;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.demo.domain.institution.Institution;
import com.demo.repo.InstitutionRepository;

@Service
public class InstitutionService {
	
	@Autowired
	InstitutionRepository repository;
	
	@Cacheable(value = "institutions")
    public List<Institution> getAllInstitutions() {
		List<Institution> listOfInstitutions = repository.findAll(); 
        return listOfInstitutions;
    }
	
	@Cacheable(value = "institute", key="#id")
    public Optional<Institution> findInstituteById(int id) {
		Optional<Institution> institution = repository.findById(id);
		return institution;
    }
	
	@Caching(evict = {
            @CacheEvict(value="institute", allEntries=true),
            @CacheEvict(value="institutions", allEntries=true) })
    public Institution addInstitute(Institution institute) {
		return repository.save(institute);
    }
	
	@Caching(evict = {
            @CacheEvict(value="institute", allEntries=true),
            @CacheEvict(value="institutions", allEntries=true) })
    public void deleteInstituteById(int id) {
		 repository.deleteById(id);
    }
}
