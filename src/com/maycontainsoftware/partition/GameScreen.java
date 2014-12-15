package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
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

	/** Reference to the current game state. */
	private GameState state;

	/** The chosen player configuration. */
	@SuppressWarnings("unused")
	private final PlayerConfiguration playerConfiguration;

	/** The chosen board configuration. */
	private final BoardConfiguration boardConfiguration;

	// Board dimensions
	/** Number of columns on the current board. */
	private final int boardColumns;

	/** Number of rows on the board. */
	private final int boardRows;

	// Temporary textures
	final TextureRegion tileTexture;
	final TextureRegion highlightTexture;
	final TextureRegion redPlayerTexture;
	final TextureRegion bluePlayerTexture;

	/** The screen transition. */
	private final ScreenTransition screenTransition;

	/** The status message at the bottom of the screen. */
	final Label statusMessage;

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
		tileTexture = atlas.findRegion("tile");
		highlightTexture = atlas.findRegion("tile_highlight");
		redPlayerTexture = atlas.findRegion("player_red0");
		bluePlayerTexture = atlas.findRegion("player_blue0");

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
		final FixedAspectContainer boardContainer = new FixedAspectContainer(new GameBoard(atlas), boardAspect);
		root.row();
		root.add(boardContainer).expand().fill();

		// Divider
		root.row().height(2.0f);
		root.add(new Image(atlas.findRegion("white1x1"))).fill();

		// Status area
		final BitmapFont font = new BitmapFont(Gdx.files.internal("segoeuiblack24.fnt"));
		final LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
		root.row();
		statusMessage = new Label("", style);
		root.add(statusMessage);

		updateStatusMessage();

		// Set up simple screen transition - fade in/out from/to black
		screenTransition = new SolidColorFadeScreenTransition(root, atlas.findRegion("black"));

		// And fade the screen in
		screenTransition.doTransitionIn();
	}

	/** Update the message displayed at the bottom of the screen with respect to the current game state. */
	private void updateStatusMessage() {
		final int playerNumber = state.currentPlayerIndex + 1;
		statusMessage.setText("Player " + playerNumber + ": "
				+ (state.turnPhase == GameState.PHASE_MOVE ? "Move" : "Shoot"));
	}

	/**
	 * GameBoard draws the Partition board at unit scale, but uses OpenGL modelview matrix transformations to transform
	 * this unit-scale render into a render at the correct size and location. This avoids issues with resizing - the
	 * board is always drawn at unit scale, so no board elements need to be updated upon resize.
	 * 
	 * Note that this class is not static at this time!
	 * 
	 * @author Charlie
	 */
	private class GameBoard extends Widget {

		/**
		 * Construct a new GameBoard.
		 * 
		 * @param atlas
		 */
		public GameBoard(final TextureAtlas atlas) {
			addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}

				@Override
				public void touchUp(InputEvent event, float px, float py, int pointer, int button) {

					final byte[] tileCoord = actorCoordToTileCoord(px, py);

					try {
						final byte phase = state.turnPhase;
						state = GameState.apply(state, tileCoord);
						switch (phase) {
						case GameState.PHASE_MOVE:
							game.playPing();
							break;
						case GameState.PHASE_SHOOT:
							game.playExplosion();
							break;
						}
					} catch (Error e) {
						if (PartitionGame.DEBUG) {
							Gdx.app.log(TAG, "Invalid move!");
						}
						game.playError();
					}

					// TEMP: Reset board if game cannot proceed
					if (GameState.isGameOver(state)) {
						state = GameState.newGameState(boardConfiguration.boardSpec);
					} else if (GameState.isStalemate(state)) {
						state = GameState.newGameState(boardConfiguration.boardSpec);
					}

					// Update status message
					updateStatusMessage();
				}
			});
		}

		public byte[] actorCoordToTileCoord(final float px, final float py) {

			// Determine board size
			final float w = getWidth();
			final float h = getHeight();

			if (PartitionGame.DEBUG) {
				Gdx.app.log(TAG, "Board size: " + w + "x" + h);
				Gdx.app.log(TAG, "Touch location: (" + px + "," + py + ")");
			}

			// Convert to a coordinate in board space
			final byte c = (byte) (px / (w / boardColumns));
			final byte r = (byte) ((-(py - h)) / (h / boardRows));

			if (PartitionGame.DEBUG) {
				Gdx.app.log(TAG, "Touch is on tile: (" + c + "," + r + ")");
			}

			return new byte[] { c, r };
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {

			// Remember current transformation matrix
			final Matrix4 transformMatrix = batch.getTransformMatrix();

			// New transformation matrix, initially identical to the current matrix
			final Matrix4 newTransform = new Matrix4(transformMatrix);
			// Translate to widget's (x,y) coordinates, so (0,0) is in correct location
			newTransform.translate(getX(), getY(), 0.0f);
			// Scale up by the size of the widget, then down by the size of the board
			newTransform
					.scale(getWidth() / GameScreen.this.boardColumns, getHeight() / GameScreen.this.boardRows, 1.0f);
			// Use this new transformation matrix
			batch.setTransformMatrix(newTransform);

			// Draw tiles
			for (int c = 0; c < boardColumns; c++) {
				for (int r = 0; r < boardRows; r++) {
					if (GameScreen.this.state.tileEnabled[c][r]) {
						game.batch.draw(tileTexture, c, GameScreen.this.boardRows - 1 - r, 1.0f, 1.0f);
					}
				}
			}

			// Draw highlights
			for (int c = 0; c < boardColumns; c++) {
				for (int r = 0; r < boardRows; r++) {
					if (GameScreen.this.state.tileEnabled[c][r]) {
						game.batch.draw(highlightTexture, c, GameScreen.this.boardRows - 1 - r, 1.0f, 1.0f);
					}
				}
			}

			// Draw players
			// FUTURE: This is hard-coded for two players at this time. Should improve this.
			for (int p = 0; p < GameState.getNumberOfPlayers(state); p++) {
				final byte[] coords = GameState.getPlayerCoords(state, p);
				game.batch.draw(p == 0 ? redPlayerTexture : bluePlayerTexture, coords[0], GameScreen.this.boardRows - 1
						- coords[1], 1.0f, 1.0f);
			}

			// Reset transformation matrix
			batch.setTransformMatrix(transformMatrix);
		}
	}
}
