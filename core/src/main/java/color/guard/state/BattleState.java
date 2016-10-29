package color.guard.state;

import color.guard.rules.PieceKind;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.K2V1;
import squidpony.squidmath.StatefulRNG;

/**
 * Created by Tommy Ettinger on 10/28/2016.
 */
public class BattleState {
    public K2V1<Coord, String, Piece> pieces;
    public StatefulRNG rng;
    public BattleState()
    {
        pieces = new K2V1<Coord, String, Piece>(128);
        rng = new StatefulRNG();
    }
    public BattleState(long seed, int[][] map, Faction[] factions)
    {
        rng = new StatefulRNG(seed);
        int pieceCount = PieceKind.kinds.size(), mapWidth = map.length, mapHeight = map[0].length;
        int[] tempOrdering = new int[pieceCount];
        pieces = new K2V1<Coord, String, Piece>(16 + mapHeight * mapWidth >>> 4);
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
                            pieces.put(Coord.get(x, y), p.name, p);
                            continue CELL_WISE;
                        }
                    }
                }
            }
        }
        Coord city;
        Coord[] cities;
        GreasedRegion tempRegion = new GreasedRegion(mapWidth, mapHeight);
        for (int i = 0; i < factions.length; i++) {
            tempRegion.remake(factions[i].territory);
            cities = tempRegion.randomSeparated(0.04, rng, 8);
            for (int j = 0; j < cities.length; j++) {
                city = cities[j];
                Piece p = new Piece(pieceCount, factions[i]);
                p.cityName(factions[i]);
                pieces.put(city, p.name, p);
            }
            tempRegion.surface().and(new GreasedRegion(map, 9).fringe());
            cities = tempRegion.randomSeparated(0.03, rng, 3);
            for (int j = 0; j < cities.length; j++) {
                city = cities[j];
                Piece p = new Piece(pieceCount + 1, factions[i]);
                p.cityName(factions[i]);
                pieces.put(city, p.name, p);
            }
            city = factions[i].capital;
            Piece p = new Piece(pieceCount + 2, factions[i]);
            p.cityName(factions[i]);
            pieces.put(city, p.name, p);
        }
    }
}
