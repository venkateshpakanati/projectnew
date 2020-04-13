package com.demo.domain.mobile;

import org.springframework.boot.autoconfigure.domain.EntityScan;

public class Mobile {
	private Long id;
    private String name;
    
    public Mobile() {
    	super();
    }
	public Mobile(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
	@Override
    public String toString() {
        return String.format("Mobile [id=%s, name=%s]", id, name);
    }
}
