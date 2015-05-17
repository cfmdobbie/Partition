package com.maycontainsoftware.partition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Board selection card.
 * 
 * @author Charlie
 */
public class SelectBoardPanel extends Table {

	/** The PartitionGame reference. */
	private final PartitionGame game;

	/** The card stack to push new cards onto. */
	private final CardStack cardStack;

	/**
	 * The chosen player configuration. This has been chosen on the previous screen and must be passed to the GameScreen
	 * instance when it is created.
	 */
	private final PlayerConfiguration playerConfiguration;

	/**
	 * Construct a new SelectBoardScreen object.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 * @param playerConfiguration
	 *            The PlayerConfiguration that has already been selected.
	 */
	public SelectBoardPanel(final PartitionGame game, final CardStack cardStack,
			final PlayerConfiguration playerConfiguration) {

		// Store constructor parameter references
		this.game = game;
		this.cardStack = cardStack;
		this.playerConfiguration = playerConfiguration;

		// Spacer before heading
		row().expand();
		add();

		// Heading
		row().padBottom(30.0f);
		add(new Image(game.textureAtlas.findRegion("select_board_heading")));

		// Table to lay out board selection buttons
		final Table boardSelections = new Table();
		boardSelections.defaults().pad(5.0f);
		row();
		add(boardSelections);

		boardSelections.row();
		boardSelections.add(makeBoardButton("board_hub_up", "board_hub_down", BoardConfiguration.HUB));
		boardSelections.add(makeBoardButton("board_open_up", "board_open_down", BoardConfiguration.OPEN));

		boardSelections.row();
		boardSelections.add(makeBoardButton("board_wall_up", "board_wall_down", BoardConfiguration.WALL));
		boardSelections.add(makeBoardButton("board_holes_up", "board_holes_down", BoardConfiguration.HOLES));

		boardSelections.row();
		boardSelections.add(makeBoardButton("black", "black", BoardConfiguration.SMALL));
		boardSelections.add(makeBoardButton("black", "black", BoardConfiguration.CORE));

		boardSelections.row();
		boardSelections.add(makeBoardButton("black", "black", BoardConfiguration.CHEQUER));
		boardSelections.add(makeBoardButton("black", "black", BoardConfiguration.STRATEGO));

		// Spacer after
		row().expand();
		add();
	}

	/**
	 * Utility method to create buttons for selecting a board configuration.
	 * 
	 * @param textureOff
	 *            The "off" texture name.
	 * @param textureOn
	 *            The "on" texture name.
	 * @param boardConfiguration
	 *            The board configuration.
	 * @return The newly created button.
	 */
	private Button makeBoardButton(final String textureOff, final String textureOn,
			final BoardConfiguration boardConfiguration) {
		final Button button = new Button(new TextureRegionDrawable(game.textureAtlas.findRegion(textureOff)),
				new TextureRegionDrawable(game.textureAtlas.findRegion(textureOn)));
		button.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new GamePanel(game, cardStack, playerConfiguration, boardConfiguration));
			}
		});

		return button;
	}
}
