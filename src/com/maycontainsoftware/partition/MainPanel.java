package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The main menu panel. This is the first panel that is seen.
 * 
 * @author Charlie
 */
public class MainPanel extends Table {

	/**
	 * Construct a new MainPanel.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 */
	public MainPanel(final PartitionGame game, final CardStack cardStack) {

		final TextureAtlas atlas = game.textureAtlas;

		final TextureRegion playUp = atlas.findRegion("play_up");
		final TextureRegion playDown = atlas.findRegion("play_down");
		final TextureRegion instructionsUp = atlas.findRegion("instructions_up");
		final TextureRegion instructionsDown = atlas.findRegion("instructions_down");

		defaults().pad(5.0f);

		// Main screen content

		// Spacer before logo
		row().expand();
		add();

		// Logo
		row().padBottom(30.0f);
		add(new Image(atlas.findRegion("partition_logo")));

		// Play button
		row();
		final Button playButton = new Button(new TextureRegionDrawable(playUp), new TextureRegionDrawable(playDown));
		add(playButton);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new SelectPlayersPanel(game, cardStack));
			}
		});

		// Horizontal row of tiles
		// Know board is 5x1 in size, and know that main menu buttons are 180 width
		final float boardWidth = 180.0f;
		final float boardHeight = boardWidth / 5;
		final BaseGameBoard gameBoard = new DemoGameBoard(game, atlas, boardWidth, boardHeight,
				PlayerConfiguration.ONE_PLAYER, BoardConfiguration.MAIN_MENU_DEMO);
		row();
		add(gameBoard);

		// Instructions button
		row();
		final Button instructionsButton = new Button(new TextureRegionDrawable(instructionsUp),
				new TextureRegionDrawable(instructionsDown));
		add(instructionsButton);
		instructionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new InstructionsPanel(game));
			}
		});

		// Spacer after
		row().expand();
		add();
	}
}
