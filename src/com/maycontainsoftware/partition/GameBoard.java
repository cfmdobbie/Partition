package com.maycontainsoftware.partition;

import java.util.Map;
import java.util.Set;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.maycontainsoftware.partition.arbiter.IPlayer;
import com.maycontainsoftware.partition.arbiter.ITile;

/**
 * The main game board. This is a concrete implementation that displays a message on game end allowing the player to
 * restart or jump straight to the main menu.
 * 
 * @author Charlie
 */
public class GameBoard extends BaseGameBoard {

	/** Tag for logging purposes. */
	private static final String TAG = GameBoard.class.getName();

	private final CardStack cardStack;

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
	 */
	public GameBoard(final PartitionGame game, final TextureAtlas atlas, final float width, final float height,
			final PlayerConfiguration playerConfiguration, final BoardConfiguration boardConfiguration,
			final CardStack cardStack) {
		super(game, atlas, width, height, playerConfiguration, boardConfiguration, false);

		this.cardStack = cardStack;
	}

	@Override
	public void doWin(IPlayer winner, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doWin()");

		// Color owned tiles, remove unreachable tiles
		notifyTilesOwned(playerTerritories);
		notifyTilesUnreachable(unreachable);

		// Display the end-of-game slate
		this.addActor(makeEndSlate(winner.getPlayerNumber() == 0 ? SlateMessage.WIN_P0 : SlateMessage.WIN_P1));
	}

	@Override
	public void doDraw(Set<IPlayer> winners, Map<IPlayer, Set<ITile>> playerTerritories, Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doDraw()");

		// Color owned tiles, remove unreachable tiles
		notifyTilesOwned(playerTerritories);
		notifyTilesUnreachable(unreachable);

		// Display the end-of-game slate
		this.addActor(makeEndSlate(SlateMessage.DRAW));
	}

	@Override
	public void doStalemate(Set<ITile> unreachable) {

		Gdx.app.debug(TAG, "doStalemate()");

		// Remove unreachable tiles
		notifyTilesUnreachable(unreachable);

		// Display the end-of-game slate
		this.addActor(makeEndSlate(SlateMessage.STALEMATE));
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
				cardStack.first();
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
