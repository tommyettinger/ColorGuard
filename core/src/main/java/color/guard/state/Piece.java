package color.guard.state;

import color.guard.rules.PieceKind;
import squidpony.squidgrid.Direction;

/**
 * Created by Tommy Ettinger on 10/28/2016.
 */
public class Piece {
    public final int kind;
    public final float palette;
    public final PieceKind pieceKind;
    public String name, stats;
    public int facing;
    public int currentHealth;
    public final int paint;

    public Piece() {
        kind = 0;
        palette = 0f;
        name = "Lonesome Joe";
        pieceKind = PieceKind.all.getAt(0);
        currentHealth = 1;
        paint = 0;
        stats = statsString();
        facing = 0;
    }

    public Piece(int kind, Faction faction) {
        this(kind, faction, faction.rng.next(2));
    }

    public Piece(int kind, Faction faction, int facing) {
        this.kind = kind;
        palette = faction.palettes[faction.rng.nextIntHasty(faction.palettes.length)] / 255f;
        this.facing = facing;
        pieceKind = PieceKind.all.getAt(kind);
        currentHealth = pieceKind.wounds;
        paint = faction.paint;
        stats = statsString();
        name = faction.language.word(faction.rng, true, faction.rng.nextIntHasty(3) + 1);
        /*
        if((faction.aggression & 31) < 5)
            name += faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        else
            name += faction.language.word(faction.rng, true, faction.rng.between(1, 4)) + " " + faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        */
    }

    public void resetName(Faction faction) {
        currentHealth = pieceKind.wounds;
        name = faction.language.word(faction.rng, true, faction.rng.nextIntHasty(3) + 1);
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

    public void cityName(Faction faction) {
        currentHealth = pieceKind.wounds;
        name = faction.language.word(faction.rng, true, Math.max(faction.rng.next(1), faction.rng.nextIntHasty(3)) + 1);
        /*
        if((faction.aggression & 3) == 0)
            name += faction.language.word(faction.rng, true, faction.rng.between(1, 3)) + " " + faction.language.word(faction.rng, true, faction.rng.between(1, 4));
        else
            name += faction.language.word(faction.rng, true, faction.rng.between(2, 4));
        */
    }

    public String statsString() {
        return /*(paint > 3 ? ":) " : (paint == 3) ? ":D " : (paint == 2) ? ":| " : ">:( ") + */currentHealth + "/" + pieceKind.wounds; /* pieceKind.abbreviation + ' ' +
                currentHealth + "/" + pieceKind.wounds;
                */
    }

    public static Direction facingDirection(final int facing) {
        switch (facing) {
            case 1:
                return Direction.UP;
            case 0:
                return Direction.LEFT;
            case 3:
                return Direction.DOWN;
            default:
                return Direction.RIGHT;
        }
    }

    public int turnLeft() {

        switch (facing) {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    public int turnRight() {
        switch (facing) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            default:
                return 2;
        }
    }
}
