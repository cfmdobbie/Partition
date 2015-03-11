package com.maycontainsoftware.partition.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.maycontainsoftware.partition.BaseScreen;
import com.maycontainsoftware.partition.PartitionGame;

public class FontSizeTestScreen extends BaseScreen<PartitionGame> {
	public FontSizeTestScreen(final PartitionGame game) {
		super(game);

		final int[] fontSizes = { 16, 24, 32, 48, 64 };

		for (final int fontSize : fontSizes) {
			final BitmapFont font = new BitmapFont(Gdx.files.internal("segoeuiblack" + fontSize + ".fnt"));
			final LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

			root.row();
			root.add(new Label("Example text at " + fontSize + "pt", style));
		}

		// Other stuff
		final BitmapFont font = new BitmapFont(Gdx.files.internal("segoeuiblack16.fnt"));
		final LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

		root.row();
		root.add(new Label("Density: " + Gdx.graphics.getDensity(), style));
		root.row();
		root.add(new Label("PPI: " + Gdx.graphics.getPpiX() + "x" + Gdx.graphics.getPpiY(), style));
		root.row();
		root.add(new Label("Dimensions: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), style));
	}
}
