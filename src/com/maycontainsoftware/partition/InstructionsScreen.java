package com.maycontainsoftware.partition;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;
import com.maycontainsoftware.partition.ScreenTransition.SolidColorFadeScreenTransition;

/**
 * A screen full of useful information! This screen contains both details on how to play the game, plus any credits that
 * are required for the assets the game uses.
 * 
 * @author Charlie
 */
public class InstructionsScreen extends CScreen<PartitionGame> {

	/** The screen transition. */
	private final ScreenTransition screenTransition;

	/**
	 * Construct a new InstructionsScreen object.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 */
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
		final Button backButton = new Button(new TextureRegionDrawable(atlas.findRegion("back_up")),
				new TextureRegionDrawable(atlas.findRegion("back_down")));
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, InstructionsScreen.this, new MainMenuScreen(game));
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

		// All text styles used on this screen

		final LabelStyle bodyStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("segoeuiblack24.fnt")),
				Color.WHITE);
		final LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("segoeuiblack32.fnt")),
				Color.BLUE);

		// Scrollable table to hold instructions
		final Table instructions = new Table();
		instructions.defaults().pad(10.0f).expandX().fillX();
		// instructions.debug();
		// Add table to stage, wrapped in a scrolling pane
		root.row();
		root.add(new ScrollPane(instructions)).expand().fill();

		// Instructions

		instructions.row();
		instructions.add(makeLabel("How To Play", titleStyle));

		instructions.row();
		instructions
				.add(makeLabel(
						"Partition is a game with very simple rules but one that requires careful thought to win.  The aim of the game is to acquire more area than your opponent.",
						bodyStyle));

		instructions.row();
		instructions.add(makeLabel(
				"Players take it in turns first to move to a new square, then to remove a square from the board.",
				bodyStyle));

		instructions.row();
		instructions
				.add(makeLabel(
						"Movements can be made any number of squares in a straight line (including diagonally).  The square that is removed must similarly be in a straight line from the player.",
						bodyStyle));

		instructions.row();
		instructions
				.add(makeLabel(
						"The game ends either when the players are separated, in which case the winner is the player on the largest patch of squares, or in a stalemate when both players are on the same patch of squares but a player cannot move.",
						bodyStyle));

		instructions.row();
		instructions.add(makeLabel("Examples", titleStyle));

		instructions.row();
		instructions.add(makeLabel("The player left in the largest patch of squares wins.  Try it out!", bodyStyle));

		instructions.row();
		instructions.add(new GameBoard(game, atlas, 125.0f * 4, 125.0f, PlayerConfiguration.TWO_PLAYER,
				BoardConfiguration.LOSE_DEMO, true));

		instructions.row();
		instructions.add(makeLabel("If the players end up in areas of equal size, the game is a draw:", bodyStyle));

		instructions.row();
		instructions.add(new GameBoard(game, atlas, 125.0f * 3, 125.0f, PlayerConfiguration.TWO_PLAYER,
				BoardConfiguration.DRAW_DEMO, true));

		instructions.row();
		instructions.add(makeLabel(
				"If the players are not isolated but a player cannot move, the game ends in a stalemate:", bodyStyle));

		instructions.row();
		instructions.add(new GameBoard(game, atlas, 125.0f * 3, 125.0f, PlayerConfiguration.TWO_PLAYER,
				BoardConfiguration.STALEMATE_DEMO, true));

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
		InstructionsScreen.this.dispose();
	}

	/** Utility method to make a new text label. */
	private Label makeLabel(final String text, final LabelStyle style) {
		final Label label = new Label(text, style);
		label.setWrap(true);
		return label;
	}

	/**
	 * Simple animated image class.
	 * 
	 * @author Charlie
	 */
	static class AnimatedImage extends Image {

		private final Animation animation;
		private float time = 0.0f;
		private Drawable currentDrawable;
		private Map<TextureRegion, Drawable> drawables = new HashMap<TextureRegion, Drawable>();

		public AnimatedImage(final Animation animation) {
			this.animation = animation;
		}

		@Override
		public void act(float delta) {
			// Update any actions
			super.act(delta);

			// Update animation time
			time += delta;

			// Determine texture region to display
			final TextureRegion region = animation.getKeyFrame(time, true);

			// Add it to the map if it doesn't already exist
			if (!drawables.containsKey(region)) {
				drawables.put(region, new TextureRegionDrawable(region));
			}

			// Get the drawable for the texture region from the map
			final Drawable nextDrawable = drawables.get(region);

			// If the drawable has just changed, update the image
			if (nextDrawable != currentDrawable) {
				currentDrawable = nextDrawable;
				setDrawable(currentDrawable);
			}
		}
	}
}
