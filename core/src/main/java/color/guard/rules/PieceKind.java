package color.guard.rules;

import squidpony.Maker;
import squidpony.squidmath.OrderedMap;

/**
 * Created by Tommy Ettinger on 10/6/2016.
 */
public class PieceKind {
    public static final int
    //categories
    FOOT=0,LIGH=1,HEAV=2,AERI=3,NAVA=4,STRU=5;

    public String name, visual, abbreviation, group;
    public int category, weapons;
    public PieceKind()
    {

    }

    public PieceKind(String name, String visual, int weapons)
    {
        this.name = name;
        this.visual = visual;
        this.weapons = weapons;
        this.abbreviation = "NON";
        this.group = "GR";
    }
    public static final OrderedMap<String, PieceKind> kinds = Maker.makeOM(
            "Infantry", new PieceKind("Infantry", "Infantry", 2),
            "Bazooka", new PieceKind("Bazooka", "Infantry_P", 3),
            "Bike", new PieceKind("Bike", "Infantry_S", 2),
            "Rifle Sniper", new PieceKind("Rifle Sniper", "Infantry_T", 3),
            "Missile Sniper", new PieceKind("Missile Sniper", "Infantry_PS", 2),
            "Mortar Sniper", new PieceKind("Mortar Sniper", "Infantry_PT", 1),
            "Light Tank", new PieceKind("Light Tank", "Tank", 3),
            "War Tank", new PieceKind("War Tank", "Tank_P", 3),
            "Heavy Cannon", new PieceKind("Heavy Cannon", "Artillery_P", 2),
            "Light Artillery", new PieceKind("Light Artillery", "Artillery", 1),
            "AA Artillery", new PieceKind("AA Artillery", "Artillery_S", 1),
            "Stealth Artillery", new PieceKind("Stealth Artillery", "Artillery_T", 1),
            "Recon", new PieceKind("Recon", "Recon", 2),
            "AA Gun", new PieceKind("AA Gun", "Tank_S", 2),
            "Flamethrower", new PieceKind("Flamethrower", "Flamethrower", 2),
            "Prop Plane", new PieceKind("Prop Plane", "Plane", 2),
            "Heavy Bomber", new PieceKind("Heavy Bomber", "Plane_P", 1),
            "Fighter Jet", new PieceKind("Fighter Jet", "Plane_S", 2),
            "Supply Truck", new PieceKind("Supply Truck", "Truck", 0),
            "Amphi Transport", new PieceKind("Amphi Transport", "Truck_S", 0),
            "Transport Copter", new PieceKind("Transport Copter", "Copter", 0),
            "Jetpack", new PieceKind("Jetpack", "Infantry_ST", 2),
            "Gunship Copter", new PieceKind("Gunship Copter", "Copter_P", 3),
            "Blitz Copter", new PieceKind("Blitz Copter", "Copter_S", 2),
            "Jammer", new PieceKind("Jammer", "Truck_T", 0),
            "Build Rig", new PieceKind("Build Rig", "Truck_P", 0),
            "Comm Copter", new PieceKind("Comm Copter", "Copter_T", 0),
            "Mud Tank", new PieceKind("Mud Tank", "Tank_T", 3),
            "Submarine", new PieceKind("Submarine", "Boat_T", 1),
            "Stealth Jet", new PieceKind("Stealth Jet", "Plane_T", 2),
            "Patrol Boat", new PieceKind("Patrol Boat", "Boat", 2),
            "Cruiser", new PieceKind("Cruiser", "Boat_S", 1),
            "Battleship", new PieceKind("Battleship", "Boat_P", 2)
    );
}
