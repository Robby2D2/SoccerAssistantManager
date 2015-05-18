/*
 * Copyright (c)  2015 Danek Consulting Company
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.useunix.soccermanager.domain;

public class PlayerShift {
	Long id;
	Long shiftId;
	Long playerId;
	String position;

	public PlayerShift() {
		super();
	}

	public PlayerShift(Long id, Long shiftId, Long playerId, String position) {
		this.id = id;
		this.shiftId = shiftId;
		this.playerId = playerId;
		this.position = position;
	}

	public PlayerShift(Long shiftId, Long playerId, String position) {
		this.shiftId = shiftId;
		this.playerId = playerId;
		this.position = position;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getShiftId() {
		return shiftId;
	}

	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "PlayerShift{" +
				"shiftId=" + shiftId +
				", playerId=" + playerId +
				", position='" + position + '\'' +
				'}';
	}
}
