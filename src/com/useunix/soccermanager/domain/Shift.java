package com.useunix.soccermanager.domain;

import java.util.Date;

public class Shift {
	Long id;
	Date startTime;
	Long gameId;
	Long rank;

	public Shift() {
	}

	public Shift(Long id, Date startTime, Long gameId, Long rank) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.gameId = gameId;
		this.rank = rank;
	}

	public Shift(Date startTime, Long gameId, Long rank) {
		super();
		this.startTime = startTime;
		this.gameId = gameId;
		this.rank = rank;

	}

	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getGameId() {
		return gameId;
	}
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Long getRank() {
		return rank;
	}
	public void setRank(Long rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "Shift [id=" + id + ", startTime=" + startTime + ", gameId=" + gameId + ", rank=" + rank +"]";
	}
	
	
}
