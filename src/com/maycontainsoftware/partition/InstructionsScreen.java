package com.maycontainsoftware.partition;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class InstructionsScreen extends BaseScreen {

	public InstructionsScreen(final PartitionGame game) {
		super(game);

		@SuppressWarnings("unused")
		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		// Plan:
		// Back button to go to top menu
		// Sound button to toggle sound on/off
		// Scrolling pane containing VverticalGroup
		// Assets/controls to represent instruction information
	}
}
