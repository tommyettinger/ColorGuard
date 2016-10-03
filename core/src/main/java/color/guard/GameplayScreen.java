package color.guard;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Gameplay screen of the application.
 */
public class GameplayScreen implements Screen {
    public ShaderProgram indexShader;

    @Override
    public void show() {
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
                "gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                "}\n";
        String fragment = "#ifdef GL_ES" +
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
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
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