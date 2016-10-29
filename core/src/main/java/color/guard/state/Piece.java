package color.guard.state;

/**
 * Created by Tommy Ettinger on 10/28/2016.
 */
public class Piece {
    public final int kind;
    public final float palette;
    public String name;
    public int facing;
    public Piece()
    {
        kind = 0;
        palette = 0f;
        name = "Lonesome Joe";
        facing = 0;
    }

    public Piece(int kind, Faction faction)
    {
        this(kind, faction, faction.rng.next(2));
    }

    public Piece(int kind, Faction faction, int facing)
    {
        this.kind = kind;
        palette = faction.palettes[faction.rng.nextIntHasty(faction.palettes.length)] / 255f;
        this.facing = facing;
        if((faction.aggression & 31) < 5)
            name = faction.language.word(faction.rng, true);
        else
            name = faction.language.word(faction.rng, true) + " " + faction.language.word(faction.rng, true);
    }
    public void resetName(Faction faction)
    {
        if(name.indexOf(' ') < 0)
            name = faction.language.word(faction.rng, true);
        else
            name = faction.language.word(faction.rng, true) + " " + faction.language.word(faction.rng, true);
    }
    public void cityName(Faction faction)
    {
        if((faction.aggression & 3) == 0)
            name = faction.language.word(faction.rng, true, faction.rng.between(1, 3)) + " " + faction.language.word(faction.rng, true, faction.rng.between(2, 5));
        else
            name = faction.language.word(faction.rng, true, faction.rng.between(3, 6));
    }
}
