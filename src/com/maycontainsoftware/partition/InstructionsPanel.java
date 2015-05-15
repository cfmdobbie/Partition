package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class InstructionsPanel extends Table {

	public InstructionsPanel(final PartitionGame game) {
		add(new ScrollPane(new InstructionsTable(game))).expand().fill();
	}

	private static class InstructionsTable extends Table {

		private final LabelStyle bodyStyle;
		private final LabelStyle titleStyle;

		public InstructionsTable(final PartitionGame game) {

			final TextureAtlas atlas = game.textureAtlas;

			defaults().pad(10.0f).expandX().fillX();

			// Text styles used on this panel
			bodyStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("segoeuiblack24.fnt")), Color.WHITE);
			titleStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("segoeuiblack32.fnt")), Color.BLUE);

			// Instructions

			row();
			add(title("Introduction"));

			row();
			add(body("Partition is a strategy game with very simple rules but one that requires careful thought to "
					+ "win.  The aim of the game is to separate yourself from your opponent, ending up on a larger "
					+ "patch of squares."));

			row();
			add(title("Rules of the Game"));

			row();
			add(body("A player moves in a straight line, in any direction: horizontally, vertically or diagonally.  "
					+ "After moving to a new square they must pick one unoccupied square for removal (the chosen "
					+ "square is also in a straight line, in any direction).  Then play moves to their opponent."));

			row();
			add(body("Players cannot jump over either their opponent or any removed squares, and the path to a square "
					+ "chosen to be removed must equally be clear."));

			row();
			add(body("The game is over when the two players are on entirely separate patches of squares (the player "
					+ "on the largest patch wins) or when the players are on the same patch but the current player "
					+ "cannot move (the game ends in a stalemate)."));

			// TODO: Explain control system?

			row();
			add(title("Examples"));

			row();
			add(body("The player left in the largest patch of squares wins.  Try it out!"));

			row();
			add(new DemoGameBoard(game, atlas, 125.0f * 4, 125.0f, PlayerConfiguration.TWO_PLAYER,
					BoardConfiguration.LOSE_DEMO));

			row();
			add(body("If the players end up in areas of equal size, the game is a draw:"));

			row();
			add(new DemoGameBoard(game, atlas, 125.0f * 3, 125.0f, PlayerConfiguration.TWO_PLAYER,
					BoardConfiguration.DRAW_DEMO));

			row();
			add(body("If the players are not isolated but a player cannot move, the game ends in a stalemate:"));

			row();
			add(new DemoGameBoard(game, atlas, 125.0f * 3, 125.0f, PlayerConfiguration.TWO_PLAYER,
					BoardConfiguration.STALEMATE_DEMO));
		}

		/** Utility method to make a new text label. */
		private Label makeLabel(final String text, final LabelStyle style) {
			final Label label = new Label(text, style);
			label.setWrap(true);
			return label;
		}

		private Label title(final String text) {
			return makeLabel(text, titleStyle);
		}

		private Label body(final String text) {
			return makeLabel(text, bodyStyle);
		}
	}
}
