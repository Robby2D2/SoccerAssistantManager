package com.useunix.soccermanager.domain;

public enum Position {
	GOALIE("Goalie")
	, RIGHT_FORWARD("Right Forward")
	, LEFT_FORWARD("Left Forward")
	, CENTER_FORWARD("Center Forward")
	, RIGHT_DEFENSE("Right Defense")
	, LEFT_DEFENSE("Left Defense");
	
	String name;
	
	Position(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
