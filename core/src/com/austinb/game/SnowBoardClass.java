package com.austinb.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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
	private long lastTreeMoveTime;
	private long lastPlayerMoveTime;

	private boolean alive;
	private BitmapFont font;

	private Random rand = new Random();
	private int score;

	private ImageButton pauseRestart;
	private ImageButton.ButtonStyle pauseRestartStyle;

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
		rec.y = screen_height - 60 - 40;
		rec.width = 60;
		rec.height = 60;
		leftt_rightf = true;

		trees = new Rectangle[20];
		for(int i = 0; i < 20 ; i++)
		{
			trees[i] = new Rectangle();
		}
		reinitialize ();
	}

	public void reinitialize () {
		SetTreePositions(false, 0, 0, 0);
		lastTreeMoveTime = TimeUtils.nanoTime();
		lastPlayerMoveTime = TimeUtils.nanoTime();

		alive = true;
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		score = 0;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		if((TimeUtils.nanoTime() - lastTreeMoveTime > 10000000) && alive)
			UpdateTreePositions();
		if(TimeUtils.nanoTime() - lastPlayerMoveTime > 1000000000 && alive) {
			score++;
			lastPlayerMoveTime = TimeUtils.nanoTime();
		}

		for (int i = 0; i < 20; i++) {
			if (rec.overlaps(trees[i])) {
				alive = false;
			}
		}

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
		if (!alive){
			font.draw(batch, "Dead", screen_width / 2 - 20, screen_height / 2);
		}
		font.draw(batch, Integer.toString(score), screen_width - 40, screen_height - 30);

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
			if (!alive) {
				reinitialize();
			}
			else {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos);
				//rec.x = touchPos.x - 64 / 2;
				if (touchPos.x < (screen_width / 2)) {
					leftt_rightf = true;
					if (rec.getX() > 0)
						rec.x = rec.getX() - 3;
				} else {
					leftt_rightf = false;
					if (rec.getX() < (screen_width - 60))
						rec.x = rec.getX() + 3;
				}
			}
		}
	}

	private void SetTreePositions (boolean setspecificpos, int specific, float xpos, float ypos) {
		if (setspecificpos) {
			trees[specific].x = xpos;
			trees[specific].y = ypos;
		}
		else {
			for (int i = 0; i < 20; i++) {
				trees[i].setX(rand.nextInt(screen_width) - 30);
				trees[i].setY(rand.nextInt(screen_height) - 39 - 300);
				trees[i].width = 30;
				trees[i].height = 39;
			}
		}
	}

	private void UpdateTreePositions () {
		for (int i = 0; i < 20; i++) {
			SetTreePositions(true, i, trees[i].x, (trees[i].y + 3));
			if (trees[i].y > screen_height) {
				trees[i].setX(rand.nextInt(screen_width - 30));
				trees[i].setY(0-40);
			}
		}
	}
}
