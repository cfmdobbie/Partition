package com.maycontainsoftware.general;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

/**
 * A sound engine that plays Sound objects that have been loaded in an AssetManager.
 * 
 * @author Charlie
 */
public class CSoundEngine {

	/** Reference to the AssetManager that has loaded the sound files. */
	private final AssetManager manager;

	/** Whether sounds are enabled. For now, defaults to true and changes are not persisted across restarts. */
	private boolean enabled = true;

	/**
	 * Construct a new sound engine
	 * 
	 * @param manager
	 *            The AssetManager that has loaded the sound files.
	 */
	public CSoundEngine(final AssetManager manager) {
		this.manager = manager;
	}

	/**
	 * Play the specified sound
	 * 
	 * @param soundId
	 *            The sound to play.
	 */
	public void play(SoundId soundId) {
		if (enabled && manager != null) {
			final Sound s = manager.get(soundId.getAssetName(), Sound.class);
			s.play();
		}
	}

	/**
	 * The marker interface for all sounds. Sounds are assumed to be loaded in the asset manager and are referenced by
	 * the asset name.
	 * 
	 * @author Charlie
	 * 
	 */
	public static interface SoundId {
		/** Return the asset name for this sound. */
		public String getAssetName();
	}

	/**
	 * Set whether or not sounds are enabled.
	 * 
	 * @param enabled
	 *            True to enable sounds, false to disable sounds.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns whether or not sounds are enabled.
	 * 
	 * @return True if sounds are enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}
}
