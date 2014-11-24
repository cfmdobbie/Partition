package com.maycontainsoftware.partition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PartitionGame extends Game {
	
	public static final String TAG = PartitionGame.class.getName();
	public static final boolean DEBUG = true;
	
	//private OrthographicCamera camera;
	SpriteBatch batch;
	//private Texture texture;
	//private Sprite sprite;

	@Override
	public void create() {
		//float w = Gdx.graphics.getWidth();
		//float h = Gdx.graphics.getHeight();

		//camera = new OrthographicCamera(1, h / w);
		batch = new SpriteBatch();

		//texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		//texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		//TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);

		//sprite = new Sprite(region);
		//sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		//sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		//sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
		
		setScreen(new MyScreen(this));
	}

	@Override
	public void dispose() {
		batch.dispose();
		//texture.dispose();
	}

	@Override
	public void render() {
		// Clear screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		super.render();

		//batch.setProjectionMatrix(camera.combined);
		//batch.begin();
		//sprite.draw(batch);
		//batch.end();
	}
}
