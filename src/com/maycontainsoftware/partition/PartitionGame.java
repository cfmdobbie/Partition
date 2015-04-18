package com.maycontainsoftware.partition;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;

/**
 * The main Game instance.
 * 
 * @author Charlie
 */
public class PartitionGame extends CGame {

	/** Tag for logging purposes. */
	public static final String TAG = PartitionGame.class.getName();

	/** Sound setting. For now, defaults to true and changes are not persisted. */
	boolean sound = true;

	/** Enumeration of player configurations. */
	public static enum PlayerConfiguration {
		TWO_PLAYER,
		ONE_PLAYER_VS_COMPUTER,
	}

	/** Enumeration of implemented boards. */
	public static enum BoardConfiguration {
		HUB("......\n......\n..0#..\n..#1..\n......\n......"),
		OPEN(".....\n.....\n..0..\n.....\n.....\n..1..\n.....\n....."),
		WALL(".......\n.......\n.......\n...#...\n..0#1..\n...#...\n.......\n.......\n......."),
		// TODO: HOLES *may* be a bust, need to play-test to determine whether it's worth keeping
		HOLES("...1...\n.#.#.#.\n.......\n.#.#.#.\n.......\n.#.#.#.\n.......\n.#.#.#.\n...0..."),
		MAIN_MENU_DEMO("..0.."),
		LOSE_DEMO(".01."),
		DRAW_DEMO(".01"),
		STALEMATE_DEMO("0.1");

		public final String boardSpec;

		BoardConfiguration(final String boardSpec) {
			this.boardSpec = boardSpec;
		}
	}

	/** Construct the Game instance. This game is designed to render at 720x1000 resolution. */
	public PartitionGame() {
		super(720, 1000);
	}

	@Override
	protected final Screen initialScreen() {
		// Start with the preloading screen
		return new PreloadingScreen(this);
	}

	/** Play the "error" sound effect, if sounds are enabled. */
	void playError() {
		if (sound) {
			manager.get("error.wav", Sound.class).play();
		}
	}

	/** Play the "explosion" sound effect, if sounds are enabled. */
	void playExplosion() {
		if (sound) {
			manager.get("explosion.wav", Sound.class).play();
		}
	}

	/** Play the "ping" sound effect, if sounds are enabled. */
	void playPing() {
		if (sound) {
			manager.get("ping.wav", Sound.class).play();
		}
	}

	/** Play the "tone" sound effect, if sounds are enabled. */
	void playTone() {
		if (sound) {
			manager.get("tone.wav", Sound.class).play();
		}
	}
}
