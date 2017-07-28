package color.guard.state;

import squidpony.FakeLanguageGen;
import squidpony.Maker;
import squidpony.squidgrid.mapping.PoliticalMapper;
import squidpony.squidgrid.mapping.WorldMapGenerator;
import squidpony.squidmath.Arrangement;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.Noise;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.StatefulRNG;
import squidpony.squidmath.WhirlingNoise;

/**
 * Really important class that calculates and stores a world map and the factions it holds.
 * Created by Tommy Ettinger on 10/3/2016.
 */
public class WorldState {
    public int worldWidth, worldHeight;
    public char[][] politicalMap;
    public int[][] worldMap;
    public StandardMap mapGen;
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
        worldName = FakeLanguageGen.RUSSIAN_ROMANIZED.mix(FakeLanguageGen.FRENCH.removeAccents(), 0.57)
                .word(worldRandom, true);
        mapGen = new StandardMap(seed, worldWidth, worldHeight,
                WhirlingNoise.instance, 1.0);
        polGen = new PoliticalMapper(worldName);
        mapGen.generate(1.391, 1.02, seed);
        GreasedRegion land = new GreasedRegion(mapGen.heightCodeData, 4, 999);
        OrderedMap<Character, FakeLanguageGen> languageAtlas = Maker.<Character, FakeLanguageGen>makeOM(
                'A', FakeLanguageGen.INFERNAL,                                                          // dark
                'B', FakeLanguageGen.INUKTITUT,                                                         // white
                'C', FakeLanguageGen.ARABIC_ROMANIZED,                                                  // red
                'D', FakeLanguageGen.HINDI_ROMANIZED.removeAccents().mix(FakeLanguageGen.JAPANESE_ROMANIZED, 0.6),// orange
                'E', FakeLanguageGen.MONGOLIAN,                                                         // yellow
                'F', FakeLanguageGen.SWAHILI,                                                           // green
                'G', FakeLanguageGen.GREEK_ROMANIZED,                                                   // blue
                'H', FakeLanguageGen.NAHUATL.mix(FakeLanguageGen.RUSSIAN_ROMANIZED, 0.2), // purple
                'I', FakeLanguageGen.LOVECRAFT,                                                         // dark
                'J', FakeLanguageGen.ELF,                                                               // white
                'K', FakeLanguageGen.SOMALI,                                                            // red
                'L', FakeLanguageGen.SIMPLISH.mix(FakeLanguageGen.JAPANESE_ROMANIZED, 0.35), // orange
                'M', FakeLanguageGen.FRENCH,                                                            // yellow
                'N', FakeLanguageGen.GOBLIN.mix(FakeLanguageGen.SWAHILI, 0.47),            // green
                'O', FakeLanguageGen.RUSSIAN_ROMANIZED,                                                 // blue
                'P', FakeLanguageGen.HINDI_ROMANIZED.removeAccents().mix(FakeLanguageGen.NAHUATL, 0.65),  // purple
                'Q', FakeLanguageGen.DEMONIC.mix(FakeLanguageGen.SIMPLISH, 0.4),            // dark
                'R', FakeLanguageGen.INUKTITUT.mix(FakeLanguageGen.ELF, 0.6),              // white
                'S', FakeLanguageGen.ARABIC_ROMANIZED.mix(FakeLanguageGen.FANTASY_NAME, 0.55), // red
                'T', FakeLanguageGen.NORSE.addModifiers(FakeLanguageGen.Modifier.SIMPLIFY_NORSE),       // orange
                'U', FakeLanguageGen.FRENCH.mix(FakeLanguageGen.INFERNAL, 0.3),          // yellow
                'V', FakeLanguageGen.SWAHILI.mix(FakeLanguageGen.SOMALI, 0.35),            // green
                'W', FakeLanguageGen.RUSSIAN_ROMANIZED.mix(FakeLanguageGen.GOBLIN, 0.25),  // blue
                'X', FakeLanguageGen.NAHUATL.mix(FakeLanguageGen.MONGOLIAN, 0.43));        // purple
        politicalMap = polGen.generate(land, languageAtlas, 0.97);
        CGBiomeMapper bioGen = new CGBiomeMapper();
        bioGen.makeBiomes(mapGen);
        worldMap = bioGen.biomeCodeData;

        factions = new Faction[24];
        String tempNation;
        for (char i = 'A'; i <= 'X'; i++) {
            tempNation = polGen.atlas.get(i);
            System.out.printf("%s (%s)\n", tempNation, polGen.briefAtlas.get(i));
            GreasedRegion territory = new GreasedRegion(politicalMap, i);
            factions[i - 'A'] = new Faction(i - 'A', tempNation, languageAtlas.get(i), territory);
        }
    }
    public void startBattle(Faction... belligerents)
    {
        battle = new BattleState(worldRandom.nextLong(), worldMap, belligerents);
    }
    public static class StandardMap extends WorldMapGenerator {
        //protected static final double terrainFreq = 1.5, terrainRidgedFreq = 1.3, heatFreq = 2.8, moistureFreq = 2.9, otherFreq = 4.5;
        protected static final double terrainFreq = 1.175, terrainRidgedFreq = 1.3, heatFreq = 2.8, moistureFreq = 2.9, otherFreq = 4.5;
        private double minHeat0 = Double.POSITIVE_INFINITY, maxHeat0 = Double.NEGATIVE_INFINITY,
                minHeat1 = Double.POSITIVE_INFINITY, maxHeat1 = Double.NEGATIVE_INFINITY,
                minWet0 = Double.POSITIVE_INFINITY, maxWet0 = Double.NEGATIVE_INFINITY;

        public final Noise.Noise2D terrain, terrainRidged, heat, moisture, otherRidged;

        /**
         * Constructs a concrete WorldMapGenerator for a map does not tile. Always makes a 256x256 map.
         * Uses WhirlingNoise as its noise generator, with 1.0 as the octave multiplier affecting detail.
         * If you were using {@link squidpony.squidgrid.mapping.WorldMapGenerator.TilingMap#TilingMap(long, int, int, squidpony.squidmath.Noise.Noise4D, double)}, then this would be the
         * same as passing the parameters {@code 0x1337BABE1337D00DL, 256, 256, WhirlingNoise.instance, 1.0}.
         */
        public StandardMap() {
            this(0x1337BABE1337D00DL, 256, 256, WhirlingNoise.instance, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used as a tiling, wrapping east-to-west as well
         * as north-to-south.
         * Takes only the width/height of the map. The initial seed is set to the same large long
         * every time, and it's likely that you would set the seed when you call {@link #generate(long)}. The width and
         * height of the map cannot be changed after the fact, but you can zoom in.
         * Uses WhirlingNoise as its noise generator, with 1.0 as the octave multiplier affecting detail.
         *
         * @param mapWidth  the width of the map(s) to generate; cannot be changed later
         * @param mapHeight the height of the map(s) to generate; cannot be changed later
         */
        public StandardMap(int mapWidth, int mapHeight) {
            this(0x1337BABE1337D00DL, mapWidth, mapHeight, WhirlingNoise.instance, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used as a tiling, wrapping east-to-west as well
         * as north-to-south.
         * Takes an initial seed and the width/height of the map. The {@code initialSeed}
         * parameter may or may not be used, since you can specify the seed to use when you call {@link #generate(long)}.
         * The width and height of the map cannot be changed after the fact, but you can zoom in.
         * Uses WhirlingNoise as its noise generator, with 1.0 as the octave multiplier affecting detail.
         *
         * @param initialSeed the seed for the StatefulRNG this uses; this may also be set per-call to generate
         * @param mapWidth    the width of the map(s) to generate; cannot be changed later
         * @param mapHeight   the height of the map(s) to generate; cannot be changed later
         */
        public StandardMap(long initialSeed, int mapWidth, int mapHeight) {
            this(initialSeed, mapWidth, mapHeight, WhirlingNoise.instance, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used as a tiling, wrapping east-to-west as well
         * as north-to-south. Takes an initial seed, the width/height of the map, and a noise generator (a
         * {@link Noise.Noise4D} implementation, which is usually {@link WhirlingNoise#instance}. The {@code initialSeed}
         * parameter may or may not be used, since you can specify the seed to use when you call
         * {@link #generate(long)}. The width and height of the map cannot be changed after the fact, but you can zoom
         * in. Currently only WhirlingNoise makes sense to use as the value for {@code noiseGenerator}, and the seed it's
         * constructed with doesn't matter because it will change the seed several times at different scales of noise
         * (it's fine to use the static {@link WhirlingNoise#instance} because it has no changing state between runs of
         * the program; it's effectively a constant). The detail level, which is the {@code octaveMultiplier} parameter
         * that can be passed to another constructor, is always 1.0 with this constructor.
         *
         * @param initialSeed      the seed for the StatefulRNG this uses; this may also be set per-call to generate
         * @param mapWidth         the width of the map(s) to generate; cannot be changed later
         * @param mapHeight        the height of the map(s) to generate; cannot be changed later
         * @param noiseGenerator   an instance of a noise generator capable of 2D noise, often {@link WhirlingNoise}
         */
        public StandardMap(long initialSeed, int mapWidth, int mapHeight, final Noise.Noise2D noiseGenerator) {
            this(initialSeed, mapWidth, mapHeight, noiseGenerator, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used as a tiling, wrapping east-to-west as well
         * as north-to-south. Takes an initial seed, the width/height of the map, and parameters for noise
         * generation (a {@link Noise.Noise4D} implementation, which is usually {@link WhirlingNoise#instance}, and a
         * multiplier on how many octaves of noise to use, with 1.0 being normal (high) detail and higher multipliers
         * producing even more detailed noise when zoomed-in). The {@code initialSeed} parameter may or may not be used,
         * since you can specify the seed to use when you call {@link #generate(long)}. The width and height of the map
         * cannot be changed after the fact, but you can zoom in. Currently only WhirlingNoise makes sense to use as the
         * value for {@code noiseGenerator}, and the seed it's constructed with doesn't matter because it will change the
         * seed several times at different scales of noise (it's fine to use the static {@link WhirlingNoise#instance} because
         * it has no changing state between runs of the program; it's effectively a constant). The {@code octaveMultiplier}
         * parameter should probably be no lower than 0.5, but can be arbitrarily high if you're willing to spend much more
         * time on generating detail only noticeable at very high zoom; normally 1.0 is fine and may even be too high for
         * maps that don't require zooming.
         * @param initialSeed the seed for the StatefulRNG this uses; this may also be set per-call to generate
         * @param mapWidth the width of the map(s) to generate; cannot be changed later
         * @param mapHeight the height of the map(s) to generate; cannot be changed later
         * @param noiseGenerator an instance of a noise generator capable of 4D noise, almost always {@link WhirlingNoise}
         * @param octaveMultiplier used to adjust the level of detail, with 0.5 at the bare-minimum detail and 1.0 normal
         */
        public StandardMap(long initialSeed, int mapWidth, int mapHeight, final Noise.Noise2D noiseGenerator, double octaveMultiplier) {
            super(initialSeed, mapWidth, mapHeight);
            terrain = new Noise.Layered2D(noiseGenerator, (int) (0.5 + octaveMultiplier * 8), terrainFreq);
            terrainRidged = new Noise.Ridged2D(noiseGenerator, (int) (0.5 + octaveMultiplier * 10), terrainRidgedFreq);
            heat = new Noise.Layered2D(noiseGenerator, (int) (0.5 + octaveMultiplier * 3), heatFreq);
            moisture = new Noise.Layered2D(noiseGenerator, (int) (0.5 + octaveMultiplier * 4), moistureFreq);
            otherRidged = new Noise.Ridged2D(noiseGenerator, (int) (0.5 + octaveMultiplier * 6), otherFreq);
        }

        protected void regenerate(int startX, int startY, int usedWidth, int usedHeight,
                                  double waterMod, double coolMod, long state)
        {
            boolean fresh = false;
            if(cachedState != state || waterMod != waterModifier || coolMod != coolingModifier)
            {
                minHeight = Double.POSITIVE_INFINITY;
                maxHeight = Double.NEGATIVE_INFINITY;
                minHeat0 = Double.POSITIVE_INFINITY;
                maxHeat0 = Double.NEGATIVE_INFINITY;
                minHeat1 = Double.POSITIVE_INFINITY;
                maxHeat1 = Double.NEGATIVE_INFINITY;
                minHeat = Double.POSITIVE_INFINITY;
                maxHeat = Double.NEGATIVE_INFINITY;
                minWet0 = Double.POSITIVE_INFINITY;
                maxWet0 = Double.NEGATIVE_INFINITY;
                minWet = Double.POSITIVE_INFINITY;
                maxWet = Double.NEGATIVE_INFINITY;
                cachedState = state;
                fresh = true;
            }
            rng.setState(state);
            int seedA = rng.nextInt(), seedB = rng.nextInt(), seedC = rng.nextInt(), t;

            waterModifier = (waterMod <= 0) ? rng.nextDouble(0.29) + 0.91 : waterMod;
            coolingModifier = (coolMod <= 0) ? rng.nextDouble(0.45) * (rng.nextDouble()-0.5) + 1.1 : coolMod;

            double p, q,
                    ps, pc,
                    qs, qc,
                    h, temp,
                    xPos, yPos = startY,
                    i_uw = usedWidth / (double)width, i_uh = usedHeight / (double)height,
                    wh = (width + height) * 0.5, i_wh = 1.0 / wh, subtle = 5.5 / wh;
            for (int y = 0; y < height; y++, yPos += i_uh) {
                xPos = startX;
                for (int x = 0; x < width; x++, xPos += i_uw) {
                    q = (xPos + yPos - wh) * i_wh;
                    p = (xPos - yPos) * i_wh;
                    h = terrain.getNoiseWithSeed(p +
                                    terrainRidged.getNoiseWithSeed(p, q, seedA + seedB),
                            q, seedA);
                    h -= Math.max(
                            Math.max(Math.max(-(xPos - (width >>> 3)), 0), Math.max(xPos - (width * 7 >>> 3), 0)),
                            Math.max(Math.max(-(yPos - (height >>> 3)), 0), Math.max(yPos - (height * 7 >>> 3), 0))) * subtle;
                    h *= waterModifier;
                    heightData[x][y] = h;
                    heatData[x][y] = (pc = heat.getNoiseWithSeed(p, q
                                    + otherRidged.getNoiseWithSeed(p, q, seedB + seedC)
                            , seedB));
                    moistureData[x][y] = (temp = moisture.getNoiseWithSeed(p, q
                                    + otherRidged.getNoiseWithSeed(p, q, seedC + seedA)
                            , seedC));
                    minHeightActual = Math.min(minHeightActual, h);
                    maxHeightActual = Math.max(maxHeightActual, h);
                    if(fresh) {
                        minHeight = Math.min(minHeight, h);
                        maxHeight = Math.max(maxHeight, h);

                        minHeat0 = Math.min(minHeat0, pc);
                        maxHeat0 = Math.max(maxHeat0, pc);

                        minWet0 = Math.min(minWet0, temp);
                        maxWet0 = Math.max(maxWet0, temp);

                    }
                }

            }
            minHeightActual = Math.min(minHeightActual, minHeight);
            maxHeightActual = Math.max(maxHeightActual, maxHeight);
            double heightDiff = 2.0 / (maxHeightActual - minHeightActual),
                    heatDiff = 0.8 / (maxHeat0 - minHeat0),
                    wetDiff = 1.0 / (maxWet0 - minWet0),
                    hMod;
            double minHeightActual0 = minHeightActual;
            double maxHeightActual0 = maxHeightActual;
            yPos = startY;
            ps = Double.POSITIVE_INFINITY;
            pc = Double.NEGATIVE_INFINITY;

            for (int y = 0; y < height; y++, yPos += i_uh) {
                xPos = startX;
                for (int x = 0; x < width; x++, xPos += i_uw) {
                    temp = Math.abs(xPos + yPos - wh) * i_wh;
                    temp *= (2.4 - temp);
                    temp = 2.2 - temp;
                    heightData[x][y] = (h = (heightData[x][y] - minHeightActual) * heightDiff - 1.0);
                    minHeightActual0 = Math.min(minHeightActual0, h);
                    maxHeightActual0 = Math.max(maxHeightActual0, h);
                    heightCodeData[x][y] = (t = codeHeight(h));
                    hMod = 1.0;
                    switch (t) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            h = 0.4;
                            hMod = 0.2;
                            break;
                        case 6:
                            h = -0.1 * (h - forestLower - 0.08);
                            break;
                        case 7:
                            h *= -0.25;
                            break;
                        case 8:
                            h *= -0.4;
                            break;
                        default:
                            h *= 0.05;
                    }
                    heatData[x][y] = (h = (((heatData[x][y] - minHeat0) * heatDiff * hMod) + h + 0.6) * temp);
                    if (fresh) {
                        ps = Math.min(ps, h); //minHeat0
                        pc = Math.max(pc, h); //maxHeat0
                    }
                }
            }
            if(fresh)
            {
                minHeat1 = ps;
                maxHeat1 = pc;
            }
            heatDiff = coolingModifier / (maxHeat1 - minHeat1);
            qs = Double.POSITIVE_INFINITY;
            qc = Double.NEGATIVE_INFINITY;
            ps = Double.POSITIVE_INFINITY;
            pc = Double.NEGATIVE_INFINITY;


            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    heatData[x][y] = (h = ((heatData[x][y] - minHeat1) * heatDiff));
                    moistureData[x][y] = (temp = (moistureData[x][y] - minWet0) * wetDiff);
                    if (fresh) {
                        qs = Math.min(qs, h);
                        qc = Math.max(qc, h);
                        ps = Math.min(ps, temp);
                        pc = Math.max(pc, temp);
                    }
                }
            }
            if(fresh)
            {
                minHeat = qs;
                maxHeat = qc;
                minWet = ps;
                maxWet = pc;
            }
            landData.refill(heightCodeData, 4, 999);
            if(generateRivers) {
                if (fresh) {
                    addRivers();
                    riverData.connect8way().thin().thin();
                    lakeData.connect8way().thin();
                    partialRiverData.remake(riverData);
                    partialLakeData.remake(lakeData);
                } else {
                    partialRiverData.remake(riverData);
                    partialLakeData.remake(lakeData);
                    for (int i = 1; i <= zoom; i++) {
                        int stx = (startCacheX.get(i) - startCacheX.get(i - 1)) << (i - 1),
                                sty = (startCacheY.get(i) - startCacheY.get(i - 1)) << (i - 1);
                        if ((i & 3) == 3) {
                            partialRiverData.zoom(stx, sty).connect8way();
                            partialRiverData.or(workingData.remake(partialRiverData).fringe().separatedRegionZCurve(0.4));
                            partialLakeData.zoom(stx, sty).connect8way();
                            partialLakeData.or(workingData.remake(partialLakeData).fringe().separatedRegionZCurve(0.55));
                        } else {
                            partialRiverData.zoom(stx, sty).connect8way().thin();
                            partialRiverData.or(workingData.remake(partialRiverData).fringe().separatedRegionZCurve(0.5));
                            partialLakeData.zoom(stx, sty).connect8way().thin();
                            partialLakeData.or(workingData.remake(partialLakeData).fringe().separatedRegionZCurve(0.7));
                        }
                    }
                }
            }
        }
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
                colderValueLower = 0.15,   colderValueUpper = 0.3,   // 1
                coldValueLower = 0.3,      coldValueUpper = 0.5,     // 2
                warmValueLower = 0.5,      warmValueUpper = 0.7,     // 3
                warmerValueLower = 0.7,    warmerValueUpper = 0.85,  // 4
                warmestValueLower = 0.85,  warmestValueUpper = 1.0,  // 5

                driestValueLower = 0.0,    driestValueUpper  = 0.16, // 0
                drierValueLower = 0.16,    drierValueUpper   = 0.32, // 1
                dryValueLower = 0.32,      dryValueUpper     = 0.48, // 2
                wetValueLower = 0.48,      wetValueUpper     = 0.64, // 3
                wetterValueLower = 0.64,    wetterValueUpper  = 0.8, // 4
                wettestValueLower = 0.8,   wettestValueUpper = 1.0;  // 5

        // Road Plains Forest Jungle Rocky Mountain Ruins Sand Ice River Ocean
        // 0    1      2      3      4     5        6     7    8   9     10
        public static final int
                Road = 0,
            Plains = 1,
            Forest = 2,
            Jungle = 3,
            Rocky = 4,
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
                Ice,      Plains,  Plains, Sand,   Sand,    Sand,     //DRYEST
                Ice,      Plains,  Plains, Plains, Sand,    Sand,     //DRYER
                Ice,      Plains,  Forest, Plains, Plains,  Plains,   //DRY
                Ice,      Forest,  Forest, Forest, Plains,  Plains,   //WET
                Ice,      Forest,  Forest, Forest, Jungle,  Jungle,   //WETTER
                Ice,      Forest,  Forest, Jungle, Jungle,  Jungle,   //WETTEST
                Ice,      Rocky,   Rocky,  Sand,   Sand,    Sand,     //COASTS
                River,    River,   River,  River,  River,   River,    //RIVERS
                River,    River,   River,  River,  River,   River,    //LAKES
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
            GreasedRegion shores = world.landData.copy().not().fringe8way();
            for (int x = 0; x < world.width; x++) {
                for (int y = 0; y < world.height; y++) {
                    final double hot = (world.heatData[x][y] - world.minHeat) * i_hot, moist = world.moistureData[x][y],
                            high = world.heightData[x][y] + NumberTools.bounce(world.heightData[x][y] * 40);
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
                    biomeCodeData[x][y] =
                            heightCode <= 3
                            ? Ocean
                            : hc == 0 || (hc == 1 && moist < 0.54)
                            ? Ice
                            : isLake
                            ? biomeTable[hc + 48]
                            : isRiver
                            ? biomeTable[hc + 42]
                            : shores.contains(x, y)
                            ? biomeTable[hc + 36]
                            : high > 1.35
                            ? Mountain
                            : biomeTable[hc + mc * 6];
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
