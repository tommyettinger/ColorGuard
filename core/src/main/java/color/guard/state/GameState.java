package color.guard.state;

import squidpony.squidmath.StatefulRNG;

/**
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
        masterRandom = new StatefulRNG(seed);
        world = new WorldState(60, 60, masterRandom.nextLong());
    }
}
