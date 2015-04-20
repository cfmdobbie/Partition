package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;
import com.maycontainsoftware.partition.ScreenTransition.SolidColorFadeScreenTransition;

/**
 * Screen to select a board.
 * 
 * @author Charlie
 */
public class SelectBoardScreen extends CScreen<PartitionGame> {

	/** The screen transition. */
	private final ScreenTransition screenTransition;

	/**
	 * Construct a new SelectBoardScreen object.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 * @param playerConfiguration
	 *            The PlayerConfiguration that has already been selected.
	 */
	public SelectBoardScreen(final PartitionGame game, final PlayerConfiguration playerConfiguration) {
		super(game);

		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

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
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				screenTransition.doTransitionOut(game, SelectBoardScreen.this, new MainMenuScreen(game));
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
		root.add(new Image(atlas.findRegion("select_board_heading")));

		// Table to lay out board selection buttons
		final Table boardSelections = new Table();
		boardSelections.defaults().pad(5.0f);
		root.row();
		root.add(boardSelections);

		boardSelections.row();
		// Board 1: HUB
		final Button boardHubButton = new Button(new TextureRegionDrawable(atlas.findRegion("board_hub_up")),
				new TextureRegionDrawable(atlas.findRegion("board_hub_down")));
		boardSelections.add(boardHubButton);
		boardHubButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				screenTransition.doTransitionOut(game, SelectBoardScreen.this, new GameScreen(game,
						playerConfiguration, BoardConfiguration.HUB));
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
				screenTransition.doTransitionOut(game, SelectBoardScreen.this, new GameScreen(game,
						playerConfiguration, BoardConfiguration.OPEN));
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
				screenTransition.doTransitionOut(game, SelectBoardScreen.this, new GameScreen(game,
						playerConfiguration, BoardConfiguration.WALL));
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
				screenTransition.doTransitionOut(game, SelectBoardScreen.this, new GameScreen(game,
						playerConfiguration, BoardConfiguration.HOLES));
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

	@Override
	protected boolean handleBack() {
		return true;
	}

	@Override
	protected void doBack() {
		game.setScreen(new MainMenuScreen(game));
		SelectBoardScreen.this.dispose();
	}
}
