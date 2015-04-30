package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.maycontainsoftware.partition.CardStack.IStackChangeListener;

/**
 * A button for navigating backwards in the card stack. This button listens to stackChanged events from the CardStack
 * and automatically hides itself if there's only one card in the stack.
 * 
 * @author Charlie
 */
class BackButton extends Button implements IStackChangeListener {

	/** Tag for logging purposes. */
	private static final String TAG = BackButton.class.getName();

	/**
	 * Construct a new BackButton.
	 * 
	 * @param game
	 * @param atlas
	 */
	public BackButton(final PartitionGame game, final TextureAtlas atlas) {
		super(new TextureRegionDrawable(atlas.findRegion("back")));
	}

	@Override
	public void stackChanged(int newSize) {

		Gdx.app.debug(TAG, "stackChanged::newSize=" + newSize);

		// If there's only one card in the stack, want to hide the back button.
		// If there's more than one - want to show it.
		if (newSize <= 1) {
			this.setVisible(false);
		} else {
			this.setVisible(true);
		}
	}
}
