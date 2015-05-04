package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Panel to select player configuration.
 * 
 * @author Charlie
 */
public class SelectPlayersPanel extends Table {

	/**
	 * Construct a new SelectPlayersPanel.
	 * 
	 * @param game
	 *            The PartitionGame instance.
	 */
	public SelectPlayersPanel(final PartitionGame game, final CardStack cardStack) {

		final TextureAtlas atlas = game.textureAtlas;

		final TextureRegion onePlayerUp = atlas.findRegion("1p_up");
		final TextureRegion onePlayerDown = atlas.findRegion("1p_down");
		final TextureRegion twoPlayersUp = atlas.findRegion("2p_up");
		final TextureRegion twoPlayersDown = atlas.findRegion("2p_down");

		defaults().pad(5.0f);

		// Spacer before heading
		row().expand();
		add();

		// Heading
		row().padBottom(30.0f);
		add(new Image(atlas.findRegion("select_players_heading")));

		// Two players
		row();
		final Button twoPlayersButton = new Button(new TextureRegionDrawable(twoPlayersUp), new TextureRegionDrawable(
				twoPlayersDown));
		add(twoPlayersButton);
		twoPlayersButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new SelectBoardPanel(game, cardStack, PlayerConfiguration.TWO_PLAYER));
			}
		});

		// One player versus computer
		row();
		final Button onePlayerButton = new Button(new TextureRegionDrawable(onePlayerUp), new TextureRegionDrawable(
				onePlayerDown));
		add(onePlayerButton);
		onePlayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.soundEngine.play(SoundEngine.SoundId.TONE);
				cardStack.push(new SelectBoardPanel(game, cardStack, PlayerConfiguration.ONE_PLAYER_VS_COMPUTER));
			}
		});

		// Spacer after
		row().expand();
		add();
	}
}
