package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Actor to represent a player component in the arbiter structure.
 * 
 * @author Charlie
 */
class PlayerActor extends Group implements IPlayer {

	/** Tag for logging purposes. */
	private static final String TAG = PlayerActor.class.getName();

	/** Names of the player token textures. */
	private static final String[] playerTextureNames = { "player_red", "player_blue" };

	/** Actor to represent the shadow. */
	private final Actor shadow;

	/** Actor to represent the player token. */
	private final Actor player;

	/** Actor to represent the target. */
	private final Actor target;

	/**
	 * Construct a new PlayerActor.
	 * 
	 * @param id
	 *            The player id.
	 * @param atlas
	 *            The main app TextureAtlas.
	 */
	public PlayerActor(final int id, final TextureAtlas atlas) {

		shadow = new Image(atlas.findRegion("shadow"));
		addActor(shadow);
		player = new Image(atlas.findRegion(playerTextureNames[id]));
		addActor(player);
		target = new Image(atlas.findRegion("target"));
		addActor(target);

		this.setTouchable(Touchable.disabled);
	}

	@Override
	public void doPendingMove() {

		Gdx.app.debug(TAG, "doPendingMove()");

		// Player token bounces up and down
		player.addAction(Actions.forever(Actions.sequence(
				Actions.moveBy(0.0f, getHeight() / 3, 0.5f, Interpolation.sine),
				Actions.moveBy(0.0f, -getHeight() / 3, 0.5f, Interpolation.sine))));
	}

	@Override
	public void doMove(final ITile targetTile, final Arbiter arbiter) {

		Gdx.app.debug(TAG, "doMove()");

		// Slide static player token to new location

		final TileActor tile = (TileActor) targetTile;
		player.clearActions();
		player.setPosition(0.0f, 0.0f);

		this.addAction(Actions.sequence(Actions.moveTo(tile.getX(), tile.getY(), 0.2f, Interpolation.sine),
				new Action() {
					@Override
					public boolean act(float delta) {
						arbiter.moveDone();
						return true;
					}
				}));
	}

	@Override
	public void doPendingShoot() {

		Gdx.app.debug(TAG, "doPendingShoot()");

		// Bouncing target graphic
		target.setColor(Color.WHITE);
		target.setPosition(getWidth() / 6, 0.0f);
		target.addAction(Actions.forever(Actions.sequence(
				Actions.moveBy(0.0f, getHeight() / 2, 0.5f, Interpolation.sine),
				Actions.moveBy(0.0f, -getHeight() / 2, 0.5f, Interpolation.sine))));
	}

	@Override
	public void doShoot(final ITile targetTile, final Arbiter arbiter) {

		Gdx.app.debug(TAG, "doShoot()");

		// Stop bouncing target
		target.clearActions();
		target.setPosition(0.0f, 0.0f);
		target.setColor(Color.CLEAR);
	}

	@Override
	public void doReset(final ITile startingTile) {

		Gdx.app.debug(TAG, "doReset()");
		Gdx.app.debug(TAG, "Moving to tile [" + startingTile.getCoords()[0] + "," + startingTile.getCoords()[1] + "]");

		final TileActor tile = (TileActor) startingTile;

		// Tile must be validly positioned and sized
		this.setPosition(tile.getX(), tile.getY());
		this.setSize(tile.getWidth(), tile.getHeight());

		// Reset shadow
		shadow.setSize(getWidth(), getHeight());

		// Reset player
		player.setSize(getWidth(), getHeight());
		player.clearActions();
		player.setPosition(0.0f, 0.0f);

		// Reset target
		target.setSize(getWidth(), getHeight());
		target.clearActions();
		target.setColor(Color.CLEAR);
		target.setPosition(0.0f, 0.0f);
	}
}