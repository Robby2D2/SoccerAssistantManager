package com.useunix.soccermanager.domain;

import com.useunix.soccermanager.services.ShiftManager;

import java.util.Date;

public class Game {
	Long id;
	Date startTime;
	String opponent;
	Long currentShiftId;
    Long teamId;

    public Game() {
	}

	public Game(Long id, Date startTime, String opponent, Long currentShiftId, Long teamId) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.opponent = opponent;
		this.currentShiftId = currentShiftId;
		this.teamId = teamId;
	}

	public Game(Date startTime, String opponent, Long currentShiftId, Long teamId) {
		super();
		this.startTime = startTime;
		this.opponent = opponent;
		this.currentShiftId = currentShiftId;
        this.teamId = teamId;
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

    public Long getTeamId() {
        return teamId;
    }
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    @Override
	public String toString() {
		return "Game [id=" + id + ", startTime=" + startTime + ", opponent="
				+ opponent + ", currentShiftId=" + currentShiftId + ", teamId=" + teamId +"]";
	}

}
