package color.guard.state;

import squidpony.FakeLanguageGen;
import squidpony.squidmath.*;

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
            {0, 0, 0, 1, 2},
            {0, 0, 1, 1, 1, 1, 1, 2},
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 6, 6, 7},
            {4, 4, 4, 5, 5, 6, 6, 7},
            {4, 4, 5, 5, 5, 5, 6, 7},
            {4, 4, 4, 5, 5, 6, 6, 7, 9, 9, 9, 9, 9, 10, 10},
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 6, 6, 7, 9, 9, 9, 9, 9, 10},
            {9, 9, 9, 9, 10},
            {9, 9, 9, 9, 10, 16, 16, 16, 17, 17},
            {16, 16, 16, 17},
            {19, 19, 19, 19, 20},
            {22, 22, 22, 24},
            {16, 16, 16, 17, 19, 19, 19, 20},
            {16, 16, 16, 17, 22, 22, 22, 24},
            {19, 19, 19, 20, 22, 22, 22, 24},

            {4, 4, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20},
            {0, 0, 1, 2, 4, 4, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24},
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 16, 16, 16, 16, 17, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24},
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 16, 16, 16, 16, 17, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24},
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 16, 16, 16, 16, 17, 19, 19, 19, 19, 19, 20},
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 16, 16, 16, 16, 17, 22, 22, 22, 22, 24},
            {0, 0, 1, 2, 4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 9, 10, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24},
            {4, 4, 4, 5, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24},
            {4, 4, 4, 5, 5, 5, 6, 7, 9, 9, 9, 10, 19, 19, 19, 19, 19, 20, 22, 22, 22, 22, 24},
            {0, 0, 1, 2, 4, 4, 5, 5, 6, 7, 19, 19, 19, 19, 19, 20},
            {0, 0, 1, 2, 4, 4, 5, 5, 6, 7, 22, 22, 22, 22, 24},

            {9, 9, 12, 13, 14, 15},
            {22, 22, 22, 23, 23, 24},
            {9, 9, 12, 13, 14, 15, 22, 22, 23, 23, 24},
            {0, 0, 1, 1, 5, 5, 6, 6, 9, 9, 12, 13, 14, 15},
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
    public Faction(int index, String name, FakeLanguageGen language, GreasedRegion territory)
    {
        rng = new StatefulRNG(CrossHash.Falcon.hash64(name));
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
                if (i == -1) {
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
