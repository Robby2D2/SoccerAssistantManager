package com.useunix.soccermanager.domain;

import java.util.ArrayList;

public class PlayerManager {
	private static PlayerManager playerManager = new PlayerManager();
	private static long playerId = 1000;
	ArrayList<Player> players;
	
	private PlayerManager() {
		players = new ArrayList<Player>();
	}
	
	public static PlayerManager getInstance() {
		return playerManager;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public void delete(int id) {
		Player player = get(id);
		if (player != null) {
			players.remove(player);
		}
	}
	
	public Player create(String firstName, String lastName) {
		Player player = new Player(playerId++, firstName, lastName);
		add(player);
		return player;
	}
	
	public void add(Player player) {
		players.add(player);
	}
	
	public Player get(long id) {
		for (Player player : players) {
			if (player.id == id) {
				return player;
			}
		}
		return null;
	}
}
