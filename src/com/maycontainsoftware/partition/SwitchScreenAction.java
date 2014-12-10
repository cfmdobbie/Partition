package com.maycontainsoftware.partition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Action that switches from one screen to another. This exists to allow code to perform a screen switch at some point
 * in the future after other actions have completed. Note that the screen we are leaving will be disposed, so this class
 * is inappropriate in situations where the screen instances are reused.
 * 
 * @author Charlie
 */
class SwitchScreenAction extends PostRunnableAction {
	/**
	 * Construct a new SwitchScreenAction.
	 * 
	 * @param game
	 *            The Game instance.
	 * @param oldScreen
	 *            The Screen we are switching from.
	 * @param newScreen
	 *            The Screen we are switching to.
	 */
	public SwitchScreenAction(final Game game, final Screen oldScreen, final Screen newScreen) {
		super(new Runnable() {
			@Override
			public void run() {
				// Tell the game to switch to the new screen
				game.setScreen(newScreen);
				// setScreen will call hide() on the old screen, so want to dispose the old screen after switching
				// to the new screen
				oldScreen.dispose();
			}
		});
	}
}