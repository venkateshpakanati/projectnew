package com.demo.controller.institution;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.domain.institution.Institution;
import com.demo.service.institution.InstitutionService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/institutions")
public class InstituteController {

	@Autowired
    InstitutionService instituteService;
 
    @GetMapping(path = "")
    @ApiOperation(value = "Get All Institutions")
    public List<Institution> getAllInstitutions()  {        
        return instituteService.getAllInstitutions();
    }
    
    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Get Institution by Id provides in params")
	public Optional<Institution> findInstituteById(@PathVariable int id) {
	    System.out.println("Searching by ID  : " + id);
	    return instituteService.findInstituteById(id); 
	}
    
    @PostMapping(path = "/add")
    @ApiOperation(value = "Add new Institution")
	public Institution addInstitute(@RequestBody Institution institute) {
    	return instituteService.addInstitute(institute);
	}
    
    @GetMapping(path = "/delete/{id}")
    @ApiOperation(value = "Delete Institution by Id")
	public void deleteInstituteById(@PathVariable int id) {
    	instituteService.deleteInstituteById(id);
    }
	 
}
