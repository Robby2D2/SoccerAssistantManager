package com.useunix.soccermanager.domain;

public enum Position {
	GOALIE("Goalie", PositionType.GOALIE)
	, RIGHT_FORWARD("Right Forward", PositionType.FORWARD)
	, LEFT_FORWARD("Left Forward", PositionType.FORWARD)
	, CENTER_FORWARD("Center Forward", PositionType.FORWARD)
	, RIGHT_DEFENSE("Right Defense", PositionType.DEFENSE)
	, LEFT_DEFENSE("Left Defense", PositionType.DEFENSE);
	
	String name;
    PositionType positionType;

	Position(String name, PositionType positionType) {
		this.name = name;
		this.positionType = positionType;
	}

	public String getName() {
		return name;
	}

    public PositionType getPositionType() {
        return positionType;
    }
}
