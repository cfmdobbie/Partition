package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.partition.ScreenTransition.SolidColorFadeScreenTransition;

/**
 * Screen to select player configuration.
 * 
 * @author Charlie
 */
public class SelectPlayersScreen extends CScreen<PartitionGame> {

	/** The screen transition. */
	private final ScreenTransition screenTransition;

	/**
	 * Construct a new SelectPlayersScreen.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 */
	public SelectPlayersScreen(final PartitionGame game) {
		super(game);

		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		final TextureRegion twoPlayersUp = atlas.findRegion("2p_up");
		final TextureRegion twoPlayersDown = atlas.findRegion("2p_down");

		root.setBackground(new TiledDrawable(atlas.findRegion("background")));
		root.defaults().pad(5.0f);

		// Top menu bar
		root.row();
		final Table topBar = new Table();
		root.add(topBar).fill();

		// Back button
		final Button backButton = new Button(new TextureRegionDrawable(atlas.findRegion("back_up")),
				new TextureRegionDrawable(atlas.findRegion("back_down")));
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, SelectPlayersScreen.this, new MainMenuScreen(game));
			}
		});

		// Widgets in top menu bar
		topBar.row();
		topBar.add(backButton);
		topBar.add().expandX();
		topBar.add(new SoundToggleButton(game, atlas));

		// Divider
		root.row().height(2.0f);
		root.add(new Image(atlas.findRegion("white1x1"))).fill();

		// Spacer before heading
		root.row().expand();
		root.add();

		// Heading
		root.row().padBottom(30.0f);
		root.add(new Image(atlas.findRegion("select_players_heading")));

		// Two players
		root.row();
		Button twoPlayersButton = new Button(new TextureRegionDrawable(twoPlayersUp), new TextureRegionDrawable(
				twoPlayersDown));
		root.add(twoPlayersButton);
		twoPlayersButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, SelectPlayersScreen.this, new SelectBoardScreen(game,
						PartitionGame.PlayerConfiguration.TWO_PLAYER));
			}
		});

		// One player versus computer
		root.row();
		root.add(new Image(atlas.findRegion("1p_up")));

		// Spacer after
		root.row().expand();
		root.add();

		// Set up simple screen transition - fade in/out from/to black
		screenTransition = new SolidColorFadeScreenTransition(root, atlas.findRegion("black"));

		// And fade the screen in
		screenTransition.doTransitionIn();
	}

	@Override
	protected boolean handleBack() {
		return true;
	}

	@Override
	protected void doBack() {
		game.setScreen(new MainMenuScreen(game));
		SelectPlayersScreen.this.dispose();
	}
}
