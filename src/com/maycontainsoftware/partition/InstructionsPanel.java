package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.maycontainsoftware.partition.CardStack.Card;
import com.maycontainsoftware.partition.PartitionGame.BoardConfiguration;
import com.maycontainsoftware.partition.PartitionGame.PlayerConfiguration;

public class InstructionsPanel extends Card {

	public InstructionsPanel(final PartitionGame game) {

		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		// Text styles used on this panel
		final LabelStyle bodyStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("segoeuiblack24.fnt")),
				Color.WHITE);
		final LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("segoeuiblack32.fnt")),
				Color.BLUE);

		// Scrollable table to hold instructions
		final Table instructions = new Table();
		instructions.defaults().pad(10.0f).expandX().fillX();
		// Add table to panel, wrapped in a scrolling pane
		row();
		add(new ScrollPane(instructions)).expand().fill();

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
	}

	/** Utility method to make a new text label. */
	private Label makeLabel(final String text, final LabelStyle style) {
		final Label label = new Label(text, style);
		label.setWrap(true);
		return label;
	}
}
