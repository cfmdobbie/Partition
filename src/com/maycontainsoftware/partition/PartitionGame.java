package com.maycontainsoftware.partition;

import com.badlogic.gdx.Screen;
import com.maycontainsoftware.general.CGame;
import com.maycontainsoftware.general.CSoundEngine;

/**
 * The main Game instance.
 * 
 * @author Charlie
 */
public class PartitionGame extends CGame {

	/** Tag for logging purposes. */
	public static final String TAG = PartitionGame.class.getName();

	/** Construct the Game instance. This game is designed to render at 720x1000 resolution. */
	public PartitionGame() {
		super(720, 1000);
	}

	@Override
	protected final Screen initialScreen() {
		// Start with the preloading screen
		return new PreloadingScreen(this);
	}

	@Override
	protected CSoundEngine makeSoundEngine() {
		return new SoundEngine(manager);
	}
}
