package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Stages cannot be resized in libGDX 0.9.9. You can have a Stage that ignores aspect ratio and stretches to fit screen,
 * or one which maintains aspect ratio and adds black bars as required. You cannot have a Stage that will respect new
 * resolution and perform a re-layout. This is a bit of a problem, as I really want to work in native screen resolution,
 * but also want to support screen resizing.
 * 
 * The idea was to recreate the Stage with the same Actors but a new resolution on resize. This class is a test of that
 * concept, to see whether suitable state is maintained: size, position, Actions etc
 * 
 * @author Charlie
 * 
 */
public class ResizeableStageTest extends ScreenAdapter {

	private Stage stage;
	private final PartitionGame game;
	private final Table root;

	public ResizeableStageTest(PartitionGame game) {
		this.game = game;

		// Create the root table
		root = new Table();
		root.setFillParent(true);

		// Create all Actors and add them to the root Table

		root.row();

		final Swatch red = new Swatch("red.png");
		red.setSize(50, 50);
		root.add(red);

		final Swatch green = new Swatch("green.png");
		green.setSize(50, 50);
		root.add(green);

		final Swatch blue = new Swatch("blue.png");
		blue.setSize(50, 50);
		root.add(blue);

		root.add().expandX();

		final Swatch cyan = new Swatch("cyan.png");
		cyan.setSize(100, 50);
		root.add(cyan);

		root.row();

		final Swatch orange = new Swatch("orange.png");
		root.add(orange).colspan(5).fillX().height(10.0f);

		root.row();

		final Swatch yellow = new Swatch("yellow.png");
		yellow.setSize(100, 100);
		yellow.addAction(Actions.forever(Actions.rotateBy(360.0f, 2.0f)));
		root.add(yellow).colspan(5).expand();

		root.row();

		final Swatch violet = new Swatch("violet.png");
		root.add(violet).colspan(5).fillX().height(10.0f);

		root.row();

		Drawable up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("cyan.png"))));
		Drawable down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("violet.png"))));
		Drawable checked = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("yellow.png"))));
		final Button toggle = new Button(up, down, checked);
		toggle.addAction(Actions.forever(Actions.sequence(Actions.delay(2.0f), Actions.moveBy(10.0f, 0.0f, 0.125f),
				Actions.moveBy(-20.0f, 0.0f, 0.125f), Actions.moveBy(10.0f, 0.0f, 0.125f))));
		root.add(toggle).colspan(5).expandX();
	}

	@Override
	public void show() {
		System.out.println("show");
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log("test", "resize");

		// Dispose of old Stage
		if (stage != null) {
			stage.dispose();
			stage = null;
		}

		// Create new Stage
		stage = new Stage(width, height, true, game.batch);
		stage.addAction(Actions.sequence(Actions.delay(1.0f), new Action() {
			@Override
			public boolean act(float delta) {
				Gdx.app.log("test", "stage action complete");
				return true;
			}
		}));

		// Add root Table to the Stage
		stage.addActor(root);
		// root.invalidate();
		// root.layout();
		// root.validate();
		// root.invalidateHierarchy();

		// Redirect input events to the Stage
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	private static class Swatch extends Widget {
		private final TextureRegion region;

		public Swatch(final String textureName) {
			region = new TextureRegion(new Texture(Gdx.files.internal(textureName)));
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			// Note: need the extended version of the draw method to specify rotation
			batch.draw(region, getX(), getY(), getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1.0f, 1.0f,
					getRotation());
		}

		@Override
		public float getPrefHeight() {
			return region.getRegionHeight();
		}

		@Override
		public float getPrefWidth() {
			return region.getRegionWidth();
		}
	}
}
