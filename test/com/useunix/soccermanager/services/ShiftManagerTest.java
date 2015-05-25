package com.useunix.soccermanager.services;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.useunix.soccermanager.domain.PlayerMetricSummary;
import org.junit.Before;
import org.junit.Test;

import com.useunix.soccermanager.domain.Player;
import com.useunix.soccermanager.domain.PlayerMetric;
import com.useunix.soccermanager.domain.Position;

public class ShiftManagerTest {

	ShiftManager shiftManager;
	private List<Player> allPlayers;
	
	@Before
	public void setup() {
		allPlayers = Arrays.asList(
			  new Player(1l, 1l, "Rob", "Danek")
			 ,new Player(2l, 1l, "Sarita", "Parikh")
			 ,new Player(3l, 1l, "Milin", "Danek")
			 ,new Player(4l, 1l, "Kirin", "Danek")
			 ,new Player(5l, 1l, "Ronin", "Danek")
			 ,new Player(6l, 1l, "Dappy", "Danek")
			 ,new Player(7l, 1l, "Grandpa", "Danek")
			 ,new Player(8l, 1l, "Riley", "Lyons")
			 ,new Player(9l, 1l, "Amy", "Lyons")
//			 ,new Player(10l, 1l, "Dave", "Lyons")
//			 ,new Player(11l, 1l, "Steve", "Danek")
//			 ,new Player(12l, 1l, "Oscar", "Danek")
//			 ,new Player(13l, 1l, "Sonia", "Pai")
		);
		shiftManager = new ShiftManager(new Long(1), allPlayers);


	}
	
	@Test
	public void testSetupDataCorrectly() {

        createBaselineMetricData();

		assert shiftManager.getPlayersForShift(9l).size() == 6;
        shiftManager.getPlayersForShift(10l);
	}

    private void createBaselineMetricData() {
        int playerCount = 0;
        for (long shiftId = 0; shiftId < 10; shiftId++) {
            List<PlayerMetric> shiftPlayerPositions = new ArrayList<PlayerMetric>();
            for (Position position : Position.values()) {
                shiftPlayerPositions.add(new PlayerMetric(shiftId, position, allPlayers.get(playerCount)));
                playerCount++;
                if (playerCount == allPlayers.size()) {
                    playerCount = 0;
                }
            }
            shiftManager.updateMetrics(shiftId, shiftPlayerPositions);
        }
    }

    @Test
	public void testGetPlayersForShift() {
		for (long shiftId = 0; shiftId < 10; shiftId++) {
			List<PlayerMetric> playersForShift = shiftManager.getPlayersForShift(shiftId);
			assertEquals(Position.values().length, playersForShift.size());
            assertFairMetrics(shiftId, shiftManager.playerStats);
		}
	}

    private boolean isInPlayerMetricList(List<PlayerMetric> playerMetrics, Player player) {
        for (PlayerMetric playerMetric : playerMetrics) {
            if (playerMetric.getPlayer() == player) {
                return true;
            }
        }
        return false;
    }

    private void assertFairMetrics(long shiftId, HashMap<Player, PlayerMetricSummary> playerStats) {
        List<PlayerMetric> playersInShift = shiftManager.getPlayersForShift(shiftId);
        List<Player> playersOnBench = new ArrayList<Player>();
        for (Player player : shiftManager.getAllPlayers()) {
            if (!isInPlayerMetricList(playersInShift, player)) {
                playersOnBench.add(player);
            }
        }

        // Assert shift has the appropriate players in it.
        for (PlayerMetric shiftPlayerMetric : playersInShift) {
            PlayerMetricSummary playerInShiftMetricSummary = playerStats.get(shiftPlayerMetric.getPlayer());
            // Make sure no other players have played less or less recently
            for (Player playerOnBench : playersOnBench) {
                PlayerMetricSummary playerOnBenchMetricSummary = playerStats.get(playerOnBench);
                int benchPlayerShiftsPlayed = 0;
                Long benchPlayerMostRecentShift = -1l;
                Long benchPlayerShiftPlayingPriority = 0l;
                if (playerOnBenchMetricSummary != null) {
                    benchPlayerShiftsPlayed = playerOnBenchMetricSummary.getTotalShiftsPlayed();
                    benchPlayerMostRecentShift = playerOnBenchMetricSummary.getMostRecentShift();
                    benchPlayerShiftPlayingPriority = playerOnBenchMetricSummary.getShiftPlayingPriority();
                }
                assertTrue(benchPlayerShiftsPlayed >= playerInShiftMetricSummary.getTotalShiftsPlayed() - 1);
                if (benchPlayerShiftsPlayed == playerInShiftMetricSummary.getTotalShiftsPlayed()) {
                    assertTrue(benchPlayerMostRecentShift >= getPreviousShiftPlayed(shiftId, playerInShiftMetricSummary));
                }

                assertTrue(benchPlayerShiftPlayingPriority >= (playerInShiftMetricSummary.getShiftPlayingPriority() - Math.pow(10, shiftId)));
            }
        }

    }

    private Long getPreviousShiftPlayed(long currentShiftId, PlayerMetricSummary playerInShiftMetricSummary) {
        List<PlayerMetric> metrics = playerInShiftMetricSummary.getMetrics();

        Long mostRecentShift = 0l;
        for (PlayerMetric metric : metrics) {
            if (metric.getShiftId() > mostRecentShift && metric.getShiftId() < currentShiftId) {
                mostRecentShift = metric.getShiftId();
            }
        }
        return mostRecentShift;

    }

}
