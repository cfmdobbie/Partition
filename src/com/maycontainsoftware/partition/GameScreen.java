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
 * The Screen instance on which the game is actually played.
 * 
 * @author Charlie
 */
public class GameScreen extends BaseScreen {

	/** Tag for logging purposes. */
	public static final String TAG = GameScreen.class.getName();

	/** The screen transition. */
	private final ScreenTransition screenTransition;

	/**
	 * Constructor.
	 * 
	 * @param game
	 *            The Game instance.
	 * @param playerConfiguration
	 *            The chosen player configuration.
	 * @param boardConfiguration
	 *            The chosen board configuration.
	 */
	public GameScreen(final PartitionGame game, final PlayerConfiguration playerConfiguration,
			final BoardConfiguration boardConfiguration) {
		super(game);

		// Get reference to main TextureAtlas
		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		// Basic setup for root Table
		root.setBackground(new TiledDrawable(atlas.findRegion("background")));
		root.defaults().pad(5.0f);

		// Top menu bar
		root.row();
		final Table topBar = new Table();
		root.add(topBar).fill();
		topBar.setBackground(new TiledDrawable(atlas.findRegion("black")));

		// Back button
		final Button quitButton = new Button(new TextureRegionDrawable(atlas.findRegion("quit_up")),
				new TextureRegionDrawable(atlas.findRegion("quit_down")));
		quitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.playTone();
				screenTransition.doTransitionOut(game, GameScreen.this, new MainMenuScreen(game));
			}
		});

		// Widgets in top menu bar
		topBar.row();
		topBar.add(quitButton);
		topBar.add().expandX();
		topBar.add(new SoundToggleButton(game, atlas));

		// Divider
		root.row().height(2.0f);
		root.add(new Image(atlas.findRegion("white1x1"))).fill();

		// FixedAspectContainer is a container which forces a specific aspect ratio on its sole child. GameBoard draws
		// the board at unit scale, but applies transformations to scale drawing to the correct size.
		final GameBoard gameBoard = new GameBoard(game, atlas, playerConfiguration, boardConfiguration);
		float boardAspect = gameBoard.getDesiredAspect();
		final FixedAspectContainer boardContainer = new FixedAspectContainer(gameBoard, boardAspect);
		root.row();
		root.add(boardContainer).expand().fill();

		// Set up simple screen transition - fade in/out from/to black
		screenTransition = new SolidColorFadeScreenTransition(root, atlas.findRegion("black"));

		// And fade the screen in
		screenTransition.doTransitionIn();
	}
}
