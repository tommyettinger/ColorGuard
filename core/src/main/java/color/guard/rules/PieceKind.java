package color.guard.rules;

import squidpony.Maker;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;

import java.util.Arrays;

/**
 * The listing of pieces we have graphics and stats for. The stats aren't used much yet.
 * Created by Tommy Ettinger on 10/6/2016.
 */
public class PieceKind {
    public static final int
    //categories
    TROP=0,LIGH=1,HEAV=2,AERI=3,NAVA=4,STRU=5,
    TOU=0, EVA=1, AMR=2, PRC=3, STR=4, HLP=5, REF=6, MOV=7, ASP=8;
    public static final OrderedMap<String, String[]> motionFeatures = Maker.makeOM(
            //0
            "Foot", new String[] {"Traverse,", "Hike,", "Ford,", "Responsive,", "Irreparable"},
            //1
            "Wheels", new String[] {"Road-Home,", "Responsive,", "Carrier"},
            //2
            "Treads", new String[] {"Traverse,", "Reliable,", "Shielded"},
            //3
            "Flying", new String[] {"Fly,", "Reliable"},
            //4
            "Soaring", new String[] {"Fly,", "Unassailable,",  "Irreparable"},
            //5
            "Naval", new String[] {"Unassailable,", "Reliable,", "Carrier,", "Shielded,", "Aquatic"},
            //6
            "Immobile", new String[]{"Shielded"}
            );

    public String name, visual, abbreviation, group, description, motion;
    public String[] ammo, show;
    public int category, weapons, wounds, permits;
    public int[] stats, minimumRanges, maximumRanges, powers, mobilities, shownStrengths;
    public OrderedSet<String> features;
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
        this.description = description;
        category = STRU;
        group = "FA";
        motion = "Immobile";
        stats = new int[]{wounds, 0, armor, 0, 0, 0, 0, 0, 0};
        this.wounds = wounds;
        this.features = new OrderedSet<>(motionFeatures.get(motion));
        weapons = 0;
        ammo = new String[]{"", ""};
        minimumRanges = new int[]{0, 0};
        maximumRanges = new int[]{0, 0};
        powers = new int[]{0, 0};
        show = new String[]{"", ""};
        shownStrengths = new int[]{0, 0};
        // Road Plains Forest Jungle Rocky Mountain Ruins Sand Ice River Ocean
        // 0    1      2      3      4     5        6     7    8   9     10
        mobilities = (aquatic)
                ? new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 1, 8,}
                : new int[]{1, 2, 4, 6, 4, 8, 8, 4, 4, 8, 8,};
        permits = (aquatic)
                ? 512
                : 1 | 2 | 4 | 16 | 128 | 256;
    }

    public PieceKind(String name, String visual, String group, String abbreviation, int category, String description,
                     String motion, int[] stats, String[] features, int weapons,
                     String[] ammo, int[] minimumRanges, int[] maximumRanges, int[] powers,
                     String[] show, int[] shownStrengths)
    {
        this.name = name;
        this.visual = visual;
        this.group = group;
        this.abbreviation = abbreviation;
        this.category = category;
        this.description = description;
        this.motion = motion;
        this.stats = stats;
        this.wounds = 5 + 4 * stats[PieceKind.TOU];
        this.features = new OrderedSet<>(motionFeatures.get(motion));
        this.features.addAll(features);
        this.weapons = weapons;
        this.ammo = ammo;
        this.minimumRanges = minimumRanges;
        this.maximumRanges = maximumRanges;
        this.powers = powers;
        this.show = show;
        this.shownStrengths = shownStrengths;
        //      Road Plains Forest Jungle Rocky Mountain Ruins Sand Ice River Ocean
        //      0    1      2      3      4     5        6     7    8   9     10
        mobilities = new int[]{
                1,   1,     2,     3,     3,    4,       3,    1,   1,  4,    4
        };
        permits = 1 | 2 | 4 | 8 | 16 | 64 | 128 | 256;
        if(this.features.contains("Fly"))
        {
            Arrays.fill(mobilities, 1);
            permits = 2047;
        }
        else if(this.features.contains("Aquatic"))
        {
            Arrays.fill(mobilities,9);
            mobilities[9] = 1;
            mobilities[10] = 1;
            permits = 512 | 1024;
        }
        else
        {
            if(this.features.contains("Traverse"))
            {
                mobilities[2]--;
                mobilities[3]--;
                mobilities[4]--;
                mobilities[5]--;
                mobilities[6]--;
            }
            if(this.features.contains("Hike"))
            {
                mobilities[4]--;
                mobilities[5]--;
                permits |= 48;
            }
            if(this.features.contains("Ford"))
            {
                mobilities[9] -= 2;
                permits |= 512;
            }
            if(this.features.contains("Float"))
            {
                mobilities[10] -= 2;
                permits |= 1024;
            }
        }
    }

    /*
    to generate from spreadsheet as TSV, search and replace.
    search string:
    ^([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t(\d)\t(\d)\t(\d)\t(\d)\t(\d)\t(\d)\t(\d)\t(\d)\t\d\t(\d)\t\d+\t([^\t]+)\t(\d)\t(\d)\t(\d)\t([^\t]*)\t(\d?)\t(\d?)\t(\d?)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t([^\t]+)\t(\d)\t([^\t]*)\t(\d)\t([^\t]*)\t(\d)\t(\d+)$
    replace string:
    //$33\n\"$3\", new PieceKind\(\"$3\", \"$27\", \"$1\", \"$2\", $4, \"$5\", \"$6\", new int\[\]\{$7, $8, $9, $10, $11, $12, $13, $14, $15\}, new String\[\]\{\"$24\", \"$25\", \"$26\"\}, $28, new String\[\]\{\"$16\", \"$20\"\}, new int\[\]\{$17, $21\}, new int\[\]\{$18, $22\}, new int\[\]\{$19, $23\}, new String\[\]\{\"$29\", \"$31\"\}, new int\[\]\{$30, $32\}\),
    */
    public static final OrderedMap<String, PieceKind> kinds = Maker.makeOM(
//0
            "Infantry", new PieceKind("Infantry", "Infantry", "SL", "INFY", TROP, "A brave foot soldier who gets stronger in a team.", "Foot", new int[]{2, 6, 2, 4, 1, 7, 8, 4, 6}, new String[]{"Swarm", "Lucky", "Troop-Focus"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{2, 2}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{1, 1}),
//1
            "Bazooka", new PieceKind("Bazooka", "Infantry_P", "SL", "BZKA", TROP, "A tougher foot soldier who can blast vehicles.", "Foot", new int[]{4, 4, 4, 3, 5, 7, 5, 4, 5}, new String[]{"Swarm", "Heavy-Focus", "Light-Focus"}, 3, new String[]{"Rocket", "Pistol"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{4, 0}, new String[]{"Rocket", "Handgun"}, new int[]{3, 1}),
//2
            "Bike", new PieceKind("Bike", "Infantry_S", "SL", "BIKE", TROP, "A soldier who moves quickly on a motorcycle and can gather loot.", "Wheels", new int[]{2, 5, 2, 4, 1, 7, 7, 6, 5}, new String[]{"Swarm", "Gather", "Traverse"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{2, 2}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{2, 2}),
//3
            "Rifle Sniper", new PieceKind("Rifle Sniper", "Infantry_T", "SN", "RFSN", TROP, "A spy who can shoot at medium range and counter as well.", "Foot", new int[]{1, 9, 1, 8, 3, 2, 6, 3, 5}, new String[]{"Stealthy", "Troop-Focus", "Seek"}, 3, new String[]{"Longarm", "Pistol"}, new int[]{2, 1}, new int[]{3, 1}, new int[]{4, 0}, new String[]{"Handgun", "Handgun"}, new int[]{1, 1}),
//4
            "Missile Sniper", new PieceKind("Missile Sniper", "Infantry_PS", "SN", "MISN", TROP, "A specialized sniper who is highly skilled at taking down aircraft.", "Foot", new int[]{3, 4, 2, 7, 5, 5, 0, 4, 4}, new String[]{"Pin", "Aerial-Focus", "Mountain-Strike"}, 1, new String[]{"Missile", ""}, new int[]{2, 0}, new int[]{6, 0}, new int[]{4, 0}, new String[]{"Arc_Missile", ""}, new int[]{2, 0}),
//5
            "Mortar Sniper", new PieceKind("Mortar Sniper", "Infantry_PT", "SN", "MRSN", TROP, "A slow but potent sniper who can pound stationary targets.", "Foot", new int[]{5, 1, 4, 2, 9, 7, 0, 3, 4}, new String[]{"Pin", "Heavy-Focus", "Structure-Focus"}, 1, new String[]{"Cannon", ""}, new int[]{3, 0}, new int[]{7, 0}, new int[]{4, 0}, new String[]{"Long_Cannon", ""}, new int[]{2, 0}),
//6
            "Light Tank", new PieceKind("Light Tank", "Tank", "TN", "LTNK", HEAV, "A basic sort of tank with a potent counterattack.", "Treads", new int[]{7, 1, 6, 2, 6, 3, 3, 6, 5}, new String[]{"Vengeful", "Juggernaut", "Light-Focus"}, 3, new String[]{"Cannon", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{2, 2}, new String[]{"Cannon", "Machine_Gun"}, new int[]{2, 1}),
//7
            "War Tank", new PieceKind("War Tank", "Tank_P", "TN", "WTNK", HEAV, "Overkill on treads; has an incredible cannon and a good counter.", "Treads", new int[]{9, 1, 9, 2, 9, 1, 1, 5, 4}, new String[]{"Vengeful", "Juggernaut", "Heavy-Focus"}, 3, new String[]{"Cannon", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{3, 1}, new String[]{"Cannon", "Machine_Gun"}, new int[]{4, 1}),
//8
            "Heavy Cannon", new PieceKind("Heavy Cannon", "Artillery_P", "TN", "HCNN", HEAV, "A mix of tank and artillery that can counter indirect fire.", "Treads", new int[]{8, 1, 6, 1, 9, 4, 1, 3, 4}, new String[]{"Vengeful", "Juggernaut", "Pin"}, 2, new String[]{"Cannon", "Cannon"}, new int[]{1, 1}, new int[]{4, 4}, new int[]{1, 3}, new String[]{"Cannon", "Cannon"}, new int[]{4, 4}),
//9
            "Light Artillery", new PieceKind("Light Artillery", "Artillery", "AR", "LART", HEAV, "Inaccurate, but long-ranged and able to pin foes it misses.", "Treads", new int[]{6, 3, 4, 1, 7, 7, 0, 4, 4}, new String[]{"Pin", "Light-Focus", "Plains-Home"}, 1, new String[]{"Cannon", ""}, new int[]{2, 0}, new int[]{5, 0}, new int[]{4, 0}, new String[]{"Long_Cannon", ""}, new int[]{3, 0}),
//10
            "AA Artillery", new PieceKind("AA Artillery", "Artillery_S", "AR", "AART", HEAV, "Able to pin flying units it misses, and obliterate ones it hits.", "Treads", new int[]{5, 3, 2, 7, 5, 6, 0, 4, 3}, new String[]{"Pin", "Aerial-Focus", "Sand-Home"}, 1, new String[]{"Missile", ""}, new int[]{3, 0}, new int[]{7, 0}, new int[]{4, 0}, new String[]{"Arc_Missile", ""}, new int[]{3, 0}),
//11
            "Stealth Artillery", new PieceKind("Stealth Artillery", "Artillery_T", "AR", "SART", LIGH, "A tricky artillery that fires missiles from difficult terrain.", "Wheels", new int[]{4, 7, 3, 5, 8, 3, 0, 4, 3}, new String[]{"Pin", "Stealthy", "Traverse"}, 1, new String[]{"Missile", ""}, new int[]{3, 0}, new int[]{6, 0}, new int[]{4, 0}, new String[]{"Arc_Missile", ""}, new int[]{4, 0}),
//12
            "Recon", new PieceKind("Recon", "Recon", "LV", "RECN", LIGH, "A quick unit that can intercept foot soldiers in rough terrain.", "Wheels", new int[]{5, 4, 3, 8, 1, 5, 4, 6, 4}, new String[]{"Gather", "Troop-Focus", "Traverse"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{3, 1}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{1, 1}),
//13
            "AA Gun", new PieceKind("AA Gun", "Tank_S", "LV", "AAGN", LIGH, "A well-rounded defender; strong against flying and foot units.", "Treads", new int[]{6, 1, 5, 6, 5, 5, 6, 5, 3}, new String[]{"Pin", "Troop-Focus", "Aerial-Focus"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{2, 2}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{2, 2}),
//14
            "Flamethrower", new PieceKind("Flamethrower", "Flamethrower", "LV", "FLMT", LIGH, "Drastically more powerful against armored targets.", "Treads", new int[]{7, 2, 6, 7, 8, 2, 2, 5, 3}, new String[]{"Incinerator", "Light-Focus", "Heavy-Focus"}, 2, new String[]{"Fire", "Fire"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{4, 0}, new String[]{"Bomb", "Bomb"}, new int[]{1, 1}),
//15
            "Legacy Plane", new PieceKind("Legacy Plane", "Plane", "PL", "LPLN", AERI, "A scouting plane that can advise allies and fight other aircraft.", "Flying", new int[]{1, 7, 1, 5, 3, 8, 7, 7, 2}, new String[]{"Benefactor", "Aerial-Focus", "Light-Focus"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{3, 1}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{2, 2}),
//16
            "Heavy Bomber", new PieceKind("Heavy Bomber", "Plane_P", "PL", "HBMR", AERI, "A power hitter that burns up everything nearby with its bombs.", "Flying", new int[]{4, 6, 2, 6, 9, 7, 0, 7, 1}, new String[]{"Incinerator", "Structure-Focus", "Heavy-Focus"}, 1, new String[]{"Fire", ""}, new int[]{1, 0}, new int[]{1, 0}, new int[]{4, 0}, new String[]{"Bomb", ""}, new int[]{4, 0}),
//17
            "Fighter Jet", new PieceKind("Fighter Jet", "Plane_S", "PL", "FIJT", AERI, "Mostly strong against other flying units, but very much so.", "Soaring", new int[]{1, 8, 1, 8, 5, 5, 4, 8, 1}, new String[]{"Responsive", "Aerial-Focus", "Pin"}, 2, new String[]{"Rocket", "Rocket"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{3, 1}, new String[]{"Rocket", "Rocket"}, new int[]{1, 1}),
//18
            "Supply Truck", new PieceKind("Supply Truck", "Truck", "TR", "SPTR", LIGH, "A loot-gathering transport that does well in groups.", "Wheels", new int[]{4, 2, 3, 3, 4, 8, 0, 6, 6}, new String[]{"Gather", "Benefactor", "Swarm"}, 0, new String[]{"Supply", ""}, new int[]{1, 0}, new int[]{2, 0}, new int[]{4, 0}, new String[]{"", ""}, new int[]{0, 0}),
//19
            "Amphi Transport", new PieceKind("Amphi Transport", "Truck_S", "TR", "AMTR", HEAV, "A different kind of transport; amphibious, with better armor.", "Treads", new int[]{6, 1, 5, 2, 6, 6, 0, 5, 5}, new String[]{"Gather", "Ford", "Float"}, 0, new String[]{"Supply", ""}, new int[]{1, 0}, new int[]{3, 0}, new int[]{4, 0}, new String[]{"", ""}, new int[]{0, 0}),
//20
            "Transport Copter", new PieceKind("Transport Copter", "Copter", "TR", "TRCP", AERI, "A flying kind of transport; very fast, but fragile.", "Flying", new int[]{2, 5, 1, 5, 4, 9, 0, 7, 5}, new String[]{"Gather", "Lucky", "Benefactor"}, 0, new String[]{"Supply", ""}, new int[]{1, 0}, new int[]{1, 0}, new int[]{4, 0}, new String[]{"", ""}, new int[]{0, 0}),
//21
            "Jetpack", new PieceKind("Jetpack", "Infantry_ST", "LF", "JTPK", AERI, "A flying daredevil who can shoot at aircraft at high speed.", "Flying", new int[]{1, 9, 1, 4, 2, 7, 6, 7, 3}, new String[]{"Raider", "Aerial-Focus", "Responsive"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{3, 1}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{2, 2}),
//22
            "Gunship Copter", new PieceKind("Gunship Copter", "Copter_P", "LF", "GNCP", AERI, "A slower copter with precision missiles and serious miniguns.", "Flying", new int[]{3, 7, 3, 6, 6, 5, 6, 5, 2}, new String[]{"Seek", "Heavy-Focus", "Light-Focus"}, 3, new String[]{"Rocket", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{2, 2}, new String[]{"Rocket", "Machine_Gun"}, new int[]{2, 2}),
//23
            "Blitz Copter", new PieceKind("Blitz Copter", "Copter_S", "LF", "BLCP", AERI, "A very fast copter that gives up some offensive strength.", "Flying", new int[]{2, 9, 1, 6, 2, 6, 6, 7, 2}, new String[]{"Ambush", "Light-Focus", "Aerial-Focus"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{1, 1}, new int[]{3, 1}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{2, 2}),
//24
            "Jammer", new PieceKind("Jammer", "Truck_T", "TV", "JAMR", LIGH, "Can hack enemy facilities and gather loot.", "Wheels", new int[]{4, 2, 2, 6, 6, 8, 0, 6, 3}, new String[]{"Gather", "Benefactor", "Sabotage"}, 0, new String[]{"Hack", ""}, new int[]{2, 0}, new int[]{4, 0}, new int[]{4, 0}, new String[]{"", ""}, new int[]{0, 0}),
//25
            "Build Rig", new PieceKind("Build Rig", "Truck_P", "TV", "BLDR", HEAV, "Can repair vehicles and facilities in remote locations.", "Treads", new int[]{7, 1, 5, 6, 8, 9, 0, 5, 2}, new String[]{"Gather", "Benefactor", "Hike"}, 0, new String[]{"Repair", ""}, new int[]{1, 0}, new int[]{1, 0}, new int[]{4, 0}, new String[]{"", ""}, new int[]{0, 0}),
//26
            "Comm Copter", new PieceKind("Comm Copter", "Copter_T", "TV", "CMCP", AERI, "Can hack at long range from the air, but can't easily gather loot.", "Soaring", new int[]{1, 9, 1, 6, 4, 9, 0, 6, 2}, new String[]{"Lucky", "Benefactor", "Sabotage"}, 0, new String[]{"Hack", ""}, new int[]{4, 0}, new int[]{6, 0}, new int[]{4, 0}, new String[]{"", ""}, new int[]{0, 0}),
//27
            "Mud Tank", new PieceKind("Mud Tank", "Tank_T", "SH", "MTNK", HEAV, "A mix of tank and sniping platform that can enter rivers.", "Treads", new int[]{7, 2, 5, 5, 5, 1, 3, 6, 3}, new String[]{"Stealthy", "Ford", "Juggernaut"}, 3, new String[]{"Longarm", "Cannon"}, new int[]{2, 1}, new int[]{3, 1}, new int[]{1, 3}, new String[]{"Handgun", "Cannon"}, new int[]{1, 2}),
//28
            "Submarine", new PieceKind("Submarine", "Boat_T", "SH", "SBMR", NAVA, "A sneaky long-range naval unit that can devastate facilities.", "Naval", new int[]{1, 8, 9, 3, 9, 1, 1, 4, 2}, new String[]{"Pin", "Structure-Focus", "Naval-Focus"}, 1, new String[]{"Missile", "Torpedo"}, new int[]{5, 1}, new int[]{7, 1}, new int[]{3, 1}, new String[]{"Arc_Missile", "Torpedo"}, new int[]{4, 1}),
//29
            "Stealth Jet", new PieceKind("Stealth Jet", "Plane_T", "SH", "STJT", AERI, "A devastating plane when attacking, though it has no defense.", "Soaring", new int[]{1, 9, 1, 9, 9, 1, 0, 8, 2}, new String[]{"Ambush", "Aerial-Focus", "Seek"}, 2, new String[]{"Rocket", ""}, new int[]{1, 0}, new int[]{1, 0}, new int[]{4, 0}, new String[]{"Rocket", ""}, new int[]{2, 0}),
//30
            "Patrol Boat", new PieceKind("Patrol Boat", "Boat", "BT", "PTBT", NAVA, "A versatile boat that can gather loot at sea and fire on afar.", "Naval", new int[]{8, 1, 2, 6, 4, 6, 5, 5, 2}, new String[]{"Gather", "Aerial-Focus", "Seek"}, 2, new String[]{"Assault", "Assault"}, new int[]{1, 1}, new int[]{3, 3}, new int[]{2, 2}, new String[]{"Machine_Gun", "Machine_Gun"}, new int[]{2, 2}),
//31
            "Cruiser", new PieceKind("Cruiser", "Boat_S", "BT", "CRSR", NAVA, "A fast boat that can devastate aerial and naval units.", "Naval", new int[]{7, 1, 2, 7, 6, 3, 4, 6, 1}, new String[]{"Responsive", "Aerial-Focus", "Naval-Focus"}, 1, new String[]{"Missile", "Torpedo"}, new int[]{2, 1}, new int[]{4, 1}, new int[]{2, 2}, new String[]{"Arc_Missile", "Torpedo"}, new int[]{3, 1}),
//32
            "Battleship", new PieceKind("Battleship", "Boat_P", "BT", "BTSP", NAVA, "A long-range boat with a set of huge anti-armor cannons.", "Naval", new int[]{9, 1, 6, 1, 9, 4, 2, 4, 1}, new String[]{"Juggernaut", "Structure-Focus", "Heavy-Focus"}, 2, new String[]{"Cannon", "Cannon"}, new int[]{1, 1}, new int[]{5, 5}, new int[]{2, 2}, new String[]{"Long_Cannon", "Long_Cannon"}, new int[]{4, 4})
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
