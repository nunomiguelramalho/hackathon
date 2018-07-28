package org.academiadecodigo.hackathon.echo;

import org.academiadecodigo.hackathon.echo.assets.GameProperties;
import org.academiadecodigo.hackathon.echo.assets.Levels;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen implements Screen {

    private final GKGame game;

    private Texture graceImageRight;
    private Texture graceImageLeft;
    private Texture graceImage;

    private Texture key;
    private Texture closet;
    private Rectangle objectRect;
    private Texture objectText;

    private Texture npc;
    private Rectangle npcRect;

    private Music music;
    private OrthographicCamera camera;
    private Rectangle grace;

    private TiledMap mapTile;
    private OrthogonalTiledMapRenderer renderer;

    private boolean jump;
    private boolean canJump;

    private float base;

    private final int imageSize = 40;

    private final Levels level;
    private Texture graceImageRightKelly;
    private Texture graceImageLeftKelly;

    private boolean hasKey;
    private boolean isKelly;
    private Sound keySound;
    private Sound kissSound;
    private Sound teleportSound;

    public GameScreen(GKGame game, Levels level) {
        this.game = game;
        this.level = level;

        TmxMapLoader loader = new TmxMapLoader();

        mapTile = loader.load(level.path);

        renderer = new OrthogonalTiledMapRenderer(mapTile);

        camera = new OrthographicCamera(GameProperties.WIDTH, GameProperties.HEIGHT);
        camera.update();

        createTextures();
        createMusic();
        createObjects();
    }

    private void createObjects() {

        grace = GameObjectsFactory.makeObject(420, 1500, imageSize);
        objectRect = GameObjectsFactory.makeObject(level.keyX, level.keyY, imageSize);
        npcRect = GameObjectsFactory.makeObject(level.npcX, level.npcY, imageSize);
    }

    private void createMusic() {

        keySound = Gdx.audio.newSound(Gdx.files.internal(GameProperties.KEY_SOUND));
        kissSound = Gdx.audio.newSound(Gdx.files.internal(GameProperties.KISS_SOUND));
        teleportSound = Gdx.audio.newSound(Gdx.files.internal(GameProperties.TELEPORT_SOUND));
        music = Gdx.audio.newMusic(Gdx.files.internal(GameProperties.MUSIC));
        music.setLooping(true);
    }

    private void createTextures() {

        graceImageRight = new Texture(Gdx.files.internal(GameProperties.GK_RIGHT));
        graceImageLeft = new Texture(Gdx.files.internal(GameProperties.GK_LEFT));

        if (level == Levels.LEVEL_1) {
            graceImageRightKelly = new Texture(Gdx.files.internal(GameProperties.GKT_RIGHT));
            graceImageLeftKelly = new Texture(Gdx.files.internal(GameProperties.GKT_LEFT));
        } else {
            graceImageRightKelly = new Texture(Gdx.files.internal(GameProperties.FM_RIGHT));
            graceImageLeftKelly = new Texture(Gdx.files.internal(GameProperties.FM_LEFT));
        }

        graceImage = graceImageRight;

        key = new Texture(Gdx.files.internal(GameProperties.KEY));
        closet = new Texture(Gdx.files.internal(GameProperties.CLOSET));
        objectText = key;

        npc = new Texture(Gdx.files.internal(GameProperties.NPC));
    }

    @Override
    public void show() {
        music.play();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        setUpBash();
        checkInputs();
        movement(delta);

        camera.position.set(grace.x, camera.viewportHeight / 2f, 0);
        camera.update();

        checkCollision();

        //System.out.println(grace.x + " " + grace.y);
    }

    private void checkCollision() {

        for (MapObject object : mapTile.getLayers().get("ground").getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            if (grace.overlaps(rect)) {
                grace.y = rect.y + rect.height;
                base = grace.y;
                canJump = true;
            }

            if (grace.y >= base + 150) {
                grace.y = base + 150;
                jump = false;
            }
        }

        if (grace.overlaps(objectRect)) {
            keySound.play();
            objectRect.setX(level.closetX);
            objectRect.setY(level.closetY);
            objectText = closet;
            hasKey = true;
        }

        if (grace.overlaps(objectRect) && hasKey) {
            teleportSound.play();
            isKelly = true;
        }

        if (grace.overlaps(npcRect) && isKelly) {


            switch (level) {

                case LEVEL_1:
                    game.setScreen(new GameScreen(game, Levels.LEVEL_2));
                    dispose();
                    break;

                default:
                    kissSound.play();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                    break;
            }
        }
    }

    private void movement(float delta) {

        grace.y = jump ?
                grace.y + GameProperties.MOVEMENT_SPEED * delta :
                grace.y - GameProperties.MOVEMENT_SPEED * delta;

        if (grace.x < 420) {
            grace.x = 420;
        }

        if (grace.x > 5190) {
            grace.x = 5190;
        }

        if (grace.y < -250) {
            System.out.println("passou no grace < -100");
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }

        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.W)) && canJump) {
            jump = true;
            canJump = false;
        }
    }

    private void checkInputs() {

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.A)) {
            grace.x -= GameProperties.MOVEMENT_SPEED * Gdx.graphics.getDeltaTime();

            if (isKelly) {
                graceImage = graceImageLeftKelly;
            } else {
                graceImage = graceImageLeft;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.D)) {
            grace.x += GameProperties.MOVEMENT_SPEED * Gdx.graphics.getDeltaTime();

            if (isKelly) {
                graceImage = graceImageRightKelly;
            } else {
                graceImage = graceImageRight;
            }
        }
    }

    private void setUpBash() {

        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        game.getBatch().draw(graceImage, grace.x, grace.y, grace.width, grace.height);
        game.getBatch().draw(objectText, objectRect.x, objectRect.y, objectRect.width, objectRect.height);
        game.getBatch().draw(npc, npcRect.x, npcRect.y, npcRect.width, npcRect.height);
        game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {

        graceImage.dispose();
        graceImageLeft.dispose();
        graceImageRight.dispose();
        graceImageLeftKelly.dispose();
        graceImageRightKelly.dispose();
        key.dispose();
        closet.dispose();
        objectText.dispose();
        npc.dispose();
        music.dispose();
        keySound.dispose();
        kissSound.dispose();
        teleportSound.dispose();
    }
}