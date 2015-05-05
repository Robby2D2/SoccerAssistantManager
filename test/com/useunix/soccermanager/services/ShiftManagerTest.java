package com.useunix.soccermanager.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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
			  new Player(1l, "Rob", "Danek")
			 ,new Player(2l, "Sarita", "Parikh")
			 ,new Player(3l, "Milin", "Danek")
			 ,new Player(4l, "Kirin", "Danek")
			 ,new Player(5l, "Ronin", "Danek")
			 ,new Player(6l, "Dappy", "Danek")
			 ,new Player(7l, "Grandpa", "Danek")
			 ,new Player(8l, "Riley", "Lyons")
			 ,new Player(9l, "Amy", "Lyons")
			 ,new Player(10l, "Dave", "Lyons")
			 ,new Player(11l, "Steve", "Danek")
			 ,new Player(12l, "Oscar", "Danek")
			 ,new Player(13l, "Sonia", "Pai")
		);
		shiftManager = new ShiftManager(allPlayers);
	}
	
	@Test
	public void testGetPlayersForShift() {
		for (int i = 0; i < 10; i++) {
			List<PlayerMetric> playersForShift = shiftManager.getPlayersForShift((long)i);
			assertEquals(Position.values().length, playersForShift.size());
		}
	}

}
