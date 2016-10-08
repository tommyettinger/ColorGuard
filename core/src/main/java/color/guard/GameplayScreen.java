package color.guard;

import color.guard.state.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.squidmath.StatefulRNG;

/**
 * Gameplay screen of the application.
 */
public class GameplayScreen implements Screen {
    public ShaderProgram indexShader;
    public TextureAtlas atlas;
    public GameState state;
    private SpriteBatch batch;
    private TextureAtlas.AtlasRegion ocean, plains;
    private Texture palettes;
    private Animation attack;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Color currentPalette;
    ObjectSet<Texture> textures;
    BitmapFont font;

    private char[][] map;
    private TextureAtlas.AtlasSprite[][] spriteMap;
    private Animation[][] animMap;
    private int mapWidth, mapHeight;
    private float currentTime = 0f;
    private StatefulRNG guiRandom;
    private String displayString;
    public GameplayScreen(GameState state)
    {
        this.state = state;
        map = state.world.worldMap;
        mapWidth = map.length;
        mapHeight = map[0].length;
    }

    @Override
    public void show() {
        guiRandom = new StatefulRNG(0L);
        viewport = new PixelPerfectViewport(Scaling.fill, 640f, 360f);
        //viewport = new ScreenViewport();
        viewport.getCamera().translate(0, 480f, 0f);
        viewport.getCamera().update();
        palettes = new Texture("palettes.png");
        currentPalette = new Color(208 / 255f, 1, 1, 1);

        atlas = new TextureAtlas("micro.atlas");
        textures = atlas.getTextures();
        font = new BitmapFont(Gdx.files.internal("NanoOKExtended.fnt"), atlas.findRegion("font/NanoOKExtended"));
        font.getData().setScale(2f);
        font.setColor(Color.BLACK);
        displayString = state.world.mapGen.atlas.getAt(state.masterRandom.between(2, 26));
        ocean = atlas.findRegion("terrains/Ocean_Huge_face0_Normal", 0);
        plains = atlas.findRegion("terrains/Plains_Huge_face0_Normal", 0);
        attack = new Animation(0.09f, atlas.createSprites("animation_frames/Tank/Tank_Large_face0_attack_0"), Animation.PlayMode.LOOP);
        String vertex = "attribute vec4 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "void main()\n" +
                "{\n" +
                "v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                "v_color.a = v_color.a * 1.0039216;\n" + //* (256.0/255.0)
                "v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                "gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                "}\n";
        String fragment = "#ifdef GL_ES\n" +
                "#define LOWP lowp\n" +
                "precision mediump float;\n" +
                "#else\n" +
                "#define LOWP\n" +
                "#endif\n" +
                "varying LOWP vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform sampler2D u_texPalette;\n" +
                "void main()\n" +
                "{\n" +
                "vec4 color = texture2D(u_texture, v_texCoords);\n" +
                "vec2 index = vec2(color.r, v_color.r);\n" +
                "gl_FragColor = vec4(texture2D(u_texPalette, index).rgb, color.a);\n" +
                "}\n";
        indexShader = new ShaderProgram(vertex, fragment);
        if (!indexShader.isCompiled()) throw new GdxRuntimeException("Error compiling shader: " + indexShader.getLog());
        spriteMap = new TextureAtlas.AtlasSprite[mapWidth][mapHeight];
        animMap = new Animation[mapWidth][mapHeight];

        currentPalette.r = 208 / 255f;
        for (int x = mapWidth - 1; x >= 0; x--) {
            for (int y = mapHeight - 1; y >= 0; y--) {
                if (map[x][y] == '~') {
                    spriteMap[x][y] = new TextureAtlas.AtlasSprite(ocean);
                }
                else
                {
                    spriteMap[x][y] = new TextureAtlas.AtlasSprite(plains);
                }
                spriteMap[x][y].setPosition(32 * y - 32 * x, 16 * x + 16 * y);
                spriteMap[x][y].setColor(currentPalette);
            }
        }
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        currentTime += delta;
        if(currentTime % 1.8f < 0.001f)
            displayString = state.world.mapGen.atlas.getAt(state.masterRandom.between(2, 26));
        Gdx.gl.glClearColor(0.45F, 0.7F, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply(false);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        guiRandom.setState(0L);

        batch.setShader(indexShader);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        batch.begin();
        currentPalette.r = 208 / 255f;
        batch.setColor(currentPalette);
        palettes.bind(3);

        indexShader.setUniformi("u_texPalette", 3);
        textures.first().bind(2);
        indexShader.setUniformi("u_texture", 2);

        for (int x = mapWidth - 1; x >= 0; x--) {
            for (int y = mapHeight - 1; y >= 0; y--) {
                spriteMap[x][y].draw(batch);
            }
        }
        for (int x = mapWidth - 1; x >= 0; x--) {
            for (int y = mapHeight - 1; y >= 0; y--) {
                if(map[x][y] != '~') {
                    currentPalette.r = guiRandom.nextIntHasty(208) / 255f;
                    batch.setColor(currentPalette);
                    batch.draw(attack.getKeyFrame(currentTime, true), 32 * y - 32 * x + 48f, 16 * x + 16 * y + 32f);
                }
            }
        }
        font.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()), -300, 640);
        font.draw(batch, displayString, -300, 560); //state.world.mapGen.atlas.getAt(guiRandom.between(2, 26))
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}