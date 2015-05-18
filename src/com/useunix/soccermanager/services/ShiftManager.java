package com.useunix.soccermanager.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.useunix.soccermanager.domain.Player;
import com.useunix.soccermanager.domain.PlayerMetric;
import com.useunix.soccermanager.domain.PlayerMetricSummary;
import com.useunix.soccermanager.domain.Position;

public class ShiftManager implements Parcelable {
	List<Player> allPlayers;
	HashMap<Long, List<PlayerMetric>> shifts;
	HashMap<Long, Date> shiftStartTimes;
	HashMap<Player, PlayerMetricSummary> playerStats;
	private SecureRandom secureRandom;
	Long gameId;

	public ShiftManager() {
		this(new Long(1), null);
	}

	public ShiftManager(Long gameId, List<Player> allPlayers) {
		super();
		this.gameId = gameId;
		this.allPlayers = allPlayers;
		secureRandom = new SecureRandom();
	}

	public List<PlayerMetric> getPlayersForShift(Long shiftId) {
		List<PlayerMetric> shiftPlayerPositions = new ArrayList<PlayerMetric>();

		if (allPlayers == null || allPlayers.isEmpty()) {
			return shiftPlayerPositions;
		}
		if (allPlayers.size() < Position.values().length) {
			return shiftPlayerPositions;
		}

		if (shifts == null) {
			shifts = new HashMap<Long, List<PlayerMetric>>();
		}

		if (playerStats == null) {
			playerStats = new HashMap<Player, PlayerMetricSummary>();
		}

		// Need to create a new shift.
		if (shifts.size() <= shiftId) {
			List<Player> playersInShift = determinePlayersInNextShift();

			// Assign positions to players.
			for (Position position : Position.values()) {
				// Determine who has played the position the least, then take
				// random
				List<Player> playersWithLeastAmountOfTimesAtPosition = determinePlayersWhoHavePlayedPositionTheLeast(playersInShift, position);

				int randomIndex = secureRandom.nextInt(playersWithLeastAmountOfTimesAtPosition.size());
				shiftPlayerPositions.add(new PlayerMetric(shiftId, position, playersWithLeastAmountOfTimesAtPosition.get(randomIndex)));
				playersInShift.remove(playersWithLeastAmountOfTimesAtPosition.get(randomIndex));
			}

			updateMetrics(shiftId, shiftPlayerPositions);
		} else {
			shiftPlayerPositions = shifts.get(shiftId);
		}

		return shiftPlayerPositions;
	}

	public void updateMetrics(Long shiftId, List<PlayerMetric> shiftPlayerPositions) {
		if (shifts == null) {
			shifts = new HashMap<Long, List<PlayerMetric>>();
		}
		if (playerStats == null) {
			playerStats = new HashMap<Player, PlayerMetricSummary>();
		}
		shifts.put(shiftId, shiftPlayerPositions);
		for (PlayerMetric playerMetric : shiftPlayerPositions) {
			PlayerMetricSummary playerMetricSummary = playerStats.get(playerMetric.getPlayer());
			if (playerMetricSummary == null) {
				playerMetricSummary = new PlayerMetricSummary();
				playerStats.put(playerMetric.getPlayer(), playerMetricSummary);
			}
			playerMetricSummary.addPlayerMetric(playerMetric);
		}
	}

	private List<Player> determinePlayersWhoHavePlayedPositionTheLeast(List<Player> playersInShift, Position position) {
		int minNumTimesAtPosition = Integer.MAX_VALUE;
		List<Player> playersWithLeastAmountOfTimesAtPosition = new ArrayList<Player>();
		for (Player player : playersInShift) {
			int numTimesAtPosition = playerStats.get(player) == null ? 0 : playerStats.get(player).getTotalTimesPlayed(position);
			if (numTimesAtPosition == minNumTimesAtPosition) {
				playersWithLeastAmountOfTimesAtPosition.add(player);
			} else if (numTimesAtPosition < minNumTimesAtPosition) {
				minNumTimesAtPosition = numTimesAtPosition;
				playersWithLeastAmountOfTimesAtPosition = new ArrayList<Player>();
				playersWithLeastAmountOfTimesAtPosition.add(player);
			}
		}
		return playersWithLeastAmountOfTimesAtPosition;
	}

	private List<Player> determinePlayersWhoHavePlayedLeastRecently(List<Player> playersInShift) {
		Long minMostRecentShift = Long.MAX_VALUE;
		List<Player> playersWhoHaveNotPlayedRecently = new ArrayList<Player>();
		for (Player player : playersInShift) {
			Long mostRecentShift = playerStats.get(player) == null ? 0l : playerStats.get(player).getMostRecentShift();
			if (mostRecentShift == minMostRecentShift) {
				playersWhoHaveNotPlayedRecently.add(player);
			} else if (mostRecentShift < minMostRecentShift) {
				minMostRecentShift = mostRecentShift;
				playersWhoHaveNotPlayedRecently = new ArrayList<Player>();
				playersWhoHaveNotPlayedRecently.add(player);
			}
		}
		return playersWhoHaveNotPlayedRecently;
	}

	private List<Player> determinePlayersInNextShift() {
		List<Player> playersInShift = new ArrayList<Player>();
		List<Player> playersOnBench = new ArrayList<Player>();
		playersOnBench.addAll(allPlayers);

		int count = 0;
		while (playersInShift.size() != Position.values().length && count++ < 100) {
			List<Player> whoIsUpNext = whoHasPlayedTheLeast(playersOnBench);

			// Need to find more players for the shift.
			if ((whoIsUpNext.size() + playersInShift.size()) <= Position.values().length) {
				playersOnBench.removeAll(whoIsUpNext);
				playersInShift.addAll(whoIsUpNext);

				// Found too many players
			} else if ((whoIsUpNext.size() + playersInShift.size()) > Position.values().length) {
				int numPlayersToAdd = Position.values().length - playersInShift.size();
				if (numPlayersToAdd > whoIsUpNext.size()) {
					numPlayersToAdd = whoIsUpNext.size();
				}

				// Determine who was in most recently
				List<Player> leastActivePlayers = determinePlayersWhoHavePlayedLeastRecently(whoIsUpNext);
				while (leastActivePlayers.size() < numPlayersToAdd && count++ < 100) {
					whoIsUpNext.removeAll(leastActivePlayers);
					leastActivePlayers.addAll(determinePlayersWhoHavePlayedLeastRecently(whoIsUpNext));
				}

				for (int i = 0; i < numPlayersToAdd; i++) {
					int randomIndex = secureRandom.nextInt(leastActivePlayers.size());
					Player player = leastActivePlayers.get(randomIndex);
					leastActivePlayers.remove(player);
					playersOnBench.remove(player);
					playersInShift.add(player);
				}
			}
		}
		return playersInShift;
	}

	private List<Player> whoHasPlayedTheLeast(List<Player> players) {
		List<Player> whoIsUpNext = new ArrayList<Player>();
		int minShiftsPlayed = Integer.MAX_VALUE;

		// Find the players who have played the least and make sure they are
		// assigned a position.
		for (Player player : players) {
			PlayerMetricSummary summary = playerStats.get(player);

			if (summary == null) {
				summary = new PlayerMetricSummary();
			}

			int totalShiftsPlayed = summary.getTotalShiftsPlayed();
			if (totalShiftsPlayed == minShiftsPlayed) {
				whoIsUpNext.add(player);
			} else if (totalShiftsPlayed < minShiftsPlayed) {
				minShiftsPlayed = totalShiftsPlayed;
				whoIsUpNext = new ArrayList<Player>();
				whoIsUpNext.add(player);
			}
		}
		return whoIsUpNext;
	}

	public List<Player> getAllPlayers() {
		return allPlayers;
	}

	public void setAllPlayers(List<Player> allPlayers) {
		this.allPlayers = allPlayers;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//int x = 1;
	}

	public Date getStartTimeForShift(Long shiftId) {
		if (shiftStartTimes == null) return null;

		return shiftStartTimes.get(shiftId);
	}

	public void startShift(Long shiftId) {
		if (shiftStartTimes == null) {
			shiftStartTimes = new HashMap<Long, Date>();
		}
		if (!shiftStartTimes.containsKey(shiftId)) {
			shiftStartTimes.put(shiftId, new Date());
		}
	}

	public HashMap<Long, Date> getShiftStartTimes() {
		return shiftStartTimes;
	}

	public void setShiftStartTimes(HashMap<Long, Date> shiftStartTimes) {
		this.shiftStartTimes = shiftStartTimes;
	}
}
