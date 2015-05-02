package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

/**
 * Board selection card.
 * 
 * @author Charlie
 */
public class SelectBoardPanel extends Table {

	/**
	 * Construct a new SelectBoardScreen object.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 * @param playerConfiguration
	 *            The PlayerConfiguration that has already been selected.
	 */
	public SelectBoardPanel(final PartitionGame game, final CardStack cardStack,
			final PlayerConfiguration playerConfiguration) {

		final TextureAtlas atlas = game.textureAtlas;

		// Spacer before heading
		row().expand();
		add();

		// Heading
		row().padBottom(30.0f);
		add(new Image(atlas.findRegion("select_board_heading")));

		// Table to lay out board selection buttons
		final Table boardSelections = new Table();
		boardSelections.defaults().pad(5.0f);
		row();
		add(boardSelections);

		boardSelections.row();
		// Board 1: HUB
		final Button boardHubButton = new Button(new TextureRegionDrawable(atlas.findRegion("board_hub_up")),
				new TextureRegionDrawable(atlas.findRegion("board_hub_down")));
		boardSelections.add(boardHubButton);
		boardHubButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new GamePanel(game, cardStack, PlayerConfiguration.TWO_PLAYER, BoardConfiguration.HUB));
			}
		});
		// Board 2: OPEN
		final Button boardOpenButton = new Button(new TextureRegionDrawable(atlas.findRegion("board_open_up")),
				new TextureRegionDrawable(atlas.findRegion("board_open_down")));
		boardSelections.add(boardOpenButton);
		boardOpenButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new GamePanel(game, cardStack, PlayerConfiguration.TWO_PLAYER, BoardConfiguration.OPEN));
			}
		});

		boardSelections.row();
		// Board 3: WALL
		final Button boardWallButton = new Button(new TextureRegionDrawable(atlas.findRegion("board_wall_up")),
				new TextureRegionDrawable(atlas.findRegion("board_wall_down")));
		boardSelections.add(boardWallButton);
		boardWallButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new GamePanel(game, cardStack, PlayerConfiguration.TWO_PLAYER, BoardConfiguration.WALL));
			}
		});
		// Board 4: HOLES
		final Button boardHolesButton = new Button(new TextureRegionDrawable(atlas.findRegion("board_holes_up")),
				new TextureRegionDrawable(atlas.findRegion("board_holes_down")));
		boardSelections.add(boardHolesButton);
		boardHolesButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack
						.push(new GamePanel(game, cardStack, PlayerConfiguration.TWO_PLAYER, BoardConfiguration.HOLES));
			}
		});

		// Spacer after
		row().expand();
		add();
	}
}
