package color.guard.rules;

import squidpony.Maker;
import squidpony.squidmath.OrderedMap;

/**
 * The listing of pieces we have graphics and stats for. The stats aren't used much yet.
 * Created by Tommy Ettinger on 10/6/2016.
 */
public class PieceKind {
    public static final int
    //categories
    FOOT=0,LIGH=1,HEAV=2,AERI=3,NAVA=4,STRU=5;

    public String name, visual, abbreviation, group, description, action, skill;
    public String[] features, weaknesses, ammo, show;
    public int category, weapons, power, armor, wounds, dodge, speed, cost;
    public int[] mobilities, strengths;
    public PieceKind()
    {

    }

    /**
     * Facility constructor.
     * "City", new PieceKind("City", "City", "CIT", "A medium-sized city that can give troops to its favored army.", 8, 60, false),
     * @param name
     * @param visual
     * @param abbreviation
     * @param description
     * @param armor
     * @param wounds
     */
    public PieceKind(String name, String visual, String abbreviation, String description, int armor, int wounds,
                     boolean aquatic)
    {
        this.name = name;
        this.visual = visual;
        this.abbreviation = abbreviation;
        this.group = "FA";
        this.description = description;
        this.action = "";
        this.skill = "";
        this.features = new String[0];
        this.weaknesses = new String[]{"", ""};
        this.ammo = new String[]{"", ""};
        this.show = new String[]{"", ""};
        this.strengths = new int[]{0, 0};
        this.category = STRU;
        this.weapons = 0;
        this.power = 0;
        this.armor = armor;
        this.wounds = wounds;
        this.dodge = 0;
        this.speed = 0;
        this.cost = 9000;
        this.mobilities = (aquatic)
                ? new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 1, 8,}
                : new int[]{1, 2, 4, 6, 4, 8, 8, 4, 4, 8, 8,};
    }

    public PieceKind(String name, String visual, String group, String abbreviation, int category, String description,
                     String action, String[] features, String[] weaknesses, String skill, int weapons, String[] ammo,
                     String[] show, int[] strengths, int power, int armor, int wounds, int dodge, int speed, int[] mobilities, int cost){
        this.name = name;
        this.visual = visual;
        this.abbreviation = abbreviation;
        this.group = group;
        this.description = description;
        this.action = action;
        this.skill = skill;
        this.features = features;
        this.weaknesses = weaknesses;
        this.ammo = ammo;
        this.show = show;
        this.strengths = strengths;
        this.category = category;
        this.weapons = weapons;
        this.power = power;
        this.armor = armor;
        this.wounds = wounds;
        this.dodge = dodge;
        this.speed = speed;
        this.cost = cost;
        this.mobilities = mobilities;
    }

    /*
    to generate from spreadsheet as TSV, search and replace.
    search string:
    ^([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t(\d)\t([^\t]*)\t(\d)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t([^\t]*)\t\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t(\d*)\t\d*\t\d*\t\d*\t(\d*)\t(\d*)
    replace string:
    //$38\n"$5", new PieceKind\("$5", "$3", "$1", "$2", $6, "$7", "$8", new String\[\]\{"$9","$10","$11",\}, new String\[\]\{"$12","$13"\}, "$14", $4, new String\[\]\{"$15","$16"\}, new String\[\]\{"$17","$19"\}, new int\[\]\{$18, $20\}, $21, $22, $23, $24, $25, new int\[\]\{$26, $27, $28, $29, $30, $31, $32, $33, $34, $35, $36,\}, $37\),
    */
    public static final OrderedMap<String, PieceKind> kinds = Maker.makeOM(
//0
            "Infantry", new PieceKind("Infantry", "Infantry", "SL", "INFY", FOOT, "Cheap, weak, and fragile, but can occupy enemy facilities.", "", new String[]{"Occupy","Reckless","Anti-Troop",}, new String[]{"Power",""}, "Full Auto", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{1, 0}, 9, 3, 10, 15, 7, new int[]{2, 2, 2, 3, 2, 4, 3, 2, 3, 4, 8,}, 68),
//1
            "Bazooka", new PieceKind("Bazooka", "Infantry_P", "SL", "BZKA", FOOT, "A cost-effective way to fight tanks and artillery.", "", new String[]{"Occupy","Reckless","Anti-Heavy",}, new String[]{"Accuracy",""}, "Armor Buster", 3, new String[]{"Pistol","Rocket"}, new String[]{"Handgun","Rocket"}, new int[]{1, 3}, 11, 4, 10, 0, 7, new int[]{2, 2, 2, 3, 2, 4, 3, 2, 3, 4, 8,}, 85),
//2
            "Bike", new PieceKind("Bike", "Infantry_S", "SL", "BIKE", FOOT, "Drastically faster than the Infantry on land, but no stronger.", "", new String[]{"Occupy","Reckless","Anti-Troop",}, new String[]{"Power",""}, "Rush", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{2, 0}, 9, 2, 10, 10, 13, new int[]{1, 2, 2, 4, 3, 6, 3, 3, 3, 8, 8,}, 85),
//3
            "Rifle Sniper", new PieceKind("Rifle Sniper", "Infantry_T", "SN", "RFSN", FOOT, "Useful for taking out other foot soldiers at medium range.", "Indirect (1,3)", new String[]{"Occupy","Stealthy","Retaliate",}, new String[]{"Defense","Speed"}, "Sneak", 3, new String[]{"Pistol","Longarm"}, new String[]{"Handgun","Handgun"}, new int[]{1, 1}, 12, 1, 10, 30, 8, new int[]{2, 2, 2, 2, 2, 3, 2, 2, 2, 4, 6,}, 92),
//4
            "Missile Sniper", new PieceKind("Missile Sniper", "Infantry_PS", "SN", "MISN", FOOT, "The cheapest way to bring down a plane or copter.", "Indirect (2,4)", new String[]{"Occupy","Anti-Aerial","",}, new String[]{"Choice",""}, "Long Shot", 1, new String[]{"","Missile"}, new String[]{"Arc_Missile",""}, new int[]{0, 2}, 14, 2, 10, 30, 9, new int[]{2, 2, 3, 4, 2, 4, 3, 2, 3, 6, 8,}, 115),
//5
            "Mortar Sniper", new PieceKind("Mortar Sniper", "Infantry_PT", "SN", "MRSN", FOOT, "Able to play the role of artillery, pinning foes, for less cost.", "Indirect (4,5)", new String[]{"Occupy","Pin","Anti-Structure",}, new String[]{"Accuracy","Speed"}, "Hunker Down", 1, new String[]{"","Cannon"}, new String[]{"","Long_Cannon"}, new int[]{0, 2}, 15, 4, 10, 10, 6, new int[]{2, 2, 2, 3, 3, 6, 3, 3, 3, 8, 8,}, 115),
//6
            "Light Tank", new PieceKind("Light Tank", "Tank", "TN", "LTNK", HEAV, "A basic sort of tank that can guard territory well.", "", new String[]{"Guard","Anti-Light","Anti-Heavy",}, new String[]{"Rotation",""}, "Rush", 3, new String[]{"Cannon","Assault"}, new String[]{"Cannon","Machine_Gun"}, new int[]{2, 1}, 17, 9, 20, 6, 10, new int[]{2, 2, 2, 3, 3, 8, 2, 3, 4, 8, 8,}, 220),
//7
            "War Tank", new PieceKind("War Tank", "Tank_P", "TN", "WTNK", HEAV, "An extremely tough tank that gives up speed for strength.", "", new String[]{"Guard","Anti-Heavy","Anti-Structure",}, new String[]{"Rotation",""}, "Rampage", 3, new String[]{"Cannon","Assault"}, new String[]{"Cannon","Machine_Gun"}, new int[]{4, 2}, 25, 13, 27, 0, 7, new int[]{2, 2, 2, 3, 3, 8, 2, 3, 3, 8, 8,}, 275),
//8
            "Heavy Cannon", new PieceKind("Heavy Cannon", "Artillery_P", "TN", "HCNN", HEAV, "A mix of tank and artillery that can counter indirect fire.", "Indirect (1,3)", new String[]{"Guard","Pin","Retaliate",}, new String[]{"Rotation","Speed"}, "Hunker Down", 2, new String[]{"Cannon",""}, new String[]{"Cannon",""}, new int[]{4, 0}, 30, 10, 24, 0, 7, new int[]{2, 2, 3, 4, 3, 8, 2, 3, 3, 8, 8,}, 275),
//9
            "Light Artillery", new PieceKind("Light Artillery", "Artillery", "AR", "LART", HEAV, "Inaccurate, but long-ranged and able to pin foes it misses.", "Indirect (3,5)", new String[]{"Pin","Anti-Structure","",}, new String[]{"Accuracy",""}, "Long Shot", 1, new String[]{"","Cannon"}, new String[]{"","Long_Cannon"}, new int[]{0, 3}, 16, 4, 14, 0, 11, new int[]{1, 2, 3, 4, 3, 8, 2, 3, 4, 8, 8,}, 180),
//10
            "AA Artillery", new PieceKind("AA Artillery", "Artillery_S", "AR", "AART", HEAV, "Able to pin flying units it misses, and obliterate ones it hits.", "Indirect (4,5)", new String[]{"Pin","Anti-Aerial","",}, new String[]{"Choice",""}, "Spotter", 1, new String[]{"","Missile"}, new String[]{"","Arc_Missile"}, new int[]{0, 3}, 17, 7, 19, 0, 15, new int[]{1, 2, 3, 4, 3, 8, 2, 3, 4, 8, 8,}, 225),
//11
            "Stealth Artillery", new PieceKind("Stealth Artillery", "Artillery_T", "AR", "SART", LIGH, "A tricky artillery that fires missiles from hiding.", "Indirect (4,5)", new String[]{"Pin","Stealthy","",}, new String[]{"Defense",""}, "Sneak", 1, new String[]{"","Missile"}, new String[]{"","Arc_Missile"}, new int[]{0, 4}, 28, 3, 16, 50, 12, new int[]{1, 2, 2, 2, 2, 4, 2, 2, 3, 4, 8,}, 215),
//12
            "Recon", new PieceKind("Recon", "Recon", "LV", "RECN", LIGH, "A quick unit that can intercept foot soldiers in rough terrain.", "", new String[]{"Seek","Coordinate","Anti-Troop",}, new String[]{"Power",""}, "Spotter", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{1, 0}, 10, 4, 15, 17, 14, new int[]{1, 2, 2, 2, 2, 4, 2, 2, 2, 8, 8,}, 148),
//13
            "AA Gun", new PieceKind("AA Gun", "Tank_S", "LV", "AAGN", LIGH, "Tank-like against flying and foot units, defending an area.", "", new String[]{"Guard","Anti-Aerial","Anti-Troop",}, new String[]{"Choice",""}, "Full Auto", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{2, 0}, 17, 4, 16, 14, 16, new int[]{2, 2, 2, 3, 3, 8, 2, 3, 4, 8, 8,}, 185),
//14
            "Flamethrower", new PieceKind("Flamethrower", "Flamethrower", "LV", "FLMT", LIGH, "Drastically more powerful against armored targets.", "", new String[]{"Anti-Structure","Anti-Heavy","Permeate",}, new String[]{"Defense",""}, "Rampage", 2, new String[]{"Chemical",""}, new String[]{"Bomb",""}, new int[]{1, 0}, 20, 4, 17, 10, 12, new int[]{1, 2, 2, 2, 3, 8, 2, 3, 3, 8, 8,}, 185),
//15
            "Prop Plane", new PieceKind("Prop Plane", "Plane", "PL", "PPLN", AERI, "Can move, attack, and keep moving, but weak to AA units.", "", new String[]{"Mobile","Anti-Light","Coordinate",}, new String[]{"Defense",""}, "Full Auto", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{2, 0}, 15, 1, 11, 33, 15, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 224),
//16
            "Heavy Bomber", new PieceKind("Heavy Bomber", "Plane_P", "PL", "HBMR", AERI, "Can move, attack all adjacent units, and move again.", "Blast", new String[]{"Mobile","Anti-Structure","Permeate",}, new String[]{"Accuracy","Defense"}, "Rampage", 1, new String[]{"","Chemical"}, new String[]{"","Bomb"}, new int[]{0, 4}, 32, 1, 12, 18, 13, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 285),
//17
            "Fighter Jet", new PieceKind("Fighter Jet", "Plane_S", "PL", "FIJT", AERI, "Mostly strong against other flying units, but very much so.", "", new String[]{"Mobile","Anti-Aerial","Seek",}, new String[]{"Defense",""}, "Rush", 2, new String[]{"Rocket",""}, new String[]{"Rocket",""}, new int[]{1, 0}, 28, 1, 10, 40, 18, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 285),
//18
            "Supply Truck", new PieceKind("Supply Truck", "Truck", "TR", "SPTR", LIGH, "A basic transport that can bring Super Ammo to fighters.", "Supply", new String[]{"Carry","Coordinate","",}, new String[]{"Noncombat",""}, "", 0, new String[]{"",""}, new String[]{"",""}, new int[]{0, 0}, 0, 5, 18, 10, 11, new int[]{1, 2, 3, 4, 3, 6, 3, 3, 4, 8, 8,}, 112),
//19
            "Amphi Transport", new PieceKind("Amphi Transport", "Truck_S", "TR", "AMTR", HEAV, "A different kind of transport; amphibious, with better armor.", "Supply", new String[]{"Carry","Coordinate","",}, new String[]{"Noncombat",""}, "", 0, new String[]{"",""}, new String[]{"",""}, new int[]{0, 0}, 0, 8, 18, 0, 10, new int[]{2, 2, 3, 3, 3, 8, 3, 3, 3, 2, 2,}, 140),
//20
            "Transport Copter", new PieceKind("Transport Copter", "Copter", "TR", "TRCP", AERI, "A flying kind of transport; very fast, but fragile.", "Restore", new String[]{"Carry","Coordinate","",}, new String[]{"Noncombat",""}, "", 0, new String[]{"",""}, new String[]{"",""}, new int[]{0, 0}, 0, 1, 8, 15, 12, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 140),
//21
            "Jetpack", new PieceKind("Jetpack", "Infantry_ST", "LF", "JTPK", AERI, "Able to occupy facilities, fly, and mobile-attack.", "", new String[]{"Occupy","Mobile","Anti-Aerial",}, new String[]{"Defense",""}, "Full Auto", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{2, 0}, 11, 1, 8, 35, 12, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 188),
//22
            "Gunship Copter", new PieceKind("Gunship Copter", "Copter_P", "LF", "GNCP", AERI, "A slow-flying copter that can defend like a tank.", "", new String[]{"Guard","Anti-Heavy","",}, new String[]{"",""}, "Armor Buster", 3, new String[]{"Assault","Rocket"}, new String[]{"Machine_Gun","Rocket"}, new int[]{2, 2}, 20, 1, 14, 30, 10, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 235),
//23
            "Blitz Copter", new PieceKind("Blitz Copter", "Copter_S", "LF", "BLCP", AERI, "A very fast copter that gives up some offensive strength.", "", new String[]{"Mobile","Anti-Light","Anti-Aerial",}, new String[]{"Power",""}, "Rush", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{2, 0}, 17, 1, 10, 45, 16, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 235),
//24
            "Jammer", new PieceKind("Jammer", "Truck_T", "TV", "JAMR", LIGH, "Can jam enemy signals and generate Super Ammo.", "Hack", new String[]{"Disrupt","Generate","",}, new String[]{"Noncombat",""}, "", 0, new String[]{"",""}, new String[]{"",""}, new int[]{0, 0}, 0, 5, 17, 30, 13, new int[]{1, 2, 3, 4, 3, 6, 3, 3, 4, 8, 8,}, 120),
//25
            "Build Rig", new PieceKind("Build Rig", "Truck_P", "TV", "BLDR", HEAV, "Can repair vehicles and facilities and generate Super Ammo.", "Restore", new String[]{"Carry","Generate","",}, new String[]{"Noncombat",""}, "", 0, new String[]{"",""}, new String[]{"",""}, new int[]{0, 0}, 0, 9, 21, 0, 9, new int[]{2, 2, 2, 3, 2, 4, 2, 2, 3, 8, 8,}, 150),
//26
            "Comm Copter", new PieceKind("Comm Copter", "Copter_T", "TV", "CMCP", AERI, "Can coordinate allies and generate Super Ammo.", "Hack", new String[]{"Coordinate","Generate","",}, new String[]{"Noncombat",""}, "", 0, new String[]{"",""}, new String[]{"",""}, new int[]{0, 0}, 0, 1, 8, 45, 13, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 150),
//27
            "Mud Tank", new PieceKind("Mud Tank", "Tank_T", "SH", "MTNK", HEAV, "A versatile tank that can stealthily defend almost any terrain.", "", new String[]{"Stealthy","Guard","",}, new String[]{"",""}, "Sneak", 3, new String[]{"Longarm","Cannon"}, new String[]{"Handgun","Cannon"}, new int[]{1, 2}, 19, 11, 25, 20, 12, new int[]{2, 2, 2, 2, 3, 6, 2, 3, 4, 3, 4,}, 264),
//28
            "Submarine", new PieceKind("Submarine", "Boat_T", "SH", "SBMR", NAVA, "A sneaky long-range naval unit that can devastate facilities.", "Indirect (4,5)", new String[]{"Vanish","Anti-Structure","Torpedo",}, new String[]{"Rotation","Defense"}, "Sneak", 1, new String[]{"","Missile"}, new String[]{"Torpedo","Arc_Missile"}, new int[]{1, 4}, 34, 10, 8, 0, 10, new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 4, 1,}, 330),
//29
            "Stealth Jet", new PieceKind("Stealth Jet", "Plane_T", "SH", "STJT", AERI, "A plane that can sneak up, blast enemies, and zoom away.", "", new String[]{"Vanish","Mobile","Anti-Aerial",}, new String[]{"Defense",""}, "Sneak", 2, new String[]{"Rocket",""}, new String[]{"Rocket",""}, new int[]{2, 0}, 40, 1, 10, 60, 16, new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,}, 330),
//30
            "Patrol Boat", new PieceKind("Patrol Boat", "Boat", "BT", "PTBT", NAVA, "A boat that can attack all kinds of unit, but isn't too strong.", "Indirect (1,3)", new String[]{"Retaliate","Seek","Anti-Aerial",}, new String[]{"Rotation","Power"}, "Spotter", 2, new String[]{"Assault",""}, new String[]{"Machine_Gun",""}, new int[]{2, 0}, 17, 5, 18, 0, 9, new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 1, 1,}, 272),
//31
            "Cruiser", new PieceKind("Cruiser", "Boat_S", "BT", "CRSR", NAVA, "A fast boat that can devastate aerial and naval units.", "Indirect (3,4)", new String[]{"Mobile","Anti-Aerial","Torpedo",}, new String[]{"Rotation","Choice"}, "Rush", 1, new String[]{"","Missile"}, new String[]{"Torpedo","Arc_Missile"}, new int[]{1, 3}, 26, 7, 19, 0, 15, new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 2, 1,}, 340),
//32
            "Battleship", new PieceKind("Battleship", "Boat_P", "BT", "BTSP", NAVA, "A long-range boat with a set of huge anti-armor cannons.", "Indirect (1,4)", new String[]{"Retaliate","Anti-Heavy","Anti-Structure",}, new String[]{"Rotation","Speed"}, "Long Shot", 2, new String[]{"Cannon",""}, new String[]{"Long_Cannon",""}, new int[]{4, 0}, 30, 7, 23, 0, 7, new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 2, 1,}, 340)
    ),
    facilities = Maker.makeOM(
            //33
            "City", new PieceKind("City", "City", "CITY", "A medium-sized city that can give troops to its favored army.", 8, 60, false),
            //34
            "Dock", new PieceKind("Dock", "Dock", "DOCK", "A port town that can sometimes give boats to its favored army.", 8, 60, true),
            //35
            "Castle", new PieceKind("Castle", "Castle", "CASL", "A vital fortress that serves as a command center for an army.", 12, 100, false)
    ),
    all = new OrderedMap<String, PieceKind>(kinds);
    static {
        all.putAll(facilities);
    }
}
