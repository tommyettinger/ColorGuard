package color.guard;

import color.guard.rules.PieceKind;
import color.guard.state.BattleState;
import color.guard.state.GameState;
import color.guard.state.Piece;
import color.guard.state.WorldState;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.ArrayTools;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.*;

/**
 * Gameplay screen of the application.
 * The majority of the code is here currently, and should be moved around to fit better.
 */
public class GameplayScreen implements Screen {
    public ShaderProgram indexShader;
    public TextureAtlas atlas;
    public GameState state;
    public Direction lastArrow = Direction.NONE;
    public static final int INPUT_LOCKED = 0, INPUT_ALLOWED = 1;
    public int inputMode = INPUT_ALLOWED;

    private Piece playerPiece;
    private DijkstraMap dijkstra;

    private SpriteBatch batch;
    private TextureAtlas.AtlasRegion[] terrains;
    private Texture palettes;

    private Animation[]
              standing
            , acting0
            , acting1
            , dying
            , receiving0
            , receiving1
            ;
    
    //private OrthographicCamera camera;
    private Viewport viewport;
    private Vector2 screenPosition = new Vector2(20, 20);
    private Coord targetCell;
    private BitmapFont font;

    private int[][] map;
    //private TextureAtlas.AtlasSprite[][] spriteMap;
    private int mapWidth, mapHeight;
    private float currentTime = 0f, turnTime = 0f;//, cameraTraversed = 0f;
    private RNG guiRandom;
    //private String displayString;
    //private InputMultiplexer input;
    private InputProcessor proc;
    private Vector3 tempVector3, nextCameraPosition; //prevCameraPosition;
    private static final float visualWidth = 800f, visualHeight = 450f;
    private StringBuilder tempSB;
    //private Noise.Noise3D fog;
    GLProfiler GLP;
    private int drawCalls = 0, textureBindings = 0;
    public GameplayScreen(GameState state)
    {
        this.state = state;
        mapWidth = state.world.worldWidth;
        mapHeight = state.world.worldHeight;
    }

    @Override
    public void show() {
        guiRandom = new RNG(new Zag32RNG(0x1337BEEFFEEDL));
        Gdx.gl.glDisable(GL20.GL_BLEND);
        viewport = new PixelPerfectViewport(Scaling.fill, visualWidth, visualHeight);
        //viewport = new ScreenViewport();
        //tempVector3 = new Vector3();
        viewport.getCamera().translate(1080, 1080f, 0f);
        viewport.getCamera().update();
        //prevCameraPosition = viewport.getCamera().position.cpy();
        nextCameraPosition = viewport.getCamera().position.cpy();
        atlas = new TextureAtlas("Iso_Mini.atlas");
        palettes = new Texture("palettes.png");
        tempSB = new StringBuilder(16);

        font = new BitmapFont(Gdx.files.internal("NanoOKExtended.fnt"), atlas.findRegion("NanoOKExtended"));
        //font.getData().setScale(2f);
        font.setColor(1f / 255f, 1f, 1f, 1f);
        //displayString = state.world.mapGen.atlas.getAt(0);
        String s, r;
        terrains = new TextureAtlas.AtlasRegion[WorldState.terrains.length << 2];
        for (int i = 0; i < terrains.length >> 2; i++) {
            terrains[i * 4]     = atlas.findRegion(WorldState.terrains[i] + "_Huge_face0", 0);
            terrains[i * 4 + 1] = atlas.findRegion(WorldState.terrains[i] + "_Huge_face1", 0);
            terrains[i * 4 + 2] = atlas.findRegion(WorldState.terrains[i] + "_Huge_face2", 0);
            terrains[i * 4 + 3] = atlas.findRegion(WorldState.terrains[i] + "_Huge_face3", 0);
        };
        int pieceCount = PieceKind.kinds.size(),
                facilityCount = PieceKind.facilities.size(),
                totalCount = pieceCount + facilityCount << 2;
        PieceKind p;
        standing   = new Animation[totalCount];
        acting0    = new Animation[totalCount];
        acting1    = new Animation[totalCount];
        dying      = new Animation[totalCount];
        receiving0 = new Animation[totalCount];
        receiving1 = new Animation[totalCount];
        for (int i = 0; i < pieceCount; i++) {
            p = PieceKind.kinds.getAt(i);
            s = p.visual + "_Large_face";
            standing[i << 2    ] = new Animation<>(0.10f, atlas.createSprites(s + 0), Animation.PlayMode.LOOP);
            standing[i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(s + 1), Animation.PlayMode.LOOP);
            standing[i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(s + 2), Animation.PlayMode.LOOP);
            standing[i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(s + 3), Animation.PlayMode.LOOP);
            s = p.visual + "_Large_face";
            if((p.weapons & 2) != 0) {
                acting0[i << 2    ] = new Animation<>(0.10f, atlas.createSprites(s + 0 + "_attack_0"));
                acting0[i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(s + 1 + "_attack_0"));
                acting0[i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(s + 2 + "_attack_0"));
                acting0[i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(s + 3 + "_attack_0"));
                r = p.show[0] + "_face";
                receiving0[i << 2    ] = new Animation<>(0.10f, atlas.createSprites(r + 0 + "_strength_" + p.shownStrengths[0]));
                receiving0[i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(r + 1 + "_strength_" + p.shownStrengths[0]));
                receiving0[i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(r + 2 + "_strength_" + p.shownStrengths[0]));
                receiving0[i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(r + 3 + "_strength_" + p.shownStrengths[0]));
            }
            if((p.weapons & 1) != 0)
            {
                acting1[i << 2    ] = new Animation<>(0.10f, atlas.createSprites(s + 0 + "_attack_1"));
                acting1[i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(s + 1 + "_attack_1"));
                acting1[i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(s + 2 + "_attack_1"));
                acting1[i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(s + 3 + "_attack_1"));
                r = p.show[1] + "_face";
                receiving1[i << 2    ] = new Animation<>(0.10f, atlas.createSprites(r + 0 + "_strength_" + p.shownStrengths[1]));
                receiving1[i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(r + 1 + "_strength_" + p.shownStrengths[1]));
                receiving1[i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(r + 2 + "_strength_" + p.shownStrengths[1]));
                receiving1[i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(r + 3 + "_strength_" + p.shownStrengths[1]));
            }
            dying[i << 2    ] = new Animation<>(0.10f, atlas.createSprites(s + 0 + "_death"));
            dying[i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(s + 1 + "_death"));
            dying[i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(s + 2 + "_death"));
            dying[i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(s + 3 + "_death"));
        }
        for (int i = 0; i < facilityCount; i++) {
            p = PieceKind.facilities.getAt(i);
            s = p.visual + "_Large_face";
            standing[pieceCount + i << 2    ] = new Animation<>(0.10f, atlas.createSprites(s + 0), Animation.PlayMode.LOOP);
            standing[pieceCount + i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(s + 1), Animation.PlayMode.LOOP);
            standing[pieceCount + i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(s + 2), Animation.PlayMode.LOOP);
            standing[pieceCount + i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(s + 3), Animation.PlayMode.LOOP);
            s = p.visual + "_Large_face";
            dying[pieceCount + i << 2    ] = new Animation<>(0.10f, atlas.createSprites(s + 0 + "_death"));
            dying[pieceCount + i << 2 | 1] = new Animation<>(0.10f, atlas.createSprites(s + 1 + "_death"));
            dying[pieceCount + i << 2 | 2] = new Animation<>(0.10f, atlas.createSprites(s + 2 + "_death"));
            dying[pieceCount + i << 2 | 3] = new Animation<>(0.10f, atlas.createSprites(s + 3 + "_death"));
        }
        GLP = new GLProfiler(Gdx.graphics);
        GLP.enable();
        state.world.startBattle(state.world.factions);
        state.world.battle.resistanceMaps();
        Coord playerPos = state.world.battle.pieces.firstKey();
        targetCell = playerPos;
        playerPiece = state.world.battle.pieces.getAt(0);
        dijkstra = new DijkstraMap(state.world.battle.resistances[playerPiece.pieceKind.mobility], DijkstraMap.Measurement.MANHATTAN);
        dijkstra.rng = new StatefulRNG(new Zag32RNG(123456789, 987654321));
        viewport.getCamera().position.set(32 * (playerPos.y - playerPos.x) + 9f, 16 * (playerPos.y + playerPos.x) + 13f, 0f);
        viewport.getCamera().update();
        //prevCameraPosition = viewport.getCamera().position.cpy();
        nextCameraPosition = viewport.getCamera().position.cpy();

        //fog = new Noise.Layered3D(SeededNoise.instance, 2, 0.015);

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
//        String fragment =
//                "#ifdef GL_ES\n" +
//                "#define LOWP lowp\n" +
//                "precision mediump float;\n" +
//                "#else\n" +
//                "#define LOWP\n" +
//                "#endif\n" +
//                "varying LOWP vec4 v_color;\n" +
//                "varying vec2 v_texCoords;\n" +
//                "uniform sampler2D u_texture;\n" +
//                "uniform sampler2D u_texPalette;\n" +
//                "// The MIT License\n" +
//                "// Copyright © 2013 Inigo Quilez\n" +
//                "// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n" +
//                "// Gradient Noise 3D             : https://www.shadertoy.com/view/Xsl3Dl\n" +
//                "//===============================================================================================\n" +
//                "vec3 hash( vec3 p ) // replace this by something better\n" +
//                "{\n" +
//                "    p = vec3( dot(p,vec3(127.1,311.7, 74.7)),\n" +
//                "        dot(p,vec3(269.5,183.3,246.1)),\n" +
//                "        dot(p,vec3(113.5,271.9,124.6)));\n" +
//                "    return -1.0 + 2.0*fract(sin(p)*43758.5453123);\n" +
//                "}\n" +
//                "float noise(vec3 p)\n" +
//                "{\n" +
//                "    vec3 i = floor(p);\n" +
//                "    vec3 f = fract(p);\n" +
//                "    vec3 u = f*f*(3.0-2.0*f);\n" +
//                "    return " +
//                        "   mix( mix( mix( dot( hash( i + vec3(0.0,0.0,0.0) ), f - vec3(0.0,0.0,0.0) ), \n" +
//                "                          dot( hash( i + vec3(1.0,0.0,0.0) ), f - vec3(1.0,0.0,0.0) ), u.x),\n" +
//                "                     mix( dot( hash( i + vec3(0.0,1.0,0.0) ), f - vec3(0.0,1.0,0.0) ), \n" +
//                "                          dot( hash( i + vec3(1.0,1.0,0.0) ), f - vec3(1.0,1.0,0.0) ), u.x), u.y),\n" +
//                "                mix( mix( dot( hash( i + vec3(0.0,0.0,1.0) ), f - vec3(0.0,0.0,1.0) ), \n" +
//                "                          dot( hash( i + vec3(1.0,0.0,1.0) ), f - vec3(1.0,0.0,1.0) ), u.x),\n" +
//                "                     mix( dot( hash( i + vec3(0.0,1.0,1.0) ), f - vec3(0.0,1.0,1.0) ), \n" +
//                "                          dot( hash( i + vec3(1.0,1.0,1.0) ), f - vec3(1.0,1.0,1.0) ), u.x), u.y), u.z)" +
//                        ";\n" +
//                "}\n" +
//                "const mat3 m = mat3( 0.00,  0.80,  0.60,\n" +
//                "                    -0.80,  0.36, -0.48,\n" +
//                "                    -0.60, -0.48,  0.64 );\n"+
//                "// End of MIT-licensed code\n"+
//                //Jim Hejl and Richard Burgess-Dawson's tone mapping formula
//                "vec3 tone(vec3 texColor, LOWP float change)\n" +
//                "{\n" +
//                "vec3 x = clamp((texColor * 0.666) - 0.004, 0.0, 100.0);\n" +
//                "return mix(texColor, (x*(6.2*x+.5))/(x*(6.2*x+1.7)+0.06), change);\n" +
//                "}\n" +
//                "void main()\n" +
//                "{\n" +
//                "vec4 color = texture2D(u_texture, v_texCoords);\n" +
//                "vec2 index = vec2(color.r * 255.0 / 255.5, v_color.r);\n" +
//                "vec3 q = vec3(0.01125 * gl_FragCoord.xy, v_color.g * 2.75);\n" +
////                "float f = 0.5000*noise( q ); q = m*q*2.01;\n" +
////                "f      += 0.3125*noise( q ); q = m*q*2.02;\n" +
////                "f      += 0.1875*noise( q );\n" +
////                "f      += 0.0625*noise( q ); q = m*q*2.01;\n" +
//                "gl_FragColor = vec4(tone(texture2D(u_texPalette, index).rgb, 0.65 - noise(q) * 0.75), color.a);\n" +
//                "}\n";
        indexShader = new ShaderProgram(vertex, fragment);
        if (!indexShader.isCompiled()) throw new GdxRuntimeException("Error compiling shader: " + indexShader.getLog());

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
                if(inputMode == INPUT_LOCKED)
                    return false;
                inputMode = INPUT_LOCKED;
                
//                prevCameraPosition.set(viewport.getCamera().position);
                nextCameraPosition.set(screenX, screenY, 0);
//                cameraTraversed = 0f;
                viewport.unproject(nextCameraPosition);
                //32 * y - 32 * x, 16 * y + 16 * x
                targetCell = Coord.get(MathUtils.round((nextCameraPosition.y - nextCameraPosition.x) * 0x1p-5f), MathUtils.round((nextCameraPosition.y + nextCameraPosition.x) * 0x1p-4f));
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if(inputMode == INPUT_LOCKED)
                    return false;
                switch (keycode)
                {
                    case Input.Keys.RIGHT: lastArrow = Direction.LEFT;
                        break;
                    case Input.Keys.LEFT: lastArrow = Direction.RIGHT;
                        break;
                    case Input.Keys.UP: lastArrow = Direction.DOWN;
                        break;
                    case Input.Keys.DOWN: lastArrow = Direction.UP;
                        break;
                    //default: lastArrow = Direction.NONE;
                    default: return false;
                }
                targetCell.translate(lastArrow);
                inputMode = INPUT_LOCKED;
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
        GLP.reset();
        Gdx.graphics.setTitle("Color Guard, running at " + Gdx.graphics.getFramesPerSecond() + " FPS");
        currentTime += delta;
//        float swap = (NumberTools.zigzag(currentTime * 1.141592653589793f)
//                + NumberTools.zigzag(currentTime * 1.218281828459045f)
//                + NumberTools.zigzag(currentTime * 0.718281828459045f)
//                + NumberTools.sway(currentTime * 0.141592653589793f)) * 0.125f + 0.5f;
        
//        cameraTraversed = Math.min(1f, cameraTraversed + delta * 8);
//        tempVector3.set(prevCameraPosition).lerp(nextCameraPosition, cameraTraversed);
//        viewport.getCamera().position.set(tempVector3);
        if(inputMode == INPUT_LOCKED)
        {
            
            inputMode = INPUT_ALLOWED;
        }
        else if((turnTime += delta) >= 1.5f)
        {
            turnTime = 0f;
            Coord pt = state.world.battle.moveTargets.first();
            Piece p = state.world.battle.pieces.alterAt(0, pt);
            p.faceDirection(lastArrow);
            Coord next = pt.translateCapped(lastArrow.deltaX, lastArrow.deltaY, map.length, map[0].length);
            if (!state.world.battle.pieces.containsKey(next)
                    && !state.world.battle.moveTargets.contains(next))
//                    && (p.pieceKind.permits & 1 << map[next.x][next.y]) != 0)
            {
                state.world.battle.moveTargets.alter(pt, next);
                lastArrow = Direction.NONE;
            }
            state.world.battle.advanceTurn();
        }

        //displayString = state.world.mapGen.atlas.getAt(((int)currentTime >>> 2) % 24 + 2);
        Gdx.gl.glClearColor(0.45F, 0.7F, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        int currentKind;
        Piece currentPiece;
        Sprite sprite;
        Coord c, n;
        BattleState battle = state.world.battle;
        OrderedMap<Coord, Piece> pieces = battle.pieces;
        float offX, offY;
        int idx;
        c = pieces.keyAt(0);
        n = battle.moveTargets.getAt(0);
        offX = MathUtils.lerp(0f, 32f * ((n.y - c.y) - (n.x - c.x)), Math.min(1f, turnTime * 1.6f));
        offY = MathUtils.lerp(0f, 16f * ((n.y - c.y) + (n.x - c.x)), Math.min(1f, turnTime * 1.6f));
        Vector3 position = viewport.getCamera().position.set(32 * (c.y - c.x) + offX + 9f, 16 * (c.y + c.x) + offY + 13f, 0f);
        viewport.getCamera().update();

        viewport.apply(false);

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.setShader(indexShader);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
        palettes.bind();
        batch.begin();

        //indexShader.setUniformi("u_texPalette", 2);
        indexShader.setUniformi("u_texPalette", 1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        //textures.first().bind(1);
//        indexShader.setUniformi("u_texture", 1);

        int centerX = -(int)((position.x) - 2 * (position.y)) >> 6,
                centerY = (int)((position.x) + 2 * (position.y)) >> 6,
                minX = Math.max(0, centerX - 13), maxX = Math.min(centerX + 14, mapWidth - 1),
                minY = Math.max(0, centerY - 14), maxY = Math.min(centerY + 13, mapHeight - 1);
        batch.setColor(208f / 255f, 1f, 1f, 1f);
        for (int x = maxX; x >= minX; x--) {
            for (int y = maxY; y >= minY; y--) {
                currentKind = map[x][y];
                //batch.setColor(208f / 255f, MathUtils.clamp((float) fog.getNoise(32 * y - 32 * x, 16 * y + 16 * x, currentTime * 60) * 0.7f + 0.7f, 0.1f, 1f), 1f, 1f);
                batch.draw(terrains[currentKind], 32 * y - 32 * x, 16 * y + 16 * x);
//                tempSB.setLength(0);
//                font.draw(batch, tempSB.append(state.world.bioGen.heatCodeData[x][y]),
//                        32 * y - 32 * x, 16 * y + 16 * x + 32, 64f, 1, false);
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
        for (int y = maxY; y >= minY; y--) {
            for (int x = maxX; x >= minX; x--) {
                if ((currentPiece = pieces.get(c = Coord.get(x, y))) != null) {
                    idx = pieces.indexOf(c);
                    n = battle.moveTargets.getAt(idx);
                    currentKind = currentPiece.kind << 2 | currentPiece.facing;
                    if (c.equals(n) && idx != 0) {
                        switch (currentPiece.pieceKind.weapons) {
                            case 2:
                                sprite = (Sprite) acting0[currentKind].getKeyFrame(turnTime, false);
                                offX = -40f;
                                offY = -20f;
                                break;
                            case 3:
                                sprite = (Sprite) (((currentKind * (x << 4 | 13) * (y << 4 | 11) & 256) == 0) ? acting0 : acting1)[currentKind].getKeyFrame(turnTime, false);
                                offX = -40f;
                                offY = -20f;
                                break;
                            case 1:
                                sprite = (Sprite) acting1[currentKind].getKeyFrame(turnTime, false);
                                offX = -40f;
                                offY = -20f;
                                break;
                            default:
                                sprite = (Sprite) standing[currentKind].getKeyFrame(currentTime, true);
                                offX = 0f;
                                offY = 0f;
                                break;
                        }
                        sprite.setColor(currentPiece.palette, 1f, 1f, 1f);
                        //MathUtils.clamp((float) fog.getNoise(32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 13f, currentTime * 60) * 0.7f + 0.7f, 0.1f, 1f)
                        sprite.setPosition(32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 13f);
                        sprite.draw(batch);

                        batch.setColor(Math.max(1, currentPiece.paint) / 255f, 0, 1, 1);
                        font.draw(batch, currentPiece.stats, 32 * (y - x) + 9f, 16 * (y + x) + 73f, 48f, Align.center, true);
                        //batch.setColor(-0x1.fffffep126f); // white as a packed float
                    } else {
                        offX = MathUtils.lerp(0f, 32f * ((n.y - c.y) - (n.x - c.x)), Math.min(1f, turnTime * 1.6f));
                        offY = MathUtils.lerp(0f, 16f * ((n.y - c.y) + (n.x - c.x)), Math.min(1f, turnTime * 1.6f));
                        sprite = (Sprite) standing[currentKind].getKeyFrame(currentTime, true);
                        sprite.setColor(currentPiece.palette, 1f, 1f, 1f);
                        //MathUtils.clamp((float) fog.getNoise(32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 13f, currentTime * 60) * 0.7f + 0.7f, 0.1f, 1f)
                        sprite.setPosition(32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 13f);
                        sprite.draw(batch);
                        batch.setColor(Math.max(1, currentPiece.paint) / 255f, 0, 1, 1);
                        font.draw(batch, currentPiece.stats, 32 * (y - x) + offX + 9f, 16 * (y + x) + offY + 73f, 48f, Align.center, true);
                        //batch.setColor(-0x1.fffffep126f); // white as a packed float
                    }
                }
            }
        }
        //batch.setColor(1f / 255f, 1f, 1f, 1f);
        //font.draw(batch, "DC: " + drawCalls + ", TBINDS: " + textureBindings, position.x, position.y, 100f, Align.center, true);
        drawCalls = GLP.getDrawCalls();
        textureBindings = GLP.getTextureBindings();
        tempSB.setLength(0);
        tempSB.append(Gdx.graphics.getFramesPerSecond()).append(" FPS, Draw Calls: ").append(drawCalls).append(", Texture Binds: ").append(textureBindings);
        screenPosition.set(16, 8);
        viewport.unproject(screenPosition);
        font.draw(batch, tempSB, screenPosition.x, screenPosition.y);
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