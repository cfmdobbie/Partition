package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.partition.ScreenTransition.SolidColorFadeScreenTransition;

/**
 * The main menu screen. This is the first interactive screen that the player encounters; it appears directly after the
 * loading screens.
 * 
 * @author Charlie
 */
public class MainMenuScreen extends CScreen<PartitionGame> {

	/** The screen transition. */
	private final ScreenTransition screenTransition;

	/**
	 * Construct a new MainMenuScreen.
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

		// Sound toggle button
		root.row();
		root.add(new SoundToggleButton(game, atlas)).expand().right().top();

		// Main screen content

		// Midpoint of screen, minus top row
		final float midX = game.virtualWidth / 2;
		final float midY = (game.virtualHeight - 50) / 2;

		// Logo
		final Actor logo = new Image(atlas.findRegion("partition_logo"));
		logo.setPosition(midX - logo.getWidth() / 2, midY + 200);
		root.addActor(logo);

		// Play button
		final Button playButton = new Button(new TextureRegionDrawable(playUp), new TextureRegionDrawable(playDown));
		playButton.setPosition(midX - playButton.getWidth() / 2, midY + 30);
		root.addActor(playButton);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, MainMenuScreen.this, new SelectPlayersScreen(game));
			}
		});

		// Example game board
		// final GameBoard gameBoard = new GameBoard(game, atlas, null, BoardConfiguration.MAIN_MENU_DEMO, true);
		final Actor gameBoard = new Image(atlas.findRegion("black"));
		// Know board is 5x1 in size, and know that main menu buttons are 180 width
		final float boardWidth = 180.0f;
		final float boardHeight = boardWidth / 5;
		gameBoard.setSize(boardWidth, boardHeight);
		final float boardX = midX - boardWidth / 2;
		final float boardY = midY - boardHeight / 2;
		gameBoard.setPosition(boardX, boardY);
		root.addActor(gameBoard);

		// Instructions button
		final Button instructionsButton = new Button(new TextureRegionDrawable(instructionsUp),
				new TextureRegionDrawable(instructionsDown));
		instructionsButton.setPosition(midX - instructionsButton.getWidth() / 2,
				midY - 30 - instructionsButton.getHeight());
		root.addActor(instructionsButton);
		instructionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, MainMenuScreen.this, new InstructionsScreen(game));
			}
		});

		// Set up simple screen transition - fade in/out from/to black
		screenTransition = new SolidColorFadeScreenTransition(root, atlas.findRegion("black"));

		// And fade the screen in
		screenTransition.doTransitionIn();
	}
}
