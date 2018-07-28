package org.academiadecodigo.hackathon.echo;

import org.academiadecodigo.hackathon.echo.assets.GameProperties;
import org.academiadecodigo.hackathon.echo.assets.Levels;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class MainMenuScreen implements Screen {

    private final GKGame game;
    private OrthographicCamera camera;
    private Texture mainScreen;

    public MainMenuScreen(GKGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameProperties.WIDTH, GameProperties.HEIGHT);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();

        mainScreen = new Texture(Gdx.files.internal(GameProperties.MAIN_SCREEN));

        game.getBatch().draw(mainScreen, 0, 0 );
        game.getBatch().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game, Levels.LEVEL_1));
            dispose();
        }
    }

    @Override
    public void dispose() {
        mainScreen.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}