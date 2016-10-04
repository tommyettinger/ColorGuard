package color.guard.state;

import squidpony.FakeLanguageGen;
import squidpony.Thesaurus;
import squidpony.squidgrid.mapping.SpillWorldMap;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.StatefulRNG;

/**
 * Created by Tommy Ettinger on 10/3/2016.
 */
public class WorldState {
    public int worldWidth, worldHeight;
    public char[][] worldMap;
    public SpillWorldMap mapGen;
    public Faction[] factions;
    public StatefulRNG worldRandom;
    String worldName;
    public WorldState()
    {
    }
    public WorldState(int width, int height, long seed)
    {
        worldWidth = width;
        worldHeight = height;
        worldRandom = new StatefulRNG(seed);
        worldName = FakeLanguageGen.FANTASY_NAME.word(worldRandom, true);
        mapGen = new SpillWorldMap(width, height, worldName);
        worldMap = mapGen.generate(24, false);
        mapGen.atlas.clear();
        mapGen.atlas.put('~', "Water");
        mapGen.atlas.put('%', "Wilderness");
        Thesaurus th = new Thesaurus(worldRandom.nextLong());
        th.addKnownCategories();
        factions = new Faction[24];
        String tempNation;
        for (char i = 'A'; i <= 'X'; i++) {
            tempNation = th.makeNationName();
            mapGen.atlas.put(i, tempNation);
            if(th.randomLanguages.isEmpty())
            {
                factions[i-'A'] = new Faction(i-'A', tempNation, FakeLanguageGen.randomLanguage(worldRandom.nextLong()), new GreasedRegion(worldMap, i));
            }
            else
            {
                factions[i-'A'] = new Faction(i-'A', tempNation, th.randomLanguages.get(0), new GreasedRegion(worldMap, i));
            }
        }
    }
}
