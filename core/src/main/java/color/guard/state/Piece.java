package color.guard.state;

import color.guard.rules.PieceKind;

/**
 * Created by Tommy Ettinger on 10/28/2016.
 */
public class Piece {
    public final int kind;
    public final float palette;
    public String name;
    public int facing;
    public int currentHealth;
    public Piece()
    {
        kind = 0;
        palette = 0f;
        name = "Lonesome Joe";
        facing = 0;
        currentHealth = 1;
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
        PieceKind pk = PieceKind.all.getAt(kind);
        currentHealth = pk.wounds;
        name = pk.abbreviation + ' ' + currentHealth + '/' + pk.wounds + '\n' +
                faction.language.word(faction.rng, true, faction.rng.next(1) + Math.max(faction.rng.next(2), 1));
        /*
        if((faction.aggression & 31) < 5)
            name += faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        else
            name += faction.language.word(faction.rng, true, faction.rng.between(1, 4)) + " " + faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        */
    }
    public void resetName(Faction faction)
    {
        PieceKind pk = PieceKind.all.getAt(kind);
        currentHealth = pk.wounds;
        name = pk.abbreviation + ' ' + currentHealth + '/' + pk.wounds + '\n' +
                faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        /*
        if(name.lastIndexOf(' ') < name.lastIndexOf(']'))
            name = "[" + PieceKind.all.getAt(kind).name + "]\n" +
                    faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        else
            name = "[" + PieceKind.all.getAt(kind).name + "]\n" +
                    faction.language.word(faction.rng, true, faction.rng.between(1, 4)) +
                    " " + faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        */
    }
    public void cityName(Faction faction)
    {
        PieceKind pk = PieceKind.all.getAt(kind);
        currentHealth = pk.wounds;
        name = pk.abbreviation + ' ' + currentHealth + '/' + pk.wounds + '\n' +
                faction.language.word(faction.rng, true, Math.max(faction.rng.between(1, 4), faction.rng.between(1, 4)));
        /*
        if((faction.aggression & 3) == 0)
            name += faction.language.word(faction.rng, true, faction.rng.between(1, 3)) + " " + faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        else
            name += faction.language.word(faction.rng, true, faction.rng.between(2, 4));
        */
    }
}
