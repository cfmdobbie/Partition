package com.maycontainsoftware.partition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

		final Swatch s1 = new Swatch("red.png");
		s1.setPosition(0, 0);
		s1.setSize(100, 100);
		root.addActor(s1);

		final Swatch s2 = new Swatch("green.png");
		s2.setPosition(100, 100);
		s2.setSize(100, 100);
		s2.addAction(Actions.forever(Actions.rotateBy(360.0f, 2.0f)));
		root.addActor(s2);

		final Swatch s3 = new Swatch("blue.png");
		s3.setPosition(200, 200);
		s3.setSize(100, 100);
		s3.addAction(Actions.forever(Actions.sequence(Actions.moveBy(100.0f, 0.0f, 1.0f),
				Actions.moveBy(-100.0f, 0.0f, 1.0f))));
		root.addActor(s3);

		Drawable up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("cyan.png"))));
		Drawable down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("violet.png"))));
		Drawable checked = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("yellow.png"))));
		final Button button1 = new Button(up, down, checked);
		button1.setPosition(300, 300);
		button1.setSize(100, 100);
		root.addActor(button1);
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

		// Redirect input events to the Stage
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	private static class Swatch extends Actor {
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
	}
}
