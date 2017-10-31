package color.guard.state;

import color.guard.rules.PieceKind;
import com.badlogic.gdx.utils.ObjectSet;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.*;

/**
 * Created by Tommy Ettinger on 10/28/2016.
 */
public class BattleState {
    public OrderedMap<Coord, Piece> pieces;
    public int moverLimit;
    public OrderedSet<Coord> moveTargets;
    public RNG rng;
    public ThrustRNG thrust;
    private transient GreasedRegion working, working2;
    public int[][] map;
    public BattleState()
    {
        pieces = new OrderedMap<>(128);
        moverLimit = 0;
        moveTargets = new OrderedSet<>(128);
        rng = new RNG(thrust = new ThrustRNG());
        map = new int[64][64];
        working = new GreasedRegion(64, 64);
        working2 = new GreasedRegion(64, 64);
    }
    public BattleState(long seed, int[][] map, Faction[] factions)
    {
        this.map = map;
        rng = new RNG(thrust = new ThrustRNG(seed));
        int pieceCount = PieceKind.kinds.size(), mapWidth = map.length, mapHeight = map[0].length;
        working = new GreasedRegion(mapWidth, mapHeight);
        working2 = new GreasedRegion(mapWidth, mapHeight);
        pieces = new OrderedMap<>(16 + mapHeight * mapWidth >>> 4);
        moveTargets = new OrderedSet<>(16 + mapHeight * mapWidth >>> 4);
        Coord pt;
        ObjectSet<String> names = new ObjectSet<>(16 + mapHeight * mapWidth >>> 4);
        int temp;
        //Fighter Jet is 17
        Piece player = new Piece(17, factions[3]);
        pt = rng.nextCoord(mapWidth, mapHeight);
        pieces.put(pt, player);
        names.add(player.name);
        moveTargets.add(pt);
        for (int x = mapWidth - 1; x >= 0; x--) {
            for (int y = mapHeight - 1; y >= 0; y--) {
                if(thrust.next(6) < 5) {
                    temp = rng.nextIntHasty(pieceCount);
                    if((PieceKind.kinds.getAt(temp).permits & 1 << map[x][y]) != 0) {
                        Faction fact = Faction.whoOwns(x, y, rng, factions);
                        Piece p = new Piece(temp, fact);
                        while (names.contains(p.name))
                            p.resetName(fact);
                        pt = Coord.get(x, y);
                        pieces.put(pt, p);
                        names.add(p.name);
                        moveTargets.add(pt);
                    }
                }
            }
        }
        moverLimit = pieces.size();
        Coord city, capital;
        Coord[] cities;
        for (int i = 0; i < factions.length; i++) {
            capital = factions[i].capital;
            working.remake(factions[i].territory).andNot(working2.refill(map, 9, 11)).remove(capital);
            cities = working.randomSeparated(0.04, rng, 10 + rng.next(3));
            for (int j = 0; j < cities.length; j++) {
                city = cities[j];
                Piece p;
                switch (rng.next(4))
                {
                    case 0:
                        p = new Piece("Oil Well", factions[i]);
                        break;
                    case 1:
                    case 2:
                        p = new Piece("Farm", factions[i]);
                        break;
                    case 3:
                        p = new Piece("Laboratory", factions[i]);
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        p = new Piece("Factory", factions[i]);
                        break;
                    case 8:
                    case 9:
                        p = new Piece("Airport", factions[i]);
                        break;
                    case 10:
                        p = new Piece("Hospital", factions[i]);
                        break;
                    default:
                        p = new Piece("City", factions[i]);
                }
                p.cityName(factions[i]);
                while(names.contains(p.name))
                    p.resetName(factions[i]);
                if (!pieces.containsKey(city)) {
                    pieces.put(city, p);
                    names.add(p.name);
                    moveTargets.add(city);
                }
            }
            working.surface().and(working2.refill(map, 9, 11).fringe()).remove(capital).removeSeveral(cities);
            cities = working.randomSeparated(0.03, rng, 2);
            for (int j = 0; j < cities.length; j++) {
                city = cities[j];
                Piece p = new Piece("Dock", factions[i]);
                p.cityName(factions[i]);
                while(names.contains(p.name))
                    p.resetName(factions[i]);
                if (!pieces.containsKey(city)) {
                    pieces.put(city, p);
                    names.add(p.name);
                    moveTargets.add(city);
                }
            }
            city = capital;
            Piece p = new Piece(rng.nextBoolean() ? "Estate" : "Castle", factions[i]);
            p.cityName(factions[i]);
            while(names.contains(p.name))
                p.resetName(factions[i]);
            if(moveTargets.remove(city))
                --moverLimit;
            pieces.remove(city);
            pieces.put(city, p);
            names.add(p.name);
            moveTargets.add(city);
        }
    }

    public void advanceTurn()
    {
        int ct = moverLimit, r;
        Direction dir;
        Coord pt, next;
        Piece p;
        for (int i = 1; i < ct; i++) {
            pt = moveTargets.getAt(i);
            p = pieces.alterAt(i, pt);
            r = thrust.next(3);
            if(r < 5)
            {
                dir = Piece.facingDirection(p.facing);
                next = pt.translateCapped(dir.deltaX, dir.deltaY, map.length, map[0].length);
                if(pieces.containsKey(next) || moveTargets.contains(next)
                        || (p.pieceKind.permits & 1 << map[next.x][next.y]) == 0)
                {
                    if(thrust.nextLong() < 0)
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
                if(thrust.nextLong() < 0)
                    p.facing = p.turnLeft();
                else
                    p.facing = p.turnRight();
            }
        }
    }
}
