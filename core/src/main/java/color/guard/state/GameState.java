package color.guard.state;

import com.badlogic.gdx.Gdx;
import squidpony.squidmath.GWTRNG;
import squidpony.squidmath.StatefulRNG;

/**
 * Tiny storage for all other parts of state, so this can be handed off to SquidStorage to save.
 * Created by Tommy Ettinger on 10/3/2016.
 */
public class GameState {
    public StatefulRNG masterRandom;
    public WorldState world;
    public GameState()
    {
    }
    public GameState(long seed)
    {
        masterRandom = new StatefulRNG(new GWTRNG(seed));
        //Gdx.app.setLogLevel(Application.LOG_INFO);
        seed = masterRandom.nextLong();
        Gdx.app.log("SEED", "World seed is " + seed);
        world = new WorldState(128, 128, seed);
    }
}
