package color.guard.state;

import color.guard.rules.PieceKind;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.*;

/**
 * Created by Tommy Ettinger on 10/28/2016.
 */
public class BattleState {
    public K2V1<Coord, String, Piece> pieces;
    public int moverLimit;
    public Arrangement<Coord> moveTargets;
    public StatefulRNG rng;
    private transient GreasedRegion working;
    public BattleState()
    {
        pieces = new K2V1<Coord, String, Piece>(128);
        moverLimit = 0;
        moveTargets = new Arrangement<>(128);
        rng = new StatefulRNG();
        working = new GreasedRegion(128, 128);
    }
    public BattleState(long seed, int[][] map, Faction[] factions)
    {
        rng = new StatefulRNG(seed);
        int pieceCount = PieceKind.kinds.size(), mapWidth = map.length, mapHeight = map[0].length;
        working = new GreasedRegion(mapWidth, mapHeight);
        int[] tempOrdering = new int[pieceCount];
        pieces = new K2V1<Coord, String, Piece>(16 + mapHeight * mapWidth >>> 4);
        moveTargets = new Arrangement<>(16 + mapHeight * mapWidth >>> 4);
        Coord pt;
        for (int x = mapWidth - 1; x >= 0; x--) {
            CELL_WISE:
            for (int y = mapHeight - 1; y >= 0; y--) {
                if(rng.next(4) == 0) {
                    rng.randomOrdering(pieceCount, tempOrdering);
                    for (int i = 0; i < pieceCount; i++) {
                        if(PieceKind.kinds.getAt(tempOrdering[i]).mobilities[map[x][y]] < 6)
                        {
                            Faction fact = Faction.whoOwns(x, y, rng, factions);
                            Piece p = new Piece(tempOrdering[i], fact);
                            while(pieces.containsB(p.name))
                                p.resetName(fact);
                            pt = Coord.get(x, y);
                            pieces.put(pt, p.name, p);
                            moveTargets.add(pt);
                            continue CELL_WISE;
                        }
                    }
                }
            }
        }
        moverLimit = pieces.size();
        Coord city, capital;
        Coord[] cities;
        for (int i = 0; i < factions.length; i++) {
            capital = factions[i].capital;
            working.remake(factions[i].territory).remove(capital);
            cities = working.randomSeparated(0.04, rng, 8);
            for (int j = 0; j < cities.length; j++) {
                city = cities[j];
                Piece p = new Piece(pieceCount, factions[i]);
                p.cityName(factions[i]);
                while(pieces.containsB(p.name))
                    p.resetName(factions[i]);
                if (!pieces.containsA(city)) {
                    pieces.put(city, p.name, p);
                    moveTargets.add(city);
                }
            }
            working.surface().and(new GreasedRegion(map, 9).fringe()).remove(capital);
            cities = working.randomSeparated(0.03, rng, 3);
            for (int j = 0; j < cities.length; j++) {
                city = cities[j];
                Piece p = new Piece(pieceCount + 1, factions[i]);
                p.cityName(factions[i]);
                while(pieces.containsB(p.name))
                    p.resetName(factions[i]);
                if (!pieces.containsA(city)) {
                    pieces.put(city, p.name, p);
                    moveTargets.add(city);
                }
            }
            city = capital;
            Piece p = new Piece(pieceCount + 2, factions[i]);
            p.cityName(factions[i]);
            while(pieces.containsB(p.name))
                p.resetName(factions[i]);
            if(moveTargets.removeInt(city) < 0) --moverLimit;
            pieces.removeA(city);
            pieces.put(city, p.name, p);
            moveTargets.add(city);
        }
    }

    public void advanceTurn()
    {
        int ct = moverLimit, r, pc = pieces.size();
        Direction dir;
        Coord pt, next;
        Piece p;
        for (int i = 0; i < ct; i++) {
            pt = moveTargets.keyAt(i);
            p = pieces.alterAAt(i, pt).getQAt(i);
            r = rng.next(3);
            if(r < 5)
            {
                dir = Piece.facingDirection(p.facing);
                next = pt.translate(dir);
                if(!next.isWithin(working.width, working.height) || pieces.containsA(next) || moveTargets.containsKey(next))
                {
                    if(rng.nextBoolean())
                        p.facing = p.turnLeft();
                    else
                        p.facing = p.turnRight();
                }
                else
                {

                    moveTargets.alter(pt, next);
                }
            }
            else
            {
                if(rng.nextBoolean())
                    p.facing = p.turnLeft();
                else
                    p.facing = p.turnRight();
            }
        }
    }
}
