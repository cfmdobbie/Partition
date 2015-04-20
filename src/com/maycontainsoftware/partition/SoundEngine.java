package com.maycontainsoftware.partition;

import com.badlogic.gdx.assets.AssetManager;

/**
 * The sound engine used in Partition.
 * 
 * @author Charlie
 */
public class SoundEngine extends CSoundEngine {

	/**
	 * Construct a new sound engine.
	 * 
	 * @param manager
	 *            The AssetManager created by PartitionGame.
	 */
	public SoundEngine(final AssetManager manager) {
		super(manager);
	}

	/**
	 * The enumeration that describes all Sound objects playable in Partition.
	 * 
	 * @author Charlie
	 */
	public static enum SoundId implements CSoundEngine.SoundId {
		ERROR("error.wav"),
		EXPLOSION("explosion.wav"),
		PING("ping.wav"),
		TONE("tone.wav");

		/** The name of the asset. */
		private final String assetName;

		/**
		 * Construct a new sound identifier.
		 * 
		 * @param assetName
		 *            The name of the asset.
		 */
		private SoundId(final String assetName) {
			this.assetName = assetName;
		}

		@Override
		public String getAssetName() {
			return assetName;
		}
	}
}