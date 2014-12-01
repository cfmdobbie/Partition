package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TopMenuScreen extends BaseScreen {

	public TopMenuScreen(final PartitionGame game) {
		super(game);

		final Texture playUp = game.manager.get("play_up.png", Texture.class);
		final Texture playDown = game.manager.get("play_down.png", Texture.class);
		final Texture instructionsUp = game.manager.get("instructions_up.png", Texture.class);
		final Texture instructionsDown = game.manager.get("instructions_down.png", Texture.class);

		root.defaults().pad(10.0f);

		root.row();
		Button playButton = new Button(new TextureRegionDrawable(new TextureRegion(playUp)), new TextureRegionDrawable(
				new TextureRegion(playDown)));
		root.add(playButton);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: Play button action
			}
		});

		root.row();
		Button instructionsButton = new Button(new TextureRegionDrawable(new TextureRegion(instructionsUp)),
				new TextureRegionDrawable(new TextureRegion(instructionsDown)));
		root.add(instructionsButton);
		instructionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: Instructions button action
			}
		});

	}
}
