package color.guard;

import color.guard.state.GameState;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

/**
 * Entry
 */
public class ColorGuardGame extends Game {
    GameState state;
    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_INFO);
        //storage = new SquidStorage("ColorGuard");
        //state = storage.get("save0", "state", GameState.class);
        //if(state == null)
            state = new GameState(999999999L);
        setScreen(new GameplayScreen(state));
    }
}