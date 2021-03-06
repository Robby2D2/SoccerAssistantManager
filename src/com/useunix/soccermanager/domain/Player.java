package com.useunix.soccermanager.domain;

import java.io.Serializable;

public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	Long id;
	Long teamId;
	String firstName;
	String lastName;
	boolean isPresent;
	
	public Player() {
	}
	
	public Player(Long id, Long teamId, String firstName, String lastName) {
		this.id = id;
		this.teamId = teamId;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Player(Long teamId, String firstName, String lastName) {
		this.teamId = teamId;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean isPresent() {
		return isPresent;
	}
	public void setIsPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", isPresent=" + isPresent + "]";
	}
	
	
}
