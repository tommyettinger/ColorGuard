package color.guard;

import color.guard.rules.PieceKind;
import color.guard.state.BattleState;
import color.guard.state.GameState;
import color.guard.state.Piece;
import color.guard.state.WorldState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.ArrayTools;
import squidpony.squidmath.*;

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

    private OrderedMap<String, Animation[]> standing = new OrderedMap<>(64),
            acting0 = new OrderedMap<>(64),
            acting1 = new OrderedMap<>(64),
            dying = new OrderedMap<>(64),
            receiving0 = new OrderedMap<>(64),
            receiving1 = new OrderedMap<>(64);
    
    //private OrthographicCamera camera;
    private Viewport viewport;
    BitmapFont font;

    private int[][] map;
    //private TextureAtlas.AtlasSprite[][] spriteMap;
    private int mapWidth, mapHeight;
    private float currentTime = 0f, turnTime = 0f;
    private RNG guiRandom;
    //private String displayString;
    //private InputMultiplexer input;
    private InputProcessor proc;
    private Vector3 tempVector3;
    private static final float visualWidth = 800f, visualHeight = 450f;
    //private StringBuilder tempSB;
    //private int drawCalls = 0, textureBindings = 0;
    public GameplayScreen(GameState state)
    {
        this.state = state;
        mapWidth = state.world.worldWidth;
        mapHeight = state.world.worldHeight;
    }

    @Override
    public void show() {
        guiRandom = new RNG(new FlapRNG(0x1337BEEF));
        viewport = new PixelPerfectViewport(Scaling.fill, visualWidth, visualHeight);
        //viewport = new ScreenViewport();
        viewport.getCamera().translate(1080, 1080f, 0f);
        viewport.getCamera().update();
        tempVector3 = new Vector3();
        palettes = new Texture("palettes.png");
        //tempSB = new StringBuilder(50);

        atlas = new TextureAtlas("Iso_Mini.atlas");
        font = new BitmapFont(Gdx.files.internal("NanoOKExtended.fnt"), atlas.findRegion("NanoOKExtended"));
        //font.getData().setScale(2f);
        font.setColor(1f / 255f, 1f, 1f, 1f);
        //displayString = state.world.mapGen.atlas.getAt(0);
        String s, r;
        terrains = new TextureAtlas.AtlasRegion[WorldState.terrains.size() * 4];
        for (int i = 0; i < terrains.length >> 2; i++) {
            terrains[i * 4]     = atlas.findRegion(WorldState.terrains.keyAt(i) + "_Huge_face0", 0);
            terrains[i * 4 + 1] = atlas.findRegion(WorldState.terrains.keyAt(i) + "_Huge_face1", 0);
            terrains[i * 4 + 2] = atlas.findRegion(WorldState.terrains.keyAt(i) + "_Huge_face2", 0);
            terrains[i * 4 + 3] = atlas.findRegion(WorldState.terrains.keyAt(i) + "_Huge_face3", 0);
        };
        int pieceCount = PieceKind.kinds.size(), facilityCount = PieceKind.facilities.size();
        PieceKind p;

        for (int i = 0; i < pieceCount; i++) {
            p = PieceKind.kinds.getAt(i);
            s = p.visual + "_Large_face";
            standing.put(p.name, new Animation[]{
                    new Animation<>(0.10f, atlas.createSprites(s + 0), Animation.PlayMode.LOOP),
                    new Animation<>(0.10f, atlas.createSprites(s + 1), Animation.PlayMode.LOOP),
                    new Animation<>(0.10f, atlas.createSprites(s + 2), Animation.PlayMode.LOOP),
                    new Animation<>(0.10f, atlas.createSprites(s + 3), Animation.PlayMode.LOOP)
            });
            s = p.visual + "_Large_face";
            if((p.weapons & 2) != 0) {
                acting0.put(p.name, new Animation[]{
                        new Animation<>(0.10f, atlas.createSprites(s + 0 + "_attack_0")),
                        new Animation<>(0.10f, atlas.createSprites(s + 1 + "_attack_0")),
                        new Animation<>(0.10f, atlas.createSprites(s + 2 + "_attack_0")),
                        new Animation<>(0.10f, atlas.createSprites(s + 3 + "_attack_0"))
                });
                r = p.show[0] + "_face";
                receiving0.put(p.name, new Animation[]{
                        new Animation<>(0.10f, atlas.createSprites(r + 0 + "_strength_" + p.shownStrengths[0])),
                        new Animation<>(0.10f, atlas.createSprites(r + 1 + "_strength_" + p.shownStrengths[0])),
                        new Animation<>(0.10f, atlas.createSprites(r + 2 + "_strength_" + p.shownStrengths[0])),
                        new Animation<>(0.10f, atlas.createSprites(r + 3 + "_strength_" + p.shownStrengths[0]))
                });
            }
            if((p.weapons & 1) != 0)
            {
                acting1.put(p.name, new Animation[]{
                        new Animation<>(0.10f, atlas.createSprites(s + 0 + "_attack_1")),
                        new Animation<>(0.10f, atlas.createSprites(s + 1 + "_attack_1")),
                        new Animation<>(0.10f, atlas.createSprites(s + 2 + "_attack_1")),
                        new Animation<>(0.10f, atlas.createSprites(s + 3 + "_attack_1"))
                });
                r = p.show[1] + "_face";
                receiving1.put(p.name, new Animation[]{
                        new Animation<>(0.10f, atlas.createSprites(r + 0 + "_strength_" + p.shownStrengths[1])),
                        new Animation<>(0.10f, atlas.createSprites(r + 1 + "_strength_" + p.shownStrengths[1])),
                        new Animation<>(0.10f, atlas.createSprites(r + 2 + "_strength_" + p.shownStrengths[1])),
                        new Animation<>(0.10f, atlas.createSprites(r + 3 + "_strength_" + p.shownStrengths[1]))
                });

            }
            dying.put(p.name, new Animation[]{
                    new Animation<>(0.10f, atlas.createSprites(s + 0 + "_death")),
                    new Animation<>(0.10f, atlas.createSprites(s + 1 + "_death")),
                    new Animation<>(0.10f, atlas.createSprites(s + 2 + "_death")),
                    new Animation<>(0.10f, atlas.createSprites(s + 3 + "_death"))
            });
        }
        for (int i = 0; i < facilityCount; i++) {
            p = PieceKind.facilities.getAt(i);
            s = p.visual + "_Large_face";
            standing.put(p.name, new Animation[]{
                    new Animation<>(0.10f, atlas.createSprites(s + 0), Animation.PlayMode.LOOP),
                    new Animation<>(0.10f, atlas.createSprites(s + 1), Animation.PlayMode.LOOP),
                    new Animation<>(0.10f, atlas.createSprites(s + 2), Animation.PlayMode.LOOP),
                    new Animation<>(0.10f, atlas.createSprites(s + 3), Animation.PlayMode.LOOP)
            });
            s = p.visual + "_Large_face";
            dying.put(p.name, new Animation[]{
                    new Animation<>(0.10f, atlas.createSprites(s + 0 + "_death")),
                    new Animation<>(0.10f, atlas.createSprites(s + 1 + "_death")),
                    new Animation<>(0.10f, atlas.createSprites(s + 2 + "_death")),
                    new Animation<>(0.10f, atlas.createSprites(s + 3 + "_death"))
            });
        }

        //GLProfiler.enable();
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
                "v_color.a = v_color.a * (255.0/254.0);\n" + //* (256.0/255.0)
                "v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                "gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                "}\n";
        String fragment =
                "#ifdef GL_ES\n" +
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
                "vec2 index = vec2(color.r * 255.0 / 255.5, v_color.r);\n" +
                "gl_FragColor = vec4(texture2D(u_texPalette, index).rgb, color.a);\n" +
                "}\n";
        indexShader = new ShaderProgram(vertex, fragment);
        if (!indexShader.isCompiled()) throw new GdxRuntimeException("Error compiling shader: " + indexShader.getLog());
        //spriteMap = new TextureAtlas.AtlasSprite[mapWidth][mapHeight];

        map = ArrayTools.copy(state.world.worldMap);
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
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
    /*
    private int getSlope(int[][] map, int x, int y)
    {
        int s = 0, h = WorldState.heights[map[x][y]];
        if(x > 0 && WorldState.heights[map[x - 1][y]] < h) s |= 1;
        if(y > 0 && WorldState.heights[map[x][y - 1]] < h) s |= 2;
        if(x < mapWidth - 1 && WorldState.heights[map[x + 1][y]] < h) s |= 4;
        if(y < mapHeight - 1 && WorldState.heights[map[x][y+1]] < h) s |= 8;
        return s;
    }
    */

    @Override
    public void render(float delta) {
        //GLProfiler.reset();
        Gdx.graphics.setTitle("Color Guard, running at " + Gdx.graphics.getFramesPerSecond() + " FPS");
        currentTime += delta;
        if((turnTime += delta) >= 1.5f)
        {
            turnTime = 0f;
            state.world.battle.advanceTurn();
        }

        //displayString = state.world.mapGen.atlas.getAt(((int)currentTime >>> 2) % 24 + 2);
        Gdx.gl.glClearColor(0.45F, 0.7F, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply(false);

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.setShader(indexShader);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
        palettes.bind();
        batch.begin();
        batch.setColor(208f / 255f, 1f, 1f, 1f);

        //indexShader.setUniformi("u_texPalette", 2);
        indexShader.setUniformi("u_texPalette", 1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        //textures.first().bind(1);
//        indexShader.setUniformi("u_texture", 1);

        int currentKind;
        Piece currentPiece;

        Vector3 position = viewport.getCamera().position;
        int centerX = -(int)((position.x) - 2 * (position.y)) >> 6,
                centerY = (int)((position.x) + 2 * (position.y)) >> 6,
                minX = Math.max(0, centerX - 14), maxX = Math.min(centerX + 14, mapWidth - 1),
                minY = Math.max(0, centerY - 14), maxY = Math.min(centerY + 14, mapHeight - 1);
        batch.setColor(208f / 255f, 1f, 1f, 1f);
        for (int x = maxX; x >= minX; x--) {
            for (int y = maxY; y >= minY; y--) {
                currentKind = map[x][y];
                batch.draw(terrains[currentKind], 32 * y - 32 * x, 16 * y + 16 * x);
            }
        }
        /*int centerX = (int)(position.x) >> 5,
                centerY = (int)(position.y) >> 5,
                minX = Math.max(0, centerX - 13), maxX = Math.min(centerX + 13, mapWidth - 1),
                minY = Math.max(0, centerY - 8), maxY = Math.min(centerY + 8, mapHeight - 1);
        for (int y = maxY; y >= minY; y--) {
            for (int x = maxX; x >= minX; x--) {
                currentKind = map[x][y];
                batch.setColor((208) / 255f, 1f, 1f, 1f);
                batch.draw(terrains[currentKind], 32 * x, 32 * y);
            }
        }
        */
        Sprite sprite;
        Coord c, n;
        BattleState battle = state.world.battle;
        OrderedMap<Coord, Piece> pieces = battle.pieces;
        float offX, offY;
        int idx;

        for (int y = maxY; y >= minY; y--) {
            for (int x = maxX; x >= minX; x--) {
                if((currentPiece = pieces.get(c = Coord.get(x, y))) != null) {
                    idx = pieces.indexOf(c);
                    n = battle.moveTargets.getAt(idx);
                    currentKind = currentPiece.kind << 2 | currentPiece.facing;

                    if(c.equals(n)) {
                        switch (currentPiece.pieceKind.weapons) {
                            case 2:
                                sprite = (Sprite) acting0.get(currentPiece.pieceKind.name)[currentKind & 3].getKeyFrame(turnTime, false);
                                offX = -40f;
                                offY = -20f;
                                break;
                            case 3:
                                sprite = (Sprite) (Light32RNG.determine(x * 31 + currentKind ^ y * 19) < 1 ? acting0 : acting1).get(currentPiece.pieceKind.name)[currentKind & 3].getKeyFrame(turnTime, false);
                                offX = -40f;
                                offY = -20f;
                                break;
                            case 1:
                                sprite = (Sprite) acting1.get(currentPiece.pieceKind.name)[currentKind & 3].getKeyFrame(turnTime, false);
                                offX = -40f;
                                offY = -20f;
                                break;
                            default:
                                sprite = (Sprite) standing.getAt(currentKind >>> 2)[currentKind & 3].getKeyFrame(currentTime, true);
                                offX = 0f;
                                offY = 0f;
                                break;
                        }
                        sprite.setColor(currentPiece.palette, 1f, 1f, 1f);
                        sprite.setPosition(32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 13f);
                        sprite.draw(batch);

                        batch.setColor(Math.max(1, currentPiece.paint) / 255f, 1f, 1f, 1f);
                        font.draw(batch, currentPiece.stats, 32 * (y - x) + 9f, 16 * (y + x) + 73f, 48f, Align.center, true);
                        //batch.setColor(-0x1.fffffep126f); // white as a packed float
                    }
                    else {
                        offX = MathUtils.lerp(0f, 32f * ((n.y - c.y) - (n.x - c.x)), Math.min(1f, turnTime * 1.6f));
                        offY = MathUtils.lerp(0f, 16f * ((n.y - c.y) + (n.x - c.x)), Math.min(1f, turnTime * 1.6f));
                        sprite = (Sprite) standing.getAt(currentKind >>> 2)[currentKind & 3].getKeyFrame(currentTime, true);
                        sprite.setColor(currentPiece.palette, 1f, 1f, 1f);
                        sprite.setPosition(32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 13f);
                        sprite.draw(batch);
                        batch.setColor(Math.max(1, currentPiece.paint) / 255f, 1f, 1f, 1f);
                        font.draw(batch, currentPiece.stats, 32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 73f, 48f, Align.center, true);
                        //batch.setColor(-0x1.fffffep126f); // white as a packed float
                    }
                }
            }
        }
        //batch.setColor(1f / 255f, 1f, 1f, 1f);
        //font.draw(batch, "DC: " + drawCalls + ", TBINDS: " + textureBindings, position.x, position.y, 100f, Align.center, true);

        //font.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()), position.x, position.y + 80);
        //font.draw(batch, displayString, -300, 1160); //state.world.mapGen.atlas.getAt(guiRandom.between(2, 26))
        //batch.setColor(-0x1.fffffep126f); // white as a packed float
        batch.end();
        //drawCalls = GLProfiler.drawCalls;
        //textureBindings = GLProfiler.textureBindings;
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