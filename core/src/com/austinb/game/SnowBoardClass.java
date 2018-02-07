package com.austinb.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class SnowBoardClass extends ApplicationAdapter {
	private int screen_width = 480;
	private int screen_height = 800;

	private SpriteBatch batch;
	private Texture logo;
	private Texture tree;
	private Texture snowboard_left;
	private Texture snowboard_right;
	private Music snowMusic;

	private OrthographicCamera camera;

	private Rectangle rec;
	private boolean leftt_rightf;

	private Rectangle[] trees;
	private long lastMoveTime;

	Random rand = new Random();

	@Override
	public void create () {
		batch = new SpriteBatch();
		logo = new Texture("badlogic.jpg");
		tree = new Texture("tree.png");
		snowboard_left = new Texture("snowboarder_left.png");
		snowboard_right = new Texture("snowboarder_right.png");

		snowMusic = Gdx.audio.newMusic(Gdx.files.internal("frozen_wastes.mp3"));

		snowMusic.setLooping(true);
		snowMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, screen_width,screen_height);

		rec = new Rectangle();
		rec.x = screen_width / 2 - 60 / 2;
		rec.y = screen_height - 60 - 20;
		rec.width = 60;
		rec.height = 60;
		leftt_rightf = true;

		trees = new Rectangle[20];
		for(int i = 0; i < 20 ; i++)
		{
			trees[i] = new Rectangle();
		}
		SetTreePositions(false, 0, 0, 0);
		lastMoveTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		if(TimeUtils.nanoTime() - lastMoveTime > 10000000)
			UpdateTreePositions();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (int i = 0; i < 20; i++) {
			batch.draw(tree, trees[i].getX(), trees[i].getY());
		}
		if (leftt_rightf) {
			batch.draw(snowboard_left, rec.x, rec.y);
		} else {
			batch.draw(snowboard_right, rec.x, rec.y);
		}
		//batch.draw(logo, 0, 0);
		batch.end();

		InputMovement();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		logo.dispose();
		tree.dispose();
		snowboard_left.dispose();
		snowboard_right.dispose();

	}

	private void InputMovement () {
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			//rec.x = touchPos.x - 64 / 2;
			if (touchPos.x < (480 / 2))
				leftt_rightf = true;
			else
				leftt_rightf = false;
		}
	}

	private void SetTreePositions (boolean setspecificpos, int specific, float xpos, float ypos) {
		if (setspecificpos) {
			trees[specific].x = xpos;
			trees[specific].y = ypos;
		}
		else {
			for (int i = 0; i < 20; i++) {
				trees[i].setX(rand.nextInt(screen_width - 30));
				trees[i].setY(rand.nextInt(screen_height - 40));
				trees[i].width = 30;
				trees[i].height = 40;
			}
		}
	}

	private void UpdateTreePositions () {
		for (int i = 0; i < 20; i++) {
			SetTreePositions(true, i, trees[i].x, (trees[i].y + 1));
			if (trees[i].y > screen_height) {
				trees[i].setX(rand.nextInt(screen_width - 30));
				trees[i].setY(0-40);
			}
		}
	}
}
