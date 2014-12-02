package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class InstructionsScreen extends BaseScreen {

	public InstructionsScreen(final PartitionGame game) {
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

		// Scrollable table to hold instructions
		final Table instructions = new Table();
		instructions.defaults().pad(10.0f);
		// instructions.debug();

		// Add table to stage, wrapped in a scrolling pane
		root.row();
		root.add(new ScrollPane(instructions)).expand().fill();

		// Fonts
		final BitmapFont bodyFont = new BitmapFont(Gdx.files.internal("segoeuiblack16.fnt"));
		final BitmapFont titleFont = new BitmapFont(Gdx.files.internal("segoeuiblack32.fnt"));
		// Styles
		final LabelStyle bodyStyle = new Label.LabelStyle(bodyFont, Color.WHITE);
		final LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.BLUE);

		// Instructions
		// TODO: Proper instructions!
		instructions.row();
		instructions.add(new Label("Title Text", titleStyle));

		final String[] lines = {
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
				"Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
				"Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				"Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." };
		for (String line : lines) {
			final Label label = new Label(line, bodyStyle);
			label.setWrap(true);
			instructions.row();
			instructions.add(label).fillX().expandX();
		}

		instructions.row();
		instructions.add(new Image(atlas.findRegion("Tile")));
		instructions.row();
		instructions.add(new Image(atlas.findRegion("RedPlayer")));
	}
}