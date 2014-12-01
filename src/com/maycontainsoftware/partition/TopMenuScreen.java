package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TopMenuScreen extends BaseScreen {

	public TopMenuScreen(final PartitionGame game) {
		super(game);

		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		final TextureRegion playUp = atlas.findRegion("play_up");
		final TextureRegion playDown = atlas.findRegion("play_down");
		final TextureRegion instructionsUp = atlas.findRegion("instructions_up");
		final TextureRegion instructionsDown = atlas.findRegion("instructions_down");

		root.defaults().pad(10.0f);

		root.row();
		Button playButton = new Button(new TextureRegionDrawable(playUp), new TextureRegionDrawable(playDown));
		root.add(playButton);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: Play button action
			}
		});

		root.row();
		Button instructionsButton = new Button(new TextureRegionDrawable(instructionsUp), new TextureRegionDrawable(
				instructionsDown));
		root.add(instructionsButton);
		instructionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: Instructions button action
			}
		});

	}
}
