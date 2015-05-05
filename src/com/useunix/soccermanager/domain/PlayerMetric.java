package com.useunix.soccermanager.domain;

public class PlayerMetric {
	Long shiftId;
	Position position;
	Player player;
	
	public PlayerMetric() {
		super();
	}

	public PlayerMetric(Long shiftId, Position position, Player player) {
		super();
		this.shiftId = shiftId;
		this.position = position;
		this.player = player;
	}
	
	public Long getShiftId() {
		return shiftId;
	}
	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return "PlayerMetric [shiftId=" + shiftId + ", position=" + position
				+ ", player=" + player + "]";
	}
	
}
