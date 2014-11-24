package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class MyScreen extends ScreenAdapter {

	public static final String TAG = MyScreen.class.getName();

	private final PartitionGame game;
	private Stage stage;

	public MyScreen(final PartitionGame game) {
		this.game = game;
		this.stage = new Stage();

		final Table root = new Table();
		root.setFillParent(true);
		root.defaults().pad(0.0f);
		root.debug();
		stage.addActor(root);

		// Spacer for advert
		root.row();
		// Empty cell of same height of advert
		float advertHeight = 50; // FIXME: Needs to be scaled based on device density!
		// System.out.println(Gdx.graphics.getPpiX());
		// System.out.println(Gdx.graphics.getPpiY());
		root.add().height(advertHeight);

		// Something before table?
		root.row();
		root.add().height(50.0f);

		// Space for the game board
		root.row().pad(10.0f);

		// root.add(new Swatch("yellow.png")).expand().fill();
		root.add(new BoardWidget()).expand().fill();

		/*
		 * final Table board = new Table(); board.debug(); root.add(board).expand().fill();
		 * 
		 * board.row(); board.add(new Swatch("red.png")).expand(); board.add(new Swatch("green.png")).expand();
		 * board.add(new Swatch("blue.png")).expand(); board.add(new Swatch("yellow.png")).expand(); board.add(new
		 * Swatch("cyan.png")).expand(); board.add(new Swatch("violet.png")).expand(); board.row(); board.add(new
		 * Swatch("green.png")).expand(); board.add(new Swatch("blue.png")); board.add(new Swatch("yellow.png"));
		 * board.add(new Swatch("cyan.png")); board.add(new Swatch("violet.png")); board.add(new Swatch("red.png"));
		 * board.row(); board.add(new Swatch("blue.png")).expand(); board.add(new Swatch("yellow.png")); board.add(new
		 * Swatch("cyan.png")); board.add(new Swatch("violet.png")); board.add(new Swatch("red.png")); board.add(new
		 * Swatch("green.png")); board.row(); board.add(new Swatch("yellow.png")).expand(); board.add(new
		 * Swatch("cyan.png")); board.add(new Swatch("violet.png")); board.add(new Swatch("red.png")); board.add(new
		 * Swatch("green.png")); board.add(new Swatch("blue.png"));
		 * 
		 * board.getChildren();
		 */

		// Something after table?
		root.row();
		root.add().height(50.0f);
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();

		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		if (PartitionGame.DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		stage.setViewport(width, height);
	}

	private static class Swatch extends Widget {
		private final Texture texture;

		public Swatch(final String textureName) {
			texture = new Texture(Gdx.files.internal(textureName));
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			// System.out.println(getWidth());
			// System.out.println(getHeight());
			// System.out.println(getX());
			// System.out.println(getY());
			batch.draw(texture, getX(), getY(), getWidth(), getHeight());
		}

		@Override
		public float getPrefHeight() {
			getParent().getHeight();
			return 10;
		}

		@Override
		public float getPrefWidth() {
			return 10;
		}

		@Override
		public void setWidth(float width) {
			super.setWidth(width);
		}

		@Override
		public void setHeight(float height) {
			super.setHeight(height);
		}
	}

	private static class BoardWidget extends Widget {

		float squareSize;
		float squareX;
		float squareY;

		@Override
		protected void sizeChanged() {
			System.out.println(getWidth());
			System.out.println(getHeight());

			squareSize = Math.min(getWidth(), getHeight());
			squareX = (getWidth() - squareSize) / 2 + getX();
			squareY = (getHeight() - squareSize) / 2 + getY();
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			final Texture boardTexture = new Texture(Gdx.files.internal("violet.png"));
			final Texture tileTexture = new Texture(Gdx.files.internal("yellow.png"));
			batch.draw(boardTexture, squareX, squareY, squareSize, squareSize);

			final int ROWS = 10;
			final int COLUMNS = 10;

			final float tileSize = squareSize / ROWS;

			final float tilePadding = 2.0f;

			for (int r = 0; r < ROWS; r++) {
				for (int c = 0; c < COLUMNS; c++) {
					batch.draw(tileTexture, squareX + c * tileSize + tilePadding, squareY + r * tileSize + tilePadding,
							tileSize - tilePadding * 2, tileSize - tilePadding * 2);
				}
			}

		}
	}
}
