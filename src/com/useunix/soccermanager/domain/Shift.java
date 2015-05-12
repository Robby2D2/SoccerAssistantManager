package com.useunix.soccermanager.domain;

import java.util.Date;

public class Shift {
	Long id;
	Date startTime;
	String opponent;
	Long currentShiftId;

	public Shift() {
	}


	public Shift(Long id, Date startTime, String opponent, Long currentShiftId) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.opponent = opponent;
		this.currentShiftId = currentShiftId;
	}


	public Shift(Date startTime, String opponent, Long currentShiftId) {
		super();
		this.startTime = startTime;
		this.opponent = opponent;
		this.currentShiftId = currentShiftId;
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

	public Long getCurrentShiftId() {
		return currentShiftId;
	}
	public void setCurrentShiftId(Long currentShiftId) {
		this.currentShiftId = currentShiftId;
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", startTime=" + startTime + ", opponent="
				+ opponent + ", currentShiftId=" + currentShiftId +"]";
	}
	
	
}
