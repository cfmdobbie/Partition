package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * A button for toggling sound on and off.
 * 
 * @author Charlie
 */
class SoundToggleButton extends Button {

	/** Tag for logging purposes. */
	private static final String TAG = SoundToggleButton.class.getName();

	/**
	 * Construct a new SoundToggleButton.
	 * 
	 * @param game
	 * @param atlas
	 */
	public SoundToggleButton(final PartitionGame game, final TextureAtlas atlas) {
		super(getDrawableForRegion(atlas, "sound_off"), null, getDrawableForRegion(atlas, "sound_on"));

		// Change listener to handle touches and toggle sound setting on Game object
		addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.setEnabled(isChecked());
				Gdx.app.debug(TAG, "Play sound? " + game.soundEngine.isEnabled());
			}
		});

		// Set button state to match current sound setting
		setChecked(game.soundEngine.isEnabled());
	}

	/**
	 * Utility method to convert a region name into a Drawable instance.
	 * 
	 * @param atlas
	 * @param regionName
	 * @return
	 */
	private static Drawable getDrawableForRegion(final TextureAtlas atlas, final String regionName) {
		return new TextureRegionDrawable(atlas.findRegion(regionName));
	}
}