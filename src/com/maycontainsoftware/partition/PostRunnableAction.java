package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;

/** Simple Action that posts a Runnable to the main thread outside of rendering loop.
 * 
 * @author Charlie
 */
class PostRunnableAction extends Action {
	/** The runnable to post. */
	final Runnable runnable;

	/** Construct a new PostRunnableAction
	 * 
	 * @param runnable The Runnable to post.
	 */
	public PostRunnableAction(final Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public boolean act(float delta) {
		// Post the runnable to the main thread
		Gdx.app.postRunnable(runnable);
		// Finish the Action
		return true;
	}
}