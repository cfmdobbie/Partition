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

public class SelectPlayersScreen extends BaseScreen {

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
		final Button backButton = new Button(new TextureRegionDrawable(atlas.findRegion("back_off")),
				new TextureRegionDrawable(atlas.findRegion("back_on")));
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new MainMenuScreen(game));
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
		root.add(new Image(atlas.findRegion("select_players_heading")));

		// Spacer between heading and buttons
		root.row().height(40.0f);
		root.add();

		// Two players
		root.row();
		Button twoPlayersButton = new Button(new TextureRegionDrawable(twoPlayersUp), new TextureRegionDrawable(
				twoPlayersDown));
		root.add(twoPlayersButton);
		twoPlayersButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new SelectBoardScreen(game, PartitionGame.PlayerConfiguration.TWO_PLAYER));
			}
		});

		// One player versus computer
		root.row();
		root.add(new Image(atlas.findRegion("coming_soon_120x60")));

		// Spacer after
		root.row().expand();
		root.add();
	}
}
