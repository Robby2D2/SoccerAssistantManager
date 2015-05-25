package com.useunix.soccermanager.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.useunix.soccermanager.domain.*;

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
				// Determine who has played the position the least, then take random
				List<Player> playersWithLeastAmountOfTimesAtPositionType = determinePlayersWhoHavePlayedPositionTypeTheLeast(playersInShift, position.getPositionType());
				playersWithLeastAmountOfTimesAtPositionType = determinePlayersWhoHavePlayedLeastRecently(playersWithLeastAmountOfTimesAtPositionType, position.getPositionType());
				playersWithLeastAmountOfTimesAtPositionType = determinePlayersWhoHavePlayedPositionTheLeast(playersWithLeastAmountOfTimesAtPositionType, position);
				playersWithLeastAmountOfTimesAtPositionType = determinePlayersWhoHavePlayedLeastRecently(playersWithLeastAmountOfTimesAtPositionType, position);

				int randomIndex = secureRandom.nextInt(playersWithLeastAmountOfTimesAtPositionType.size());
				shiftPlayerPositions.add(new PlayerMetric(shiftId, position, playersWithLeastAmountOfTimesAtPositionType.get(randomIndex)));
				playersInShift.remove(playersWithLeastAmountOfTimesAtPositionType.get(randomIndex));
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

	private List<Player> determinePlayersWhoHavePlayedPositionTypeTheLeast(List<Player> players, PositionType positionType) {
		int minNumTimesAtPositionType = Integer.MAX_VALUE;
		List<Player> playersWithLeastAmountOfTimesAtPositionType = new ArrayList<Player>();
		for (Player player : players) {
			int numTimesAtPosition = playerStats.get(player) == null ? 0 : playerStats.get(player).getTotalTimesPlayed(positionType);
			if (numTimesAtPosition == minNumTimesAtPositionType) {
				playersWithLeastAmountOfTimesAtPositionType.add(player);
			} else if (numTimesAtPosition < minNumTimesAtPositionType) {
				minNumTimesAtPositionType = numTimesAtPosition;
				playersWithLeastAmountOfTimesAtPositionType = new ArrayList<Player>();
				playersWithLeastAmountOfTimesAtPositionType.add(player);
			}
		}
		return playersWithLeastAmountOfTimesAtPositionType;
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

	private List<Player> determinePlayersWhoHavePlayedLeastRecently(List<Player> playersInShift, PositionType positionType) {
		Long minMostRecentShift = Long.MAX_VALUE;
		List<Player> playersWhoHaveNotPlayedRecently = new ArrayList<Player>();
		for (Player player : playersInShift) {
			Long mostRecentShift = playerStats.get(player) == null ? 0l : playerStats.get(player).getMostRecentShift(positionType);
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

	private List<Player> determinePlayersWhoHavePlayedLeastRecently(List<Player> playersInShift, Position position) {
		Long minMostRecentShift = Long.MAX_VALUE;
		List<Player> playersWhoHaveNotPlayedRecently = new ArrayList<Player>();
		for (Player player : playersInShift) {
			Long mostRecentShift = playerStats.get(player) == null ? 0l : playerStats.get(player).getMostRecentShift(position);
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
        String metricsString = metricsToString();
        System.out.println(metricsString);

		List<Player> playersInShift = new ArrayList<Player>();
		List<Player> playersOnBench = new ArrayList<Player>();
		playersOnBench.addAll(allPlayers);

        for (int count = 0; count < Position.values().length; count++) {
            Player nextOneIn = null;
            List<Player> tiedPlayersForNextOneIn = new ArrayList<Player>();
            for (Player playerOnBench : playersOnBench) {
                if (nextOneIn == null) {
                    nextOneIn = playerOnBench;
                    tiedPlayersForNextOneIn = new ArrayList<Player>();
                    tiedPlayersForNextOneIn.add(playerOnBench);
                } else {
                    PlayerMetricSummary nextOneInSummary = playerStats.get(nextOneIn);
                    PlayerMetricSummary playerOnBenchSummary = playerStats.get(playerOnBench);
                    long playerOnBenchSummaryShiftPlayingPriority = 0l;
                    if (playerOnBenchSummary != null) {
                        playerOnBenchSummaryShiftPlayingPriority = playerOnBenchSummary.getShiftPlayingPriority();
                    }
                    long nextOneInSummaryShiftPlayingPriority = 0l;
                    if (nextOneInSummary != null) {
                        nextOneInSummaryShiftPlayingPriority = nextOneInSummary.getShiftPlayingPriority();
                    }
                    if (playerOnBenchSummaryShiftPlayingPriority < nextOneInSummaryShiftPlayingPriority) {
                        tiedPlayersForNextOneIn = new ArrayList<Player>();
                        tiedPlayersForNextOneIn.add(playerOnBench);
                        nextOneIn = playerOnBench;
                    } else if (playerOnBenchSummaryShiftPlayingPriority == nextOneInSummaryShiftPlayingPriority) {
                        tiedPlayersForNextOneIn.add(playerOnBench);
                    }
                }
            }
            int randomIndex = secureRandom.nextInt(tiedPlayersForNextOneIn.size());
            nextOneIn = tiedPlayersForNextOneIn.get(randomIndex);
            playersOnBench.remove(nextOneIn);
            playersInShift.add(nextOneIn);
        }

		return playersInShift;
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

    public String metricsToString() {
        String metricsString = "";
        for (Player player : playerStats.keySet()) {
            String positionsPlayedString = "";
            PlayerMetricSummary playerMetricSummary = playerStats.get(player);
            List<PlayerMetric> metrics = playerMetricSummary.getMetrics();
            for (PlayerMetric metric : metrics) {
                positionsPlayedString += ", " + metric.getShiftId() + ": " + metric.getPosition();
            }
            metricsString += "\n" + player.getFirstName() + " " + player.getLastName() + ": " + metrics.size() + positionsPlayedString;
        }
        return metricsString;
    }
}
