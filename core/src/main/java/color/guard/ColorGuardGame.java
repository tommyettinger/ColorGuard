package color.guard;

import color.guard.state.GameState;
import com.badlogic.gdx.Game;
import squidpony.SquidStorage;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ColorGuardGame extends Game {
    GameState state;
    SquidStorage storage;
    @Override
    public void create() {
        storage = new SquidStorage("ColorGuard");
        state = storage.get("save0", "state", GameState.class);
        if(state == null)
            state = new GameState(9999L);
        setScreen(new GameplayScreen(state));
    }
}