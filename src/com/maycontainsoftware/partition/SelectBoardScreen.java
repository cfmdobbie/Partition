package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class SelectBoardScreen extends BaseScreen {

	public SelectBoardScreen(final PartitionGame game) {
		super(game);

		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		root.setBackground(new TiledDrawable(atlas.findRegion("background")));
		root.defaults().pad(5.0f);

		// Top menu bar
		root.row();
		final Table topBar = new Table();
		root.add(topBar).fill();

		// Back button
		final Button backButton = new Button(new TextureRegionDrawable(atlas.findRegion("back_off")),
				new TextureRegionDrawable(atlas.findRegion("back_on")));
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new SelectPlayersScreen(game));
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
		root.row();
		root.add(new Image(atlas.findRegion("select_board_heading")));

		// Spacer between heading and buttons
		root.row().height(40.0f);
		root.add();

		// Table to lay out board selection buttons
		final Table boardSelections = new Table();
		boardSelections.defaults().pad(5.0f);
		root.row();
		root.add(boardSelections);

		// Board 1
		final Button board1Button = new Button(new TextureRegionDrawable(atlas.findRegion("board1_up")),
				new TextureRegionDrawable(atlas.findRegion("board1_down")));
		boardSelections.row();
		boardSelections.add(board1Button);
		board1Button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new MyScreen(game));
			}
		});
		// Coming soon
		boardSelections.add(new Image(atlas.findRegion("board_coming_soon")));

		boardSelections.row();
		// Coming soon
		boardSelections.add(new Image(atlas.findRegion("board_coming_soon")));
		// Coming soon
		boardSelections.add(new Image(atlas.findRegion("board_coming_soon")));

		// Spacer after
		root.row().expand();
		root.add();
	}
}
