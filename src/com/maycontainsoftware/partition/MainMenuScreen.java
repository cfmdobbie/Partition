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
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.ScreenTransition.SolidColorFadeScreenTransition;

/**
 * The main menu screen. This is the first interactive screen that the player encounters; it appears directly after the
 * loading screens.
 * 
 * @author Charlie
 */
public class MainMenuScreen extends BaseScreen {

	/** The screen transition. */
	private final ScreenTransition screenTransition;

	/**
	 * Construct a new MainMenuScree.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 */
	public MainMenuScreen(final PartitionGame game) {
		super(game);

		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		final TextureRegion playUp = atlas.findRegion("play_up");
		final TextureRegion playDown = atlas.findRegion("play_down");
		final TextureRegion instructionsUp = atlas.findRegion("instructions_up");
		final TextureRegion instructionsDown = atlas.findRegion("instructions_down");

		root.setBackground(new TiledDrawable(atlas.findRegion("background")));
		root.defaults().pad(5.0f);

		// Top menu bar
		root.row();
		final Table topBar = new Table();
		root.add(topBar).fill();

		// Widgets in top menu bar
		topBar.row();
		topBar.add().expandX();
		topBar.add(new SoundToggleButton(game, atlas));

		// Divider
		root.row().height(2.0f);
		root.add(new Image(atlas.findRegion("white1x1"))).fill();

		// Main screen content

		// Spacer before logo
		root.row().expand();
		root.add();

		// Logo
		root.row();
		root.add(new Image(atlas.findRegion("partition_logo")));

		// Spacer between logo and buttons
		root.row().height(10.0f);
		root.add();

		// Play button
		root.row();
		Button playButton = new Button(new TextureRegionDrawable(playUp), new TextureRegionDrawable(playDown));
		root.add(playButton);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, MainMenuScreen.this, new SelectPlayersScreen(game));
			}
		});

		// Horizontal row of tiles
		final GameBoard gameBoard = new GameBoard(game, atlas, null, BoardConfiguration.MAIN_MENU_DEMO);
		float boardAspect = gameBoard.getDesiredAspect();
		final FixedAspectContainer boardContainer = new FixedAspectContainer(gameBoard, boardAspect);
		// Know board is 5x1 in size, and know that main menu buttons are 180 width
		final float boardWidth = 180.0f;
		final float boardHeight = boardWidth / 5;
		root.row().height(boardHeight);
		root.add(boardContainer).width(boardWidth);

		// Instructions button
		root.row();
		Button instructionsButton = new Button(new TextureRegionDrawable(instructionsUp), new TextureRegionDrawable(
				instructionsDown));
		root.add(instructionsButton);
		instructionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, MainMenuScreen.this, new InstructionsScreen(game));
			}
		});

		// Spacer after
		root.row().expand();
		root.add();

		// Set up simple screen transition - fade in/out from/to black
		screenTransition = new SolidColorFadeScreenTransition(root, atlas.findRegion("black"));

		// And fade the screen in
		screenTransition.doTransitionIn();
	}
}
