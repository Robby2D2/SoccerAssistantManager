package com.useunix.soccermanager.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerMetricSummary {
	Player player;
	List<PlayerMetric> metrics;
	HashMap<Position, List<PlayerMetric>> positionsPlayed;
	
	public PlayerMetricSummary() {
		super();
		metrics = new ArrayList<PlayerMetric>();
		positionsPlayed = new HashMap<Position, List<PlayerMetric>>();
	}
	
	public void addPlayerMetric(PlayerMetric playerMetric) {
		this.player = playerMetric.getPlayer();
		metrics.add(playerMetric);
		List<PlayerMetric> existingMetrics = positionsPlayed.get(playerMetric.getPosition());
		if (existingMetrics == null) {
			existingMetrics = new ArrayList<PlayerMetric>();
		}
		existingMetrics.add(playerMetric);
		positionsPlayed.put(playerMetric.getPosition(), existingMetrics);
	}
	
	public Long getMostRecentShift() {
		Long mostRecentShift = -1l;
		for (PlayerMetric metric : metrics) {
			if (metric.getShiftId() > mostRecentShift) {
				mostRecentShift = metric.getShiftId();
			}
		}
		return mostRecentShift;
	}

	public Long getMostRecentShift(PositionType positionType) {
		Long mostRecentShift = 0l;
		for (PlayerMetric metric : metrics) {
			if (metric.getShiftId() > mostRecentShift && metric.getPosition().getPositionType() == positionType) {
				mostRecentShift = metric.getShiftId();
			}
		}
		return mostRecentShift;
	}

	public Long getMostRecentShift(Position position) {
		Long mostRecentShift = 0l;
		for (PlayerMetric metric : metrics) {
			if (metric.getShiftId() > mostRecentShift && metric.getPosition() == position) {
				mostRecentShift = metric.getShiftId();
			}
		}
		return mostRecentShift;
	}
	
	public int getTotalShiftsPlayed() {
		return metrics.size();
	}
	
	public int getTotalTimesPlayed(Position position) {
		List<PlayerMetric> shiftsAtPosition = positionsPlayed.get(position);
		return shiftsAtPosition == null ? 0 : shiftsAtPosition.size();
	}

	public int getTotalTimesPlayed(PositionType positionType) {
        int timesAtPositionType= 0;
        for (PlayerMetric playerMetric : metrics) {
            if (playerMetric.getPosition().getPositionType() == positionType) {
                timesAtPositionType++;
            }
        }
		return timesAtPositionType;
	}

	@Override
	public String toString() {
        String metricsString = "";
        for (PlayerMetric metric : metrics) {
            metricsString += " " + metric.toString();
        }
        return "PlayerMetricSummary [player=" + player + ", metrics=" + metricsString
				+ ", positionsPlayed=" + positionsPlayed + "]";
	}

    public List<PlayerMetric> getMetrics() {
        return metrics;
    }
}
