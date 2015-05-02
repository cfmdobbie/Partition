package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

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
			add(title("How To Play"));

			row();
			add(body("Partition is a game with very simple rules but one that requires careful thought to win.  The "
					+ "aim of the game is to acquire more area than your opponent."));

			row();
			add(body("Players take it in turns first to move to a new square, then to remove a square from the board."));

			row();
			add(body("Movements can be made any number of squares in a straight line (including diagonally).  The "
					+ "square that is removed must similarly be in a straight line from the player."));

			row();
			add(body("The game ends either when the players are separated, in which case the winner is the player on "
					+ "the largest patch of squares, or in a stalemate when both players are on the same patch of "
					+ "squares but a player cannot move."));

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
