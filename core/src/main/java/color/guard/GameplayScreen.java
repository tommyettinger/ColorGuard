package color.guard;

import color.guard.state.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Scaling;

/**
 * Gameplay screen of the application.
 */
public class GameplayScreen implements Screen {
    public ShaderProgram indexShader;
    public TextureAtlas atlas;
    public GameState state;
    private SpriteBatch batch;
    private Sprite ocean, plains;
    private Texture palettes;
    private OrthographicCamera camera;
    private PixelPerfectViewport viewport;
    private Color currentPalette;
    ObjectSet<Texture> textures;

    private char[][] map;
    private int mapWidth, mapHeight;

    public GameplayScreen(GameState state)
    {
        this.state = state;
        map = state.world.worldMap;
        mapWidth = map.length;
        mapHeight = map[0].length;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(640f, 360f);
        viewport = new PixelPerfectViewport(Scaling.fill, 640f, 360f, camera);
        palettes = new Texture("palettes.png");
        currentPalette = new Color(209 / 256f, 1, 1, 1);

        atlas = new TextureAtlas("micro.atlas");
        textures = atlas.getTextures();
        ocean = atlas.createSprite("terrains/Ocean_Huge_face0_Normal", 0);
        plains = atlas.createSprite("terrains/Plains_Huge_face0_Normal", 0);

        String vertex = "attribute vec4 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "void main()\n" +
                "{\n" +
                "v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                "v_color.a = v_color.a * (256.0/255.0);\n" +
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

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.45F, 0.7F, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.setShader(indexShader);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        batch.begin();
        batch.setColor(currentPalette);
        palettes.bind(3);

        indexShader.setUniformi("u_texPalette", 3);
        textures.first().bind(2);
        indexShader.setUniformi("u_texture", 2);

        for (int x = mapWidth - 1; x >= 0; x--) {
            for (int y = mapHeight - 1; y >= 0; y--) {
                batch.draw(map[x][y] == '~' ? ocean : plains, 32 * y - 32 * x + 360f, 16 * x + 16 * y - 180f);
            }
        }
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
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