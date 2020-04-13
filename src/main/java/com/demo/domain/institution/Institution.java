package com.demo.domain.institution;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.swagger.annotations.ApiModel;

@Entity
@ApiModel(description="All details about the Institution. ")
public class Institution {
	@Id
	private int inst_id;
	private String inst_name;
	
	public Institution() {
		
	}
	
	public Institution(int inst_id, String inst_name) {
		this.inst_id = inst_id;
		this.inst_name = inst_name;
	}
	public int getInst_id() {
		return inst_id;
	}
	public void setInst_id(int inst_id) {
		this.inst_id = inst_id;
	}
	public String getInst_name() {
		return inst_name;
	}
	public void setInst_name(String inst_name) {
		this.inst_name = inst_name;
	}
	
}
