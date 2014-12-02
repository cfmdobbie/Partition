package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

/**
 * A Screen instance.
 * 
 * @author Charlie
 */
public class GameScreen extends BaseScreen {

	/** Tag for logging purposes. */
	public static final String TAG = GameScreen.class.getName();

	/** Reference to the current game state. */
	private GameState state;

	/** The chosen player configuration. */
	@SuppressWarnings("unused")
	private final PlayerConfiguration playerConfiguration;

	/** The chosen board configuration. */
	private final BoardConfiguration boardConfiguration;

	// Board dimensions
	private final int boardColumns;
	private final int boardRows;

	// Tile size
	private float tileWidth;
	private float tileHeight;

	// Temporary textures
	final TextureRegion tileTexture;
	final TextureRegion redPlayerTexture;
	final TextureRegion bluePlayerTexture;

	// Screen Layout
	//
	// Still largely to be decided. May contain elapsed time, AI thinking state, whose turn it is

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

		// Save player and board configuration
		this.playerConfiguration = playerConfiguration;
		this.boardConfiguration = boardConfiguration;

		// Get reference to main TextureAtlas
		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		// Store references to required Textures
		tileTexture = atlas.findRegion("Tile");
		redPlayerTexture = atlas.findRegion("RedPlayer");
		bluePlayerTexture = atlas.findRegion("BluePlayer");

		// Create new game state
		this.state = GameState.newGameState(boardConfiguration.boardSpec);

		// Determine board size
		boardColumns = this.state.tileEnabled.length;
		boardRows = this.state.tileEnabled[0].length;
		final float boardAspect = boardColumns / (float) boardRows;

		// Basic setup for root Table
		root.setBackground(new TiledDrawable(atlas.findRegion("background")));
		root.defaults().pad(5.0f);

		// Top menu bar
		root.row();
		final Table topBar = new Table();
		root.add(topBar).fill();

		// Back button
		final Button quitButton = new Button(new TextureRegionDrawable(atlas.findRegion("quit_off")),
				new TextureRegionDrawable(atlas.findRegion("quit_on")));
		quitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new MainMenuScreen(game));
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

		// Board container
		final Actor child = new Image(new Texture(Gdx.files.internal("yellow.png")));
		final FixedAspectContainer boardContainer = new FixedAspectContainer(child, boardAspect);
		root.row();
		root.add(boardContainer).expand().fill();

		// Divider
		root.row().height(2.0f);
		root.add(new Image(atlas.findRegion("white1x1"))).fill();

		// Status area
		final BitmapFont font = new BitmapFont(Gdx.files.internal("segoeuiblack16.fnt"));
		final LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
		root.row();
		root.add(new Label("<TODO: status area>", style));
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// Process any touch input
		if (Gdx.input.justTouched()) {
			// Get touch location
			final Vector3 pos = new Vector3();
			pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			game.camera.unproject(pos);
			// The Vector3 "pos" now contains the touch location

			// Convert to a coordinate in board space
			final byte r = (byte) ((-(pos.y - screenHeight)) / tileHeight);
			final byte c = (byte) (pos.x / tileWidth);

			if (PartitionGame.DEBUG) {
				Gdx.app.log(TAG, "Touch event on (" + c + "," + r + ")");
			}
			// state.tileEnabled[c][r] = !state.tileEnabled[c][r];

			try {
				state = GameState.apply(state, new byte[] { c, r });
			} catch (Error e) {
				if (PartitionGame.DEBUG) {
					Gdx.app.log(TAG, "Invalid move!");
				}
			}

			// TEMP: Reset board if game cannot proceed
			if (GameState.isGameOver(state)) {
				state = GameState.newGameState(boardConfiguration.boardSpec);
			} else if (GameState.isStalemate(state)) {
				state = GameState.newGameState(boardConfiguration.boardSpec);
			}
		}

		// Start drawing
		game.batch.begin();

		// Draw tiles
		for (int c = 0; c < boardColumns; c++) {
			for (int r = 0; r < boardRows; r++) {
				if (this.state.tileEnabled[c][r]) {
					final float x = c * tileWidth;
					final float y = screenHeight - (r + 1) * tileHeight;
					game.batch.draw(tileTexture, x + tileWidth * 0.1f, y + tileHeight * 0.1f, tileWidth * 0.8f,
							tileHeight * 0.8f);
				}
			}
		}

		// Draw players
		// FUTURE: This is hard-coded for two players at this time. Should improve this.
		for (int p = 0; p < GameState.getNumberOfPlayers(state); p++) {
			final byte[] coords = GameState.getPlayerCoords(state, p);
			final float x = coords[0] * tileWidth;
			final float y = screenHeight - (coords[1] + 1) * tileHeight;
			game.batch.draw(p == 0 ? redPlayerTexture : bluePlayerTexture, x + tileWidth * 0.1f, y + tileHeight * 0.1f,
					tileWidth * 0.8f, tileHeight * 0.8f);
		}

		// Stop drawing
		game.batch.end();
	}

	@Override
	public void resize(final int width, final int height) {
		if (PartitionGame.DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		super.resize(width, height);

		// Determine tile size
		tileWidth = screenWidth / boardColumns;
		tileHeight = screenHeight / boardRows;
	}
}
