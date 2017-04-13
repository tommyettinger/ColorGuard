package color.guard.state;

import squidpony.FakeLanguageGen;
import squidpony.squidmath.*;

import java.util.List;

/**
 * A faction in the game world; stores territory, a spoken language, the positions of the
 * capital and cities, some visual info for palettes, and the relationships this Faction has
 * with the other 23 Factions (currently there are 24, hard-coded).
 *
 * Created by Tommy Ettinger on 10/3/2016.
 */
public class Faction {
    public static final int
            PALE_SKIN_BLOND_HAIR = 0,
            PALE_SKIN_RED_HAIR = 1,
            PALE_SKIN_GRAY_HAIR = 2,
            PALE_SKIN_NO_HAIR = 3,
            LIGHT_SKIN_BROWN_HAIR = 4,
            LIGHT_SKIN_BLACK_HAIR = 5,
            LIGHT_SKIN_BLOND_HAIR = 6,
            LIGHT_SKIN_GRAY_HAIR = 7,
            LIGHT_SKIN_NO_HAIR = 8,
            GOLD_SKIN_BLACK_HAIR = 9,
            GOLD_SKIN_GRAY_HAIR = 10,
            GOLD_SKIN_NO_HAIR = 11,
            GOLD_SKIN_SCARLET_HAIR = 12,
            GOLD_SKIN_GREEN_HAIR = 13,
            GOLD_SKIN_BLUE_HAIR = 14,
            GOLD_SKIN_MAGENTA_HAIR = 15,
            OLIVE_SKIN_BLACK_HAIR = 16,
            OLIVE_SKIN_GRAY_HAIR = 17,
            OLIVE_SKIN_NO_HAIR = 18,
            OCHRE_SKIN_BLACK_HAIR = 19,
            OCHRE_SKIN_GRAY_HAIR = 20,
            OCHRE_SKIN_NO_HAIR = 21,
            COFFEE_SKIN_BLACK_HAIR = 22,
            COFFEE_SKIN_BLOND_HAIR = 23,
            COFFEE_SKIN_GRAY_HAIR = 24,
            COFFEE_SKIN_NO_HAIR = 25;
    public static final int[][] diversity = {
            {0, 0, 0, 1, 2}, //0 all pale, mostly blond
            {0, 0, 1, 1, 1, 1, 1, 2}, //1 all pale, mostly redhead
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 6, 6, 7}, //2 some pale, more light, 50/50 brown/black hair
            {4, 4, 4, 5, 5, 6, 6, 7}, //3 all light, more brown hair but a balanced mix
            {4, 4, 5, 5, 5, 5, 6, 7}, //4 all light, more black hair, less blond
            {4, 4, 4, 5, 5, 6, 6, 7, 9, 9, 9, 9, 9, 10, 10}, //5 light and gold, real hair
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 6, 6, 7, 9, 9, 9, 9, 9, 10}, //6 pale, light, and gold, real hair
            {9, 9, 9, 9, 10}, //7 all gold, real hair
            {9, 9, 9, 9, 10, 16, 16, 16, 17, 17}, //8 gold and olive, all hair black/gray
            {16, 16, 16, 17}, //9 all olive, real hair
            {19, 19, 19, 19, 20}, //10 all ochre, real hair but less gray
            {22, 22, 22, 24}, //11 all coffee, black/gray
            {16, 16, 16, 17, 19, 19, 19, 20}, //12 olive and ochre, black/gray
            {16, 16, 16, 17, 22, 22, 22, 24}, //13 olive and coffee, black/gray
            {19, 19, 19, 20, 22, 22, 22, 24}, //14 ochre and coffee, black/gray
            {4, 4, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20}, //15 light and ochre, all real
            {0, 0, 1, 2, 4, 4, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24}, //16 pale, light, ochre, coffee, real hair
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 16, 16, 16, 16, 17, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24}, //17 pale, light, olive, ochre, coffee, real hair
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 16, 16, 16, 16, 17, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24}, //18 pale, light, gold, olive, ochre, coffee, real hair
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 16, 16, 16, 16, 17, 19, 19, 19, 19, 19, 20}, //19 pale, light, gold, olive, ochre, real hair
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 16, 16, 16, 16, 17, 22, 22, 22, 22, 24}, //20 pale, light, gold, olive, coffee, real hair
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24}, //21 pale, light, gold, ochre, coffee, real hair
            {4, 4, 4, 5, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24}, //22 light, ochre, coffee, real hair
            {4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 10, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24}, //23 light, gold, ochre, coffee, real hair
            {0, 0, 1, 2, 4, 4, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20}, //24 pale, light, ochre, real hair
            {0, 0, 1, 2, 4, 4, 5, 5, 6, 7, 22, 22, 22, 22, 24}, //25 pale, light, coffee, real hair
            {9, 9, 12, 13, 14, 15}, //26 all gold, all bright un-real hair colors plus black
            {22, 22, 22, 23, 23, 24}, //27 all coffee, real hair colors plus blond
            {9, 9, 12, 13, 14, 15, 22, 22, 23, 23, 24}, //28 gold: black hair and un-real, coffee: real plus blond
            {0, 0, 1, 1, 5, 5, 6, 6, 9, 9, 12, 13, 14, 15}, //29 pale: blond and red, light: blond and black, gold: black and un-real
            {0, 0, 6, 6, 6, 13, 13, 13}, //30: pale: blond, light: blond, gold: green
            {1, 1, 1, 12, 12}, //31: pale: red, gold: scarlet
            {3, 3, 7, 7, 7, 10, 10, 14, 14, 14, 15, 15, 15} //32 pale, light, and gold: gray, gold only: blue and magenta
    };

    public FakeLanguageGen language;
    public String name;
    /**
     * Index in any factions array; used to refer to this Faction in {@code attitudes}.
     */
    public int index,
    /**
     * Ranges from 0 to 127, and is added to the random attitude this faction has toward each other non-allied faction.
     */
    aggression,
    /**
     * The paint color index, ranging from 0 to 7, that this faction and its permanent allies use.
     */
    paint;
    /**
     * Used to determine levels of hostility between non-allied factions; ranges from 0 to 255, with the minimum value
     * equal to aggression and the attitude toward each faction in an alliance at about the same level (within 15). The
     * actual level of hostility is determined by adding together the attitudes from both sides, generating a result
     * from 0 to 510. Dividing by 64 (right shift by 6) gives the current war status from 0 to 7, from values of 0
     * making two armies willing to fight side-by-side to defend each other, to values of 7 embroiling two armies in a
     * full-blown fight to the death, disregarding lesser threats. A faction can be at most 1 step different in war
     * status between any armies in a permanent alliance (same paint color).
     */
    public int[] attitudes,
    /**
     * Skin and hair colors, plus this army's paint color, found in this army; affects how soldiers are drawn. Common
     * colors will show up more than once, and palettes for a unit are expected to be taken randomly from this array.
     */
    palettes;
    /**
     * The mutable storage of what sections of land are controlled by this faction. Expected to change over time. The
     * capital is drawn from somewhere in this area, typically a decent distance from any borders.
     */
    public GreasedRegion territory;

    public Coord capital;

    public Coord[] cities;

    public StatefulRNG rng;

    public Faction()
    {
    }
    public Faction(int index, String name, List<FakeLanguageGen> languages, GreasedRegion territory)
    {
        this(index, name,
                languages.size() == 1 ? languages.get(0) : languages.get(0).mix(languages.get(1), 0.5),
                territory);
    }
    public Faction(int index, String name, FakeLanguageGen language, GreasedRegion territory)
    {
        rng = new StatefulRNG(CrossHash.Wisp.hash64(name));
        this.index = index;
        this.name = name;
        this.language = language;
        paint = index & 7;
        aggression = rng.next(7);
        this.territory = territory;

        attitudes = new int[24];
        for (int p = 0; p < 8; p++) {
            if (p == paint) {
                attitudes[p] = 0;
                attitudes[p+8] = 0;
                attitudes[p+16] = 0;
            }
            else
            {
                int mod = aggression + rng.nextIntHasty(114);
                attitudes[p] = mod + rng.next(4);
                attitudes[p+8] = mod + rng.next(4);
                attitudes[p+16] = mod + rng.next(4);
            }
        }
        int skinIndex = rng.nextIntHasty(diversity.length);
        palettes = new int[diversity[skinIndex].length];
        for (int i = 0; i < palettes.length; i++) {
            palettes[i] = 8 * diversity[skinIndex][i] + paint;
        }
        if(territory.isEmpty())
        {
            capital = Coord.get(-1, -1);
            cities = new Coord[0];
        }
        else {
            GreasedRegion[] retractions = territory.retractSeries(4);
            for (int i = 3; i >= -1; i--) {
                if (i == -1) { // should never happen; only possible when territory is empty
                    capital = territory.singleRandom(rng);
                }
                else
                {
                    if(retractions[i].isEmpty())
                        continue;
                    capital = retractions[i].singleRandom(rng);
                    break;
                }
            }
            cities = new Coord[8];
            for (int i = 0; i < 8; i++) {
                cities[i] = Coord.get(-1,-1);
            }
        }
    }
    public static Faction whoOwns(int x, int y, RNG random, Faction[] factions)
    {
        for (int i = 0; i < factions.length; i++) {
            if(factions[i].territory.contains(x, y))
                return factions[i];
        }
        return factions[random.nextIntHasty(factions.length)];
    }
}
