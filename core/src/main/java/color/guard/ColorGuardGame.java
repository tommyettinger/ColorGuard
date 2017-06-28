package color.guard;

import color.guard.state.GameState;
import com.badlogic.gdx.Game;

/**
 * Entry
 */
public class ColorGuardGame extends Game {
    GameState state;
    @Override
    public void create() {
        //storage = new SquidStorage("ColorGuard");
        //state = storage.get("save0", "state", GameState.class);
        //if(state == null)
            state = new GameState(999999999L);
        setScreen(new GameplayScreen(state));
    }
}