package color.guard.state;

import squidpony.FakeLanguageGen;
import squidpony.GwtCompatibility;
import squidpony.Maker;
import squidpony.Thesaurus;
import squidpony.squidgrid.mapping.SpillWorldMap;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.StatefulRNG;

import java.util.ArrayList;

/**
 * Really important class that calculates and stores a world map and the factions it holds.
 * Created by Tommy Ettinger on 10/3/2016.
 */
public class WorldState {
    public int worldWidth, worldHeight;
    public char[][] politicalMap;
    public int[][] worldMap;
    public SpillWorldMap mapGen;
    public Faction[] factions;
    public StatefulRNG worldRandom;
    public static final OrderedMap<String, String> terrains = Maker.makeOM(
            "Road", "Road",
            "Plains", "Plains",
            "Forest", "Forest",
            "Jungle", "Jungle",
            "Hill", "Hill",
            "Mountain", "Mountain",
            "Ruins", "Ruins",
            "Sand", "Desert",
            "Ice", "Tundra",
            "River", "River",
            "Ocean", "Ocean");
    String worldName;

    public WorldState() {
    }

    public WorldState(int width, int height, long seed) {
        worldWidth = Math.max(20, width);
        worldHeight = Math.max(20, height);
        worldRandom = new StatefulRNG(seed);
        worldName = FakeLanguageGen.FANTASY_NAME.word(worldRandom, true);
        mapGen = new SpillWorldMap(worldWidth, worldHeight, worldName);
        politicalMap = mapGen.generate(24, false);
        GreasedRegion land = new GreasedRegion(worldWidth, worldHeight),//.not(),
                water = new GreasedRegion(politicalMap, '~'), //.or(new GreasedRegion(politicalMap, '%'))
                tempCon;
        //water.spill(land, water.size() * 67 >>> 6, worldRandom);
        land.remake(water).not();

        politicalMap = land.mask(politicalMap, '~');

        //DungeonUtility.debugPrint(politicalMap);

        mapGen.atlas.clear();
        mapGen.atlas.put('~', "Water");
        mapGen.atlas.put('%', "Wilderness");
        Thesaurus th = new Thesaurus(worldRandom.nextLong());
        th.addKnownCategories();
        factions = new Faction[24];
        String tempNation;

        worldMap = GwtCompatibility.fill2D(10, worldWidth, worldHeight);
        worldMap = land.writeInts(worldMap, 1);
        ArrayList<GreasedRegion> continents = land.split(), tempRings;
        int cc = continents.size(), ringCount, continentSize;
        int wmax = Math.max(worldWidth, worldHeight), polarLimit = wmax * 2 / 3, tropicLimit = wmax >>> 2;
        GreasedRegion tempRegion = new GreasedRegion(worldWidth, worldHeight);
        Coord starter;
        for (int i = 0; i < cc; i++) {
            tempCon = continents.get(i);
            tempRings = tempCon.surfaceSeriesToLimit8way();
            ringCount = tempRings.size();
            if (ringCount <= 1) {
                worldMap = tempCon.writeInts(worldMap, 7);
            } else {
                continentSize = tempCon.size();
                worldMap = tempRings.get(0).writeInts(worldMap, 7);
                for (int j = 1; j < ringCount; j++) {
                    worldMap = tempRings.get(j).writeInts(worldMap, 1);
                }
                tempRegion.clear();
                tempRegion.insertSeveral(tempCon.randomPortion(worldRandom, continentSize / worldRandom.between(16, 20)));
                tempRegion.spill(tempCon, tempRegion.size() + continentSize / worldRandom.between(3, 6), worldRandom).expand8way().retract(2);
                if(worldRandom.next(3) > 4)
                {
                    starter = tempRegion.first();
                    if(Math.abs(starter.x + starter.y - wmax) < tropicLimit && worldRandom.next(3) < 5)
                        worldMap = tempRegion.writeInts(worldMap, 2);
                    else
                        worldMap = tempRegion.writeInts(worldMap, 7);
                }else
                    worldMap = tempRegion.writeInts(worldMap, 2);
                tempRegion.clear();
                tempRegion.insertSeveral(tempCon.randomPortion(worldRandom, continentSize / worldRandom.between(20, 25)));
                worldMap = tempRegion.writeInts(worldMap, 5);
                tempRegion.xor(tempRegion.copy().spill(tempCon, tempRegion.size() + continentSize / worldRandom.between(12, 16), worldRandom));
                worldMap = tempRegion.writeInts(worldMap, 4);
            }
        }
        continents = water.split();
        cc = continents.size();
        for (int i = 0; i < cc; i++) {
            tempCon = continents.get(i);
            continentSize = tempCon.size();
            if(continentSize < 9) {
                if (worldRandom.next(4) < 11) {
                    water.andNot(tempCon);
                    land.or(tempCon);
                    if(worldRandom.next(3) > 4)
                        worldMap = tempCon.writeInts(worldMap, 7);
                    else
                        worldMap = tempCon.writeInts(worldMap, 2);
                    worldMap = tempCon.fringe8way().writeInts(worldMap, 1);
                    starter = tempCon.singleRandom(worldRandom);
                    tempCon.not().mask(politicalMap, politicalMap[starter.x][starter.y]);
                } else {
                    worldMap = tempCon.writeInts(worldMap, 9);
                    starter = tempCon.singleRandom(worldRandom);
                    char glyph = politicalMap[starter.x][starter.y];
                    tempCon.not().mask(politicalMap, '~');
                    worldMap = tempCon.not().fringe8way().writeInts(worldMap, 1);
                    //tempCon.not().mask(politicalMap, glyph);

                    if(worldRandom.next(3) > 4)
                        worldMap = tempCon.removeSeveral(tempCon.randomPortion(worldRandom, tempCon.size() >>> 1)).writeInts(worldMap, 7);
                    else
                        worldMap = tempCon.removeSeveral(tempCon.randomPortion(worldRandom, tempCon.size() >>> 1)).writeInts(worldMap, 2);
                }
            }
        }
        for (int x = 1; x < worldWidth - 1; x++) {
            for (int y = 1; y < worldHeight - 1; y++) {
                if ((worldMap[x][y] < 4 || worldMap[x][y] == 7) && Math.abs(x + y - wmax) > polarLimit)
                    worldMap[x][y] = 8;
                else if(worldMap[x][y] == 10 && Math.abs(x + y - wmax) > polarLimit && worldRandom.next(5) < 3) {
                    worldMap[x][y] = 8;
                    politicalMap[x][y] = '%';
                }
                else if (worldMap[x][y] == 2 && Math.abs(x + y - wmax) < tropicLimit)
                    worldMap[x][y] = 3;
            }
        }
        worldMap = land.fringe8way().writeInts(worldMap, 9);
        for (char i = 'A'; i <= 'X'; i++) {
            tempNation = th.makeNationName();
            mapGen.atlas.put(i, tempNation);
            GreasedRegion territory = new GreasedRegion(politicalMap, i);
            if (th.randomLanguages.isEmpty()) {
                factions[i - 'A'] = new Faction(i - 'A', tempNation, FakeLanguageGen.randomLanguage(worldRandom.nextLong()), territory);
            } else {
                factions[i - 'A'] = new Faction(i - 'A', tempNation, th.randomLanguages.get(0), territory);
            }
        }
    }
}
