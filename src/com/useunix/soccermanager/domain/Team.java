package com.useunix.soccermanager.domain;

public class Team {
	Long id;
	String name;

	public Team() {
	}

	public Team(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Team(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Game [id=" + id +  ", name=" + name + "]";
	}
	
}
