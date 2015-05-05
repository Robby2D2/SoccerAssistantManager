package com.useunix.soccermanager.domain;

import java.util.Date;

public class Game {
	Long id;
	Date startTime;
	String opponent;
	
	public Game() {
	}
	
	
	public Game(Long id, Date startTime, String opponent) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.opponent = opponent;
	}


	public Game(Date startTime, String opponent) {
		super();
		this.startTime = startTime;
		this.opponent = opponent;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public String getOpponent() {
		return opponent;
	}


	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return "Game [id=" + id + ", startTime=" + startTime + ", opponent="
				+ opponent + "]";
	}
	
	
}
