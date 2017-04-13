package color.guard.state;

import squidpony.FakeLanguageGen;
import squidpony.squidgrid.mapping.PoliticalMapper;
import squidpony.squidgrid.mapping.WorldMapGenerator;
import squidpony.squidmath.Arrangement;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.StatefulRNG;

/**
 * Really important class that calculates and stores a world map and the factions it holds.
 * Created by Tommy Ettinger on 10/3/2016.
 */
public class WorldState {
    public int worldWidth, worldHeight;
    public char[][] politicalMap;
    public int[][] worldMap;
    public WorldMapGenerator mapGen;
    public PoliticalMapper polGen;
    public Faction[] factions;
    public StatefulRNG worldRandom;
    public BattleState battle = null;
    public static final Arrangement<String> terrains = new Arrangement<>(
            new String[]{
            "Road",
            "Plains",
            "Forest",
            "Jungle",
            "Hill",
            "Mountain",
            "Ruins",
            "Sand",
            "Ice",
            "River",
            "Ocean",
            "Pit",
            "Volcano",
            "Poison",
            "Warning"
            });
/*
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
            "Ocean", "Ocean"
 */


    public static final int[] heights = {
            0, //road
            0, //plains
            1, //forest
            1, //jungle
            2, //hill
            3, //mountain
            1, //ruins
            0, //sand
            0, //ice
            0, //river
            0, //ocean
            0, //pit
            3, //volcano
            1, //poison
            1, //warning
    };
    String worldName;

    public WorldState() {
    }

    public WorldState(int width, int height, long seed) {
        worldWidth = Math.max(20, width);
        worldHeight = Math.max(20, height);
        worldRandom = new StatefulRNG(seed);
        worldName = FakeLanguageGen.FANTASY_NAME.word(worldRandom, true);
        mapGen = new WorldMapGenerator(seed, worldWidth, worldHeight);
        polGen = new PoliticalMapper(worldName);
        mapGen.generate(0.9, 1.0, seed);
        GreasedRegion land = new GreasedRegion(mapGen.heightCodeData, 4, 999);
        politicalMap = polGen.generate(land, 24, 0.97);
        CGBiomeMapper bioGen = new CGBiomeMapper();
        bioGen.makeBiomes(mapGen);
        worldMap = bioGen.biomeCodeData;

        factions = new Faction[24];
        String tempNation;
        for (char i = 'A'; i <= 'X'; i++) {
            tempNation = polGen.atlas.get(i);
            GreasedRegion territory = new GreasedRegion(politicalMap, i);
            factions[i - 'A'] = new Faction(i - 'A', tempNation, polGen.spokenLanguages.get(i), territory);
        }
    }
    public void startBattle(Faction... belligerents)
    {
        battle = new BattleState(worldRandom.nextLong(), worldMap, belligerents);
    }

    public static class CGBiomeMapper
    {
        /**
         * The heat codes for the analyzed map, from 0 to 5 inclusive, with 0 coldest and 5 hottest.
         */
        public int[][] heatCodeData,
        /**
         * The moisture codes for the analyzed map, from 0 to 5 inclusive, with 0 driest and 5 wettest.
         */
        moistureCodeData,
        /**
         * The biome codes for the analyzed map, corresponding to shown terrain types.
         */
        biomeCodeData;

        public static final double
                coldestValueLower = 0.0,   coldestValueUpper = 0.15, // 0
                colderValueLower = 0.15,   colderValueUpper = 0.31,  // 1
                coldValueLower = 0.31,     coldValueUpper = 0.5,     // 2
                warmValueLower = 0.5,      warmValueUpper = 0.69,    // 3
                warmerValueLower = 0.69,   warmerValueUpper = 0.85,  // 4
                warmestValueLower = 0.85,  warmestValueUpper = 1.0,  // 5

        driestValueLower = 0.0,    driestValueUpper  = 0.27, // 0
                drierValueLower = 0.27,    drierValueUpper   = 0.4,  // 1
                dryValueLower = 0.4,       dryValueUpper     = 0.6,  // 2
                wetValueLower = 0.6,       wetValueUpper     = 0.8,  // 3
                wetterValueLower = 0.8,    wetterValueUpper  = 0.9,  // 4
                wettestValueLower = 0.9,   wettestValueUpper = 1.0;  // 5
public static final int Road = 0,
            Plains = 1,
            Forest = 2,
            Jungle = 3,
            Hill = 4,
            Mountain = 5,
            Ruins= 6,
            Sand = 7,
            Ice = 8,
            River = 9,
            Ocean = 10;

        /**
         * The default biome table to use with biome codes from {@link #biomeCodeData}. Biomes are assigned based on
         * heat and moisture for the first 36 of 54 elements (coldest to warmest for each group of 6, with the first
         * group as the dryest and the last group the wettest), then the next 6 are for coastlines (coldest to warmest),
         * then rivers (coldest to warmest), then lakes (coldest to warmest).
         */
        public static final int[] biomeTable = {
                //COLDEST //COLDER //COLD  //HOT   //HOTTER //HOTTEST
                Ice,      Ice,     Plains, Sand,   Sand,    Sand,     //DRYEST
                Ice,      Ice,     Plains, Plains, Sand,    Sand,     //DRYER
                Ice,      Plains,  Forest, Plains, Plains,  Plains,   //DRY
                Ice,      Forest,  Forest, Forest, Plains,  Plains,   //WET
                Ice,      Forest,  Forest, Forest, Jungle,  Plains,   //WETTER
                Ice,      Forest,  Forest, Jungle, Jungle,  Jungle,   //WETTEST
                Ice,      Sand,    Sand,   Sand,   Sand,    Sand,     //COASTS
                Ice,      River,   River,  River,  River,   River,    //RIVERS
                Ice,      Ice,     River,  River,  River,   River,    //LAKES
        };

        public CGBiomeMapper()
        {
            heatCodeData = null;
            moistureCodeData = null;
            biomeCodeData = null;
        }

        public void makeBiomes(WorldMapGenerator world) {
            if(world == null || world.width <= 0 || world.height <= 0)
                return;
            if(heatCodeData == null || (heatCodeData.length != world.width || heatCodeData[0].length != world.height))
                heatCodeData = new int[world.width][world.height];
            if(moistureCodeData == null || (moistureCodeData.length != world.width || moistureCodeData[0].length != world.height))
                moistureCodeData = new int[world.width][world.height];
            if(biomeCodeData == null || (biomeCodeData.length != world.width || biomeCodeData[0].length != world.height))
                biomeCodeData = new int[world.width][world.height];
            final double i_hot = (world.maxHeat == world.minHeat) ? 1.0 : 1.0 / (world.maxHeat - world.minHeat);
            for (int x = 0; x < world.width; x++) {
                for (int y = 0; y < world.height; y++) {
                    final double hot = (world.heatData[x][y] - world.minHeat) * i_hot, moist = world.moistureData[x][y],
                            high = world.heightData[x][y] + NumberTools.bounce(world.heightData[x][y]);
                    final int heightCode = world.heightCodeData[x][y];
                    int hc, mc;
                    boolean isLake = world.generateRivers && world.partialLakeData.contains(x, y) && heightCode >= 4,
                            isRiver =
                                    (world.generateRivers && world.partialRiverData.contains(x, y) && heightCode >= 4);
                    if (moist > wetterValueUpper) {
                        mc = 5;
                    } else if (moist > wetValueUpper) {
                        mc = 4;
                    } else if (moist > dryValueUpper) {
                        mc = 3;
                    } else if (moist > drierValueUpper) {
                        mc = 2;
                    } else if (moist > driestValueUpper) {
                        mc = 1;
                    } else {
                        mc = 0;
                    }

                    if (hot > warmerValueUpper) {
                        hc = 5;
                    } else if (hot > warmValueUpper) {
                        hc = 4;
                    } else if (hot > coldValueUpper) {
                        hc = 3;
                    } else if (hot > colderValueUpper) {
                        hc = 2;
                    } else if (hot > coldestValueUpper) {
                        hc = 1;
                    } else {
                        hc = 0;
                    }

                    heatCodeData[x][y] = hc;
                    moistureCodeData[x][y] = mc;
                    biomeCodeData[x][y] = heightCode < 3
                            ? Ocean
                            : isLake
                            ? biomeTable[hc + 48]
                            : (isRiver
                            ? biomeTable[hc + 42]
                            :((heightCode == 4)
                            ? biomeTable[hc + 36]
                            : high > 0.95
                            ? Mountain
                            : high > 0.7
                            ? Hill
                            : biomeTable[hc + mc * 6]));
                }
            }
        }
    }
/*
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

        worldMap = ArrayTools.fill(10, worldWidth, worldHeight);
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

 */
}
