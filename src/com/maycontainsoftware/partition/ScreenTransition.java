package com.maycontainsoftware.partition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Class that represents a screen transition.
 * 
 * @author Charlie
 */
abstract class ScreenTransition {

	/** Perform the in-transition animation. */
	abstract void doTransitionIn();

	/**
	 * Perform the out-transition animation.
	 * 
	 * @param game
	 *            The Game instance.
	 * @param oldScreen
	 *            The Screen instance we are transitioning from.
	 * @param newScreen
	 *            The Screen instance we are transitioning to.
	 */
	abstract void doTransitionOut(final Game game, final Screen oldScreen, final Screen newScreen);

	/**
	 * A screen transition in the form of a fade in from, and a fade out to, a solid color.
	 * 
	 * @author Charlie
	 */
	static class SolidColorFadeScreenTransition extends ScreenTransition {

		/** The solid-color panel that covers the screen. */
		final Widget solid;

		/**
		 * Construct a new SolidColorFadeScreenTransition. This will add the required UI widgets to the root Table
		 * immediately, so this transition should be constructed after all other UI elements.
		 * 
		 * @param root
		 *            The root Table.
		 * @param textureRegion
		 *            The solid-color texture region
		 */
		public SolidColorFadeScreenTransition(final Table root, final TextureRegion textureRegion) {

			// Need a single widget that is a full-screen solid-color panel
			solid = new Image(textureRegion);
			solid.setFillParent(true);

			// Add the widget to the root table
			root.addActor(solid);

			// We assume that we set ourselves up ready to transition in, so visible and blocking all input
			solid.setVisible(true);
		}

		@Override
		void doTransitionIn() {

			// Fade out then stop blocking input
			solid.addAction(Actions.sequence(Actions.fadeOut(0.25f), new Action() {
				@Override
				public boolean act(float delta) {
					// Set invisible so input is no longer intercepted
					solid.setVisible(false);
					return true;
				}
			}));
		}

		@Override
		void doTransitionOut(final Game game, final Screen oldScreen, final Screen newScreen) {

			// Start to block all input
			solid.setVisible(true);

			// Fade in then trigger switch to new screen
			solid.addAction(Actions.sequence(Actions.fadeIn(0.25f), new SwitchScreenAction(game, oldScreen, newScreen)));
		}
	}
}