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

/**
 * The main menu screen. This is the first interactive screen that the player encounters; it appears directly after the
 * loading screens.
 * 
 * @author Charlie
 */
public class MainMenuScreen extends BaseScreen {

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
		root.row().height(40.0f);
		root.add();

		// Play button
		root.row();
		Button playButton = new Button(new TextureRegionDrawable(playUp), new TextureRegionDrawable(playDown));
		root.add(playButton);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				// TODO: Need fade out, disposal of current screen
				game.setScreen(new SelectPlayersScreen(game));
			}
		});

		// Instructions button
		root.row();
		Button instructionsButton = new Button(new TextureRegionDrawable(instructionsUp), new TextureRegionDrawable(
				instructionsDown));
		root.add(instructionsButton);
		instructionsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				// TODO: Need fade out, disposal of current screen
				game.setScreen(new InstructionsScreen(game));
			}
		});

		// Spacer after
		root.row().expand();
		root.add();

		// TODO: Screen transition

		// Can add an Actor to the root table not via add() but via addActor(), so it does not participate in layout.
		// Added actor could be setFillParent() to automatically fill the Screen over resizes. If added last, the actor
		// will overlay all other widgets. Can use setTouchable(disabled) to make it transparent to touches, or leave it
		// opaque to touches during transition and use setVisible(false) once transition complete to allow touches
		// through to other screen widgets. 300ms is probably a good speed for the transition.

		// As for what the transition is, a simple black screen fade to alpha=0 would suffice, but a more interesting
		// effect based in some way upon game mechanics would be better. Maybe this depends on the final decision on how
		// the tiles animate as they are destroyed? So maybe some kind of generic object is required, whose exact render
		// can be decided later.

		// Image i = new Image(atlas.findRegion("BlueTile"));
		// i.setTouchable(Touchable.disabled);
		// i.setVisible(false);
		// i.setFillParent(true);
		// root.addActor(i);
	}
}
