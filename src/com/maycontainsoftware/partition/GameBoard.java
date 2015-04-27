package com.maycontainsoftware.partition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

/**
 * A representation of a game board.
 * 
 * @author Charlie
 */
public class GameBoard extends FixedSizeWidgetGroup implements IBoard {

	/** Tag for logging purposes. */
	private static final String TAG = GameBoard.class.getName();

	/** Reference to the Game instance. */
	protected final PartitionGame game;

	/** Reference to the texture atlas. */
	private final TextureAtlas atlas;

	/** Whether or not the game board is in "demo" mode. */
	private final boolean isDemoMode;

	/** The Arbiter that controls the game board. */
	private final Arbiter arbiter;

	/**
	 * Construct a new GameBoard.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 * @param atlas
	 *            The TextureAtlas containing all graphics required to draw the board.
	 * @param width
	 *            The (fixed) board width.
	 * @param height
	 *            The (fixed) board height.
	 * @param playerConfiguration
	 *            The chosen player configuration.
	 * @param boardConfiguration
	 *            The chosen board configuration.
	 * @param isDemoMode
	 *            Whether or not the game is running in demo mode, a mode which is intended for use on the main menu and
	 *            on the instructions screen.
	 */
	public GameBoard(final PartitionGame game, final TextureAtlas atlas, final float width, final float height,
			final PlayerConfiguration playerConfiguration, final BoardConfiguration boardConfiguration,
			final boolean isDemoMode) {

		super(width, height);

		// Save references to useful objects
		this.game = game;
		this.atlas = atlas;

		// Save board configuration
		this.isDemoMode = isDemoMode;

		// Create new game state
		final GameState state = GameState.newGameState(boardConfiguration.boardSpec);

		// Players
		final List<PlayerActor> players = new ArrayList<PlayerActor>(GameState.getNumberOfPlayers(state));
		for (int i = 0; i < GameState.getNumberOfPlayers(state); i++) {
			players.add(new PlayerActor(atlas, game.soundEngine, i));
		}

		// Tiles
		final Set<TileActor> tiles = new HashSet<TileActor>();
		for (byte c = 0; c < GameState.getNumberOfColumns(state); c++) {
			for (byte r = 0; r < GameState.getNumberOfRows(state); r++) {
				TileActor tile = new TileActor(atlas, game.soundEngine, c, r);
				tiles.add(tile);
			}
		}

		// Create the Arbiter
		arbiter = new Arbiter(state, this, players, tiles);

		// Direct tile input events to the arbiter's input method
		for (final TileActor tile : tiles) {
			tile.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					arbiter.input(tile);
					return true;
				}
			});
		}

		// Create user interface

		// Determine the area to be occupied by the board
		final Vector2 boardSize = Scaling.fit.apply(GameState.getNumberOfColumns(state),
				GameState.getNumberOfRows(state), width, height);

		// Determine the position of the board
		final Vector2 boardPosition = new Vector2((width - boardSize.x) / 2, (height - boardSize.y) / 2);

		// Determine the size of an individual tile (note: tiles are square!)
		final float tileSize = boardSize.x / GameState.getNumberOfColumns(state);

		for (final TileActor tile : tiles) {
			// Determine tile coordinates
			final byte[] coords = tile.getCoords();
			final byte x = coords[0];
			final byte y = coords[1];

			// Add tile to the board
			this.addActor(tile);

			// Set tile size
			tile.setSize(tileSize, tileSize);

			// Set tile position
			tile.setPosition(boardPosition.x + tileSize * x, boardPosition.y + boardSize.y - tileSize * (y + 1));
		}

		// Add players to the board
		for (PlayerActor player : players) {
			this.addActor(player);
		}

		// Reset the arbiter to set the game to its initial state
		arbiter.doReset();
	}

	@Override
	public void doWin(IPlayer winner, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doWin()");

		if (isDemoMode) {
			demoModeBoardReset();
		} else {
			notifyTilesOwned(playerTerritories);
			notifyTilesUnreachable(unreachable);

			this.addActor(makeEndSlate(winner.getPlayerNumber() == 0 ? SlateMessage.WIN_P0 : SlateMessage.WIN_P1));
		}
	}

	@Override
	public void doDraw(Set<IPlayer> winners, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doDraw()");

		if (isDemoMode) {
			demoModeBoardReset();
		} else {
			notifyTilesOwned(playerTerritories);
			notifyTilesUnreachable(unreachable);

			this.addActor(makeEndSlate(SlateMessage.DRAW));
		}
	}

	@Override
	public void doStalemate(Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doStalemate()");

		if (isDemoMode) {
			demoModeBoardReset();
		} else {
			notifyTilesUnreachable(unreachable);

			this.addActor(makeEndSlate(SlateMessage.STALEMATE));
		}
	}

	/**
	 * Notify tiles that were outright owned by a player at the end of a game of this fact.
	 * 
	 * @param playerTerritories
	 *            The player territories as supplied by the Arbiter.
	 */
	private void notifyTilesOwned(final Map<IPlayer, Set<ITile>> playerTerritories) {
		for (final IPlayer player : playerTerritories.keySet()) {
			final Set<ITile> tiles = playerTerritories.get(player);
			for (final ITile tile : tiles) {
				((TileActor) tile).endOwned(player.getPlayerNumber());
			}
		}
	}

	/**
	 * Notify tiles that were unreachable at the end of a game of this fact.
	 * 
	 * @param unreachable
	 *            The unreachable tiles as supplied by the Arbiter.
	 */
	private void notifyTilesUnreachable(final Set<ITile> unreachable) {
		for (final ITile tile : unreachable) {
			((TileActor) tile).endUnreachable();
		}
	}

	private void demoModeBoardReset() {
		this.addAction(Actions.sequence(Actions.delay(1.0f), Actions.fadeOut(0.25f), Actions.delay(0.5f), new Action() {
			@Override
			public boolean act(float delta) {
				arbiter.doReset();
				return true;
			}
		}, Actions.fadeIn(0.25f)));
	}

	/**
	 * An enumeration of all possible end-slate message, one for each possible game outcome.
	 * 
	 * @author Charlie
	 */
	private static enum SlateMessage {
		WIN_P0("Red player wins!", Color.RED),
		WIN_P1("Blue player wins!", Color.BLUE),
		DRAW("It's a draw!", Color.GRAY),
		STALEMATE("Stalemate!", Color.GRAY);

		/** The text of this message. */
		private final String text;

		/** The color associated with this message. */
		private final Color color;

		/**
		 * Construct a new end-slate message
		 * 
		 * @param text
		 *            The text of this message.
		 * @param color
		 *            The color associated with this message.
		 */
		private SlateMessage(final String text, final Color color) {
			this.text = text;
			this.color = color;
		}

		/** Get the text of this end-slate message. */
		public String getText() {
			return text;
		}

		/** Get the color associated with this end-slate message. */
		public Color getColor() {
			return color;
		}
	}

	/**
	 * Construct an end-slate suitable for display on this board. Note that the positioning of the slate depends on the
	 * board dimensions, so is only valid after the game board has been laid out.
	 * 
	 * @return The constructed end-slate.
	 */
	private Actor makeEndSlate(final SlateMessage message) {

		final Table slate = new Table();
		// Background texture
		slate.setBackground(new NinePatchDrawable(atlas.createPatch("slate_border")));
		// Slight alpha so board is still visible
		slate.setColor(1.0f, 1.0f, 1.0f, 0.75f);

		// Add message
		slate.row();
		final LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("segoeuiblack48.fnt")),
				message.getColor());
		slate.add(new Label(message.getText(), labelStyle)).colspan(2);

		// Add buttons
		slate.row();

		// Play again
		final Button playAgainButton = new Button(new TextureRegionDrawable(atlas.findRegion("play_again_off")),
				new TextureRegionDrawable(atlas.findRegion("play_again_on")));
		slate.add(playAgainButton).expand();
		playAgainButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				GameBoard.this.removeActor(slate);
				arbiter.doReset();
			}
		});

		// Main menu
		final Button mainMenuButton = new Button(new TextureRegionDrawable(atlas.findRegion("main_menu_off")),
				new TextureRegionDrawable(atlas.findRegion("main_menu_on")));
		slate.add(mainMenuButton).expand();
		mainMenuButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				game.setScreen(new MainMenuScreen(game));
			}
		});

		// Fixed size, based on button graphic sizes. Yes, this means this slate is inappropriate to display on very
		// small boards!
		slate.setSize(580, 180);
		// Center slate on the game board
		slate.setPosition(getWidth() / 2 - slate.getWidth() / 2, getHeight() / 2 - slate.getHeight() / 2);

		return slate;
	}
}
