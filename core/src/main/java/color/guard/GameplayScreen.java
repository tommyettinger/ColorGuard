package color.guard;

import color.guard.rules.PieceKind;
import color.guard.state.Faction;
import color.guard.state.GameState;
import color.guard.state.Piece;
import color.guard.state.WorldState;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.StatefulRNG;

/**
 * Gameplay screen of the application.
 * The majority of the code is here currently, and should be moved around to fit better.
 */
public class GameplayScreen implements Screen {
    public ShaderProgram indexShader;
    public TextureAtlas atlas;
    public GameState state;
    private SpriteBatch batch;
    private TextureAtlas.AtlasRegion[] terrains;
    private Texture palettes;
    private OrderedMap<String, Animation[]> standing = new OrderedMap<String, Animation[]>(64),
            acting0 = new OrderedMap<String, Animation[]>(64),
            acting1 = new OrderedMap<String, Animation[]>(64),
            dying = new OrderedMap<String, Animation[]>(64);

    private OrthographicCamera camera;
    private Viewport viewport;
    private Color currentPalette;
    ObjectSet<Texture> textures;
    BitmapFont font;

    private int[][] map;
    //private TextureAtlas.AtlasSprite[][] spriteMap;
    private int mapWidth, mapHeight;
    private float currentTime = 0f;
    private StatefulRNG guiRandom;
    //private String displayString;
    private InputMultiplexer input;
    private InputProcessor proc;
    private Vector3 tempVector3;
    private static final float visualWidth = 640f, visualHeight = 360f;
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
        viewport = new PixelPerfectViewport(Scaling.fill, visualWidth, visualHeight);
        //viewport = new ScreenViewport();
        viewport.getCamera().translate(0, 1080f, 0f);
        viewport.getCamera().update();
        tempVector3 = new Vector3();
        palettes = new Texture("palettes.png");
        currentPalette = new Color(208 / 255f, 1, 1, 1);

        atlas = new TextureAtlas("mini.atlas");
        textures = atlas.getTextures();
        font = new BitmapFont(Gdx.files.internal("NanoOKExtended.fnt"), atlas.findRegion("font/NanoOKExtended"));
        //font.getData().setScale(2f);
        font.setColor(Color.BLACK);
        //displayString = state.world.mapGen.atlas.getAt(0);
        String s;
        terrains = new TextureAtlas.AtlasRegion[WorldState.terrains.size() * 4];
        for (int i = 0; i < terrains.length >> 2; i++) {
            terrains[i * 4]     = atlas.findRegion("terrains/" + WorldState.terrains.getAt(i) + "_Huge_face0", 0);
            terrains[i * 4 + 1] = atlas.findRegion("terrains/" + WorldState.terrains.getAt(i) + "_Huge_face1", 0);
            terrains[i * 4 + 2] = atlas.findRegion("terrains/" + WorldState.terrains.getAt(i) + "_Huge_face2", 0);
            terrains[i * 4 + 3] = atlas.findRegion("terrains/" + WorldState.terrains.getAt(i) + "_Huge_face3", 0);
        };
        int pieceCount = PieceKind.kinds.size(), facilityCount = PieceKind.facilities.size();
        PieceKind p;
        for (int i = 0; i < pieceCount; i++) {
            p = PieceKind.kinds.getAt(i);
            s = "standing_frames/" + p.visual + "/" + p.visual + "_Large_face";
            standing.put(p.name, new Animation[]{
                    new Animation(0.09f, atlas.createSprites(s + 0), Animation.PlayMode.LOOP),
                    new Animation(0.09f, atlas.createSprites(s + 1), Animation.PlayMode.LOOP),
                    new Animation(0.09f, atlas.createSprites(s + 2), Animation.PlayMode.LOOP),
                    new Animation(0.09f, atlas.createSprites(s + 3), Animation.PlayMode.LOOP)
            });
            s = "animation_frames/" + p.visual + "/" + p.visual + "_Large_face";
            if((p.weapons & 2) != 0) {
                acting0.put(p.name, new Animation[]{
                        new Animation(0.09f, atlas.createSprites(s + 0 + "_attack_0")),
                        new Animation(0.09f, atlas.createSprites(s + 1 + "_attack_0")),
                        new Animation(0.09f, atlas.createSprites(s + 2 + "_attack_0")),
                        new Animation(0.09f, atlas.createSprites(s + 3 + "_attack_0"))
                });
            }
            if((p.weapons & 1) != 0)
            {
                acting1.put(p.name, new Animation[]{
                        new Animation(0.09f, atlas.createSprites(s + 0 + "_attack_1")),
                        new Animation(0.09f, atlas.createSprites(s + 1 + "_attack_1")),
                        new Animation(0.09f, atlas.createSprites(s + 2 + "_attack_1")),
                        new Animation(0.09f, atlas.createSprites(s + 3 + "_attack_1"))
                });
            }
            dying.put(p.name, new Animation[]{
                    new Animation(0.09f, atlas.createSprites(s + 0 + "_death")),
                    new Animation(0.09f, atlas.createSprites(s + 1 + "_death")),
                    new Animation(0.09f, atlas.createSprites(s + 2 + "_death")),
                    new Animation(0.09f, atlas.createSprites(s + 3 + "_death"))
            });
        }
        for (int i = 0; i < facilityCount; i++) {
            p = PieceKind.facilities.getAt(i);
            s = "standing_frames/" + p.visual + "/" + p.visual + "_Large_face";
            standing.put(p.name, new Animation[]{
                    new Animation(0.09f, atlas.createSprites(s + 0), Animation.PlayMode.LOOP),
                    new Animation(0.09f, atlas.createSprites(s + 1), Animation.PlayMode.LOOP),
                    new Animation(0.09f, atlas.createSprites(s + 2), Animation.PlayMode.LOOP),
                    new Animation(0.09f, atlas.createSprites(s + 3), Animation.PlayMode.LOOP)
            });
            s = "animation_frames/" + p.visual + "/" + p.visual + "_Large_face";
            dying.put(p.name, new Animation[]{
                    new Animation(0.09f, atlas.createSprites(s + 0 + "_death")),
                    new Animation(0.09f, atlas.createSprites(s + 1 + "_death")),
                    new Animation(0.09f, atlas.createSprites(s + 2 + "_death")),
                    new Animation(0.09f, atlas.createSprites(s + 3 + "_death"))
            });
        }

        state.world.startBattle(state.world.factions);

        /*pieces = new int[mapWidth][mapHeight];
        int[] tempOrdering = new int[pieceCount];
        for (int x = mapWidth - 1; x >= 0; x--) {
            CELL_WISE:
            for (int y = mapHeight - 1; y >= 0; y--) {
                if(guiRandom.next(4) == 0) {
                    guiRandom.randomOrdering(pieceCount, tempOrdering);
                    for (int i = 0; i < pieceCount; i++) {
                        if(PieceKind.kinds.getAt(tempOrdering[i]).mobilities[map[x][y]] < 6)
                        {
                            pieces[x][y] = tempOrdering[i] << 2 | guiRandom.next(2);
                            continue CELL_WISE;
                        }
                    }
                }
                pieces[x][y] = -1;
            }
        }
        */
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
        //spriteMap = new TextureAtlas.AtlasSprite[mapWidth][mapHeight];

        currentPalette.r = 208 / 255f;
        for (int x = mapWidth - 1; x >= 0; x--) {
            for (int y = mapHeight - 1; y >= 0; y--) {
                map[x][y] = map[x][y] * 4 + guiRandom.next(2);
            }
        }
        batch = new SpriteBatch();
        proc = new InputAdapter(){
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                tempVector3.set(screenX, screenY, 0);
                viewport.unproject(tempVector3);
                viewport.getCamera().position.set(tempVector3);
                return true;
            }
        };
        Gdx.input.setInputProcessor(proc);
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.setTitle("Color Guard, running at " + Gdx.graphics.getFramesPerSecond() + " FPS");
        currentTime += delta;

        //displayString = state.world.mapGen.atlas.getAt(((int)currentTime >>> 2) % 24 + 2);
        Gdx.gl.glClearColor(0.45F, 0.7F, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply(false);

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.setShader(indexShader);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        batch.begin();
        currentPalette.r = 208 / 255f;
        batch.setColor(currentPalette);
        palettes.bind(3);

        indexShader.setUniformi("u_texPalette", 3);
        textures.first().bind(2);
        indexShader.setUniformi("u_texture", 2);
        int currentKind;
        Piece currentPiece;

        Vector3 position = viewport.getCamera().position;
        int centerX = -(int)((position.x - 8) - 2 * (position.y - 4)) >> 6,
                centerY = (int)((position.x - 8) + 2 * (position.y - 4)) >> 6,
                minX = Math.max(0, centerX - 12), maxX = Math.min(centerX + 12, mapWidth - 1),
                minY = Math.max(0, centerY - 12), maxY = Math.min(centerY + 12, mapHeight - 1);

        for (int x = maxX; x >= minX; x--) {
            for (int y = maxY; y >= minY; y--) {
                currentKind = map[x][y];
                currentPalette.r = (208 + (currentKind >>> 2)) / 255f;
                batch.setColor(currentPalette);
                batch.draw(terrains[currentKind], 32 * y - 32 * x, 16 * y + 16 * x);
            }
        }
        Faction faction;
        Sprite sprite;
        Coord c;
        for (int x = maxX; x >= minX; x--) {
            for (int y = maxY; y >= minY; y--) {
                c = Coord.get(x, y);
                if((currentPiece = state.world.battle.pieces.getQFromA(c)) != null) {
                    currentKind = currentPiece.kind << 2 | currentPiece.facing;
                    sprite = (Sprite) standing.getAt(currentKind >>> 2)[currentKind & 3].getKeyFrame(currentTime, true);
                    sprite.setColor(currentPiece.palette, 1f, 1f, 1f);
                    sprite.setPosition(32 * y - 32 * x + 9f, 16 * y + 16 * x + 13f);
                    sprite.draw(batch);
                    //if(currentKind >>> 2 == standing.size() - 1)
                    //{
                        font.draw(batch, currentPiece.name, 32 * y - 32 * x - 32f, 16 * y + 16 * x + 72f, 128f, Align.center, true);
                    //}
                }
            }
        }
        //font.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()), -300, 1200);
        //font.draw(batch, displayString, -300, 1160); //state.world.mapGen.atlas.getAt(guiRandom.between(2, 26))
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