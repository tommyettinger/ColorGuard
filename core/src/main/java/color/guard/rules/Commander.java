package color.guard.rules;

import java.io.Serializable;
import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class Commander implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final Commander[] ENTRIES = new Commander[] {
    new Commander("Hangman", "The undisputed leader of Noose, who has united the worst of humanity under one creed: Take it all.", new String[] {"Raider", "Shielded"}, new String[] {"Bandit", "Barbarian", "Samurai"}, makeMap("BZKA", 1, "AAGN", 1, "BTSP", 1)),
    new Commander("Tarpit", "A hitman who follows his own strange rules; he killed his commander on Hangman's orders, then surprisingly assumed control.", new String[] {"Ambush", "Seek"}, new String[] {"Assassin", "Skirmisher", "Veteran"}, makeMap("MISN", 1, "SBMR", 1, "STJT", 1)),
    new Commander("Wrath", "An outlaw-biker-turned-cult-leader who tells his followers that only Noose will survive the End Times.", new String[] {"Vengeful", "Incinerator"}, new String[] {"Bandit", "Chaplain", "Skirmisher"}, makeMap("BIKE", 3)),
    new Commander("Scurvy", "A pirate who terrorized the seas just outside Snow's control; now he exploits the rising tide to raid further inland in the name of Noose.", new String[] {"Ford", "Raider"}, new String[] {"Dragoon", "Hunter", "Pirate"}, makeMap("PTBT", 2, "MTNK", 1)),
    new Commander("Jackal", "A genocidal maniac who wants to exterminate the people of Gem, and has fought in numerous wars to further her aims.", new String[] {"Troop-Focus", "Sand-Strike"}, new String[] {"Hermit", "Paladin", "Veteran"}, makeMap("LTNK", 2, "FLMT", 1)),
    new Commander("Hitch", "A twisted artist, exiled from Sun, who joined Noose to see his horrible visions come to life.", new String[] {"Aerial-Focus", "Swarm"}, new String[] {"Alchemist", "Dragoon", "Sage"}, makeMap("RFSN", 1, "LPLN", 1, "JTPK", 1)),
    new Commander("Crusher", "A hugely obese “Queen of the Illegal” and leader of a crime family that merged with Noose, tricking Coin in the process.", new String[] {"Pin", "Juggernaut"}, new String[] {"Bandit", "Pirate", "Shaman"}, makeMap("HCNN", 2, "WTNK", 1)),
    new Commander("Blight", "A horrid researcher tasked with destroying the environment in the parts of Leaf that Noose doesn't consider worth capturing.", new String[] {"Venomous", "Ruins-Strike"}, new String[] {"Alchemist", "Assassin", "Skirmisher"}, makeMap("SART", 1, "HBMR", 1, "MTNK", 1)),
    new Commander("Screech", "A mad scientist from the ex-nation of Stone (now Wave) who didn't like how they paid him, so he built a superweapon to wipe them out.", new String[] {"Vengeful", "Structure-Focus"}, new String[] {"Alchemist", "Hermit", "Pirate"}, makeMap("MRSN", 1, "JAMR", 1, "CRSR", 1)),
    new Commander("Fang", "Noose's main mouthpiece; he blames his own group's atrocities on Pit, thinking they don't have the strength to fight back.", new String[] {"Swarm", "Light-Focus"}, new String[] {"Chaplain", "Dancer", "Hunter"}, makeMap("RECN", 1, "CMCP", 2)),
    new Commander("Hyunna", "The eccentric dictator who inherited Snow from her father, Hyunso; she wants absolute control over her subjects.", new String[] {"Benefactor", "Ice-Home"}, new String[] {"Dancer", "Paladin", "Sage"}, makeMap("FIJT", 3)),
    new Commander("Laiseng", "A strange mix of researcher, commander, and propagandist, Laiseng alters scientific findings to suit Hyunna's whims.", new String[] {"Shielded", "Gather"}, new String[] {"Alchemist", "Sage", "Samurai"}, makeMap("JTPK", 3)),
    new Commander("Gwol", "One of Laiseng's subjects, Gwol was a normal general who was force-grown to twice his height; he and his troops also have venomous fangs.", new String[] {"Venomous", "Juggernaut"}, new String[] {"Barbarian", "Dragoon", "Hermit"}, makeMap("BZKA", 3)),
    new Commander("Kanul", "A brutal spymaster who seeks to manipulate Noose into fighting other nations while he quells potential uprisings at home.", new String[] {"Stealthy", "Ice-Strike"}, new String[] {"Assassin", "Veteran", "Warden"}, makeMap("SART", 3)),
    new Commander("Tashuin", "The leader of Gem and a veteran of countless wars, Tashuin wants to personally kill his sworn nemesis, Noose commander Jackal.", new String[] {"Reliable", "Sand-Home"}, new String[] {"Paladin", "Skirmisher", "Veteran"}, makeMap("MRSN", 1, "HBMR", 1, "TRCP", 1)),
    new Commander("Jalnez", "The woman who keeps the faith alive in Gem territory; her soldiers are especially brave and routinely take on stronger units.", new String[] {"Lucky", "Benefactor"}, new String[] {"Chaplain", "Dragoon", "Warden"}, makeMap("INFY", 2, "SPTR", 1)),
    new Commander("Halwahi", "A driven woman bent on revenge against Noose for killing much of her family; good at fighting with and against planes.", new String[] {"Aerial-Focus", "Vengeful"}, new String[] {"Hunter", "Samurai", "Veteran"}, makeMap("LPLN", 2, "AART", 1)),
    new Commander("Morazuk", "Leader of an ancient group of desert wanderers respected as much for their nimble feet as their keen eyes.", new String[] {"Sand-Strike", "Seek"}, new String[] {"Assassin", "Nomad", "Skirmisher"}, makeMap("RFSN", 2, "LART", 1)),
    new Commander("Ray", "The Patriot Lord of Sun, beloved by his people and despised by his enemies; he favors arming his many troops with incendiary weapons.", new String[] {"Incinerator", "Reliable"}, new String[] {"Dragoon", "Samurai", "Warden"}, makeMap("WTNK", 1, "STJT", 1, "BTSP", 1)),
    new Commander("Matt", "A headbanging military man who pipes loud, furious music from speakers on his helicopters.", new String[] {"Juggernaut", "Heavy-Focus"}, new String[] {"Barbarian", "Dancer", "Skirmisher"}, makeMap("GNCP", 3)),
    new Commander("Loshandra", "A bodybuilder who pumps steroids into her troops' drinks to ensure her foot soldiers are unparalleled in strength.", new String[] {"Shielded", "Light-Focus"}, new String[] {"Barbarian", "Dragoon", "Pirate"}, makeMap("INFY", 1, "BZKA", 1, "MRSN", 1)),
    new Commander("Johnny", "A veteran of many campaigns who repeatedly argued against hiring mercenaries, and is determined to stop Noose now.", new String[] {"Gather", "Pin"}, new String[] {"Nomad", "Samurai", "Veteran"}, makeMap("LTNK", 1, "AAGN", 1, "LPLN", 1)),
    new Commander("Saffron", "A brave commander who acts overconfident to fool her enemies, especially her old acquaintances at Noose.", new String[] {"Lucky", "Raider"}, new String[] {"Bandit", "Dancer", "Sage"}, makeMap("BIKE", 1, "AAGN", 1, "BLCP", 1)),
    new Commander("Duster", "The mysterious second-in-command at Coin who sabotaged Noose's superweapons from inside; he seems to have been trained as a spy.", new String[] {"Responsive", "Structure-Focus"}, new String[] {"Alchemist", "Assassin", "Samurai"}, makeMap("MISN", 1, "JAMR", 2)),
    new Commander("Fatback", "A commander who claims he left Noose because the food was lousy; he has more of a conscience than he lets on.", new String[] {"Gather", "Carrier"}, new String[] {"Hunter", "Paladin", "Warden"}, makeMap("SPTR", 1, "AMTR", 1, "HBMR", 1)),
    new Commander("Swatter", "A peculiar older woman who joined Coin 20 years after the last battle she had commanded; she hasn't skipped a beat.", new String[] {"Reliable", "Aerial-Focus"}, new String[] {"Hermit", "Shaman", "Veteran"}, makeMap("RFSN", 1, "LTNK", 1, "LART", 1)),
    new Commander("Thorn", "The teenage inheritor to Leaf's royal throne; while knowledgeable about all of the lands she rules, she lacks combat experience.", new String[] {"Forest-Home", "Traverse"}, new String[] {"Paladin", "Hunter", "Warden"}, makeMap("SART", 2, "SPTR", 1)),
    new Commander("Seed", "A precocious child from the barren grasslands who shows remarkable tactical skill, and is beloved by her soldiers.", new String[] {"Plains-Home", "Benefactor"}, new String[] {"Nomad", "Sage", "Skirmisher"}, makeMap("BZKA", 1, "BIKE", 1, "LART", 1)),
    new Commander("Root", "A warrior from forests that have been flooded by Noose's superweapon, who has adapted to fighting in now-ubiquitous swamps.", new String[] {"Ford", "Traverse"}, new String[] {"Barbarian", "Pirate", "Warden"}, makeMap("MTNK", 2, "PTBT", 1)),
    new Commander("Bark", "A gruff medicine woman from jungle-covered mountains who seems to always be accompanied by fierce winds.", new String[] {"Hike", "Mountain-Home"}, new String[] {"Chaplain", "Hermit", "Shaman"}, makeMap("MISN", 3)),
    new Commander("Oddmund", "A commander who once fought against Stone, then graciously gave them a home after theirs was destroyed.", new String[] {"Benefactor", "Heavy-Focus"}, new String[] {"Paladin", "Pirate", "Samurai"}, makeMap("BTSP", 2, "TRCP", 1)),
    new Commander("Olga", "One of the highest-ranking commanders in Stone, she is now the tough-as-nails second-in-command in Wave's army.", new String[] {"Reliable", "Naval-Focus"}, new String[] {"Dragoon", "Sage", "Skirmisher"}, makeMap("LART", 1, "MRSN", 1, "WTNK", 1)),
    new Commander("Nevsky", "Stone's trench-warfare specialist who has since become Wave's expert on breaching Noose's land defenses.", new String[] {"Structure-Focus", "Pin"}, new String[] {"Alchemist", "Samurai", "Warden"}, makeMap("MTNK", 1, "HCNN", 1, "AMTR", 1)),
    new Commander("Eska", "A hardy lady of the sea who has long served in Wave's navy, and deeply believes in Oddmund.", new String[] {"Reliable", "Ford"}, new String[] {"Barbarian", "Chaplain", "Paladin"}, makeMap("SBMR", 3)),
    new Commander("Willum", "A frontiersman who has, through various unlikely events, been thrust into the leadership of Pit; his humble beginnings are why many trust him.", new String[] {"Lucky", "Radioactive"}, new String[] {"Barbarian", "Hunter", "Skirmisher"}, makeMap("RFSN", 2, "JTPK", 1)),
    new Commander("Zoku", "A larger-than-life ex-gangster who entered Pit territory to establish a paradise for the cultural fringe, and now fights to defend his home.", new String[] {"Road-Strike", "Radioactive"}, new String[] {"Bandit", "Dragoon", "Shaman"}, makeMap("BIKE", 1, "SPTR", 1, "RECN", 1)),
    new Commander("Anahit", "A young woman born in Pit with significant mutations, including super-human memory and calculation, and these aid her tactical skill.", new String[] {"Seek", "Radioactive"}, new String[] {"Hermit", "Nomad", "Sage"}, makeMap("JAMR", 2, "AART", 1)),
    new Commander("Trang", "A revolutionary whose homeland was annihilated by nuclear war; as one of the few survivors, she seeks to form Pit into a stable nation.", new String[] {"Ruins-Home", "Radioactive"}, new String[] {"Samurai", "Veteran", "Warden"}, makeMap("BLDR", 2, "LTNK", 1)),
  };

  public static final Map<String, Commander> MAPPING = makeMap(
  "Hangman", ENTRIES[0], "Tarpit", ENTRIES[1], "Wrath", ENTRIES[2], "Scurvy", ENTRIES[3],
  "Jackal", ENTRIES[4], "Hitch", ENTRIES[5], "Crusher", ENTRIES[6], "Blight",
  ENTRIES[7], "Screech", ENTRIES[8], "Fang", ENTRIES[9], "Hyunna", ENTRIES[10],
  "Laiseng", ENTRIES[11], "Gwol", ENTRIES[12], "Kanul", ENTRIES[13],
  "Tashuin", ENTRIES[14], "Jalnez", ENTRIES[15], "Halwahi", ENTRIES[16], "Morazuk",
  ENTRIES[17], "Ray", ENTRIES[18], "Matt", ENTRIES[19], "Loshandra", ENTRIES[20],
  "Johnny", ENTRIES[21], "Saffron", ENTRIES[22], "Duster", ENTRIES[23],
  "Fatback", ENTRIES[24], "Swatter", ENTRIES[25], "Thorn", ENTRIES[26],
  "Seed", ENTRIES[27], "Root", ENTRIES[28], "Bark", ENTRIES[29], "Oddmund",
  ENTRIES[30], "Olga", ENTRIES[31], "Nevsky", ENTRIES[32], "Eska", ENTRIES[33],
  "Willum", ENTRIES[34], "Zoku", ENTRIES[35], "Anahit", ENTRIES[36],
  "Trang", ENTRIES[37]);

  public String name;

  public String description;

  public String[] features;

  public String[] aspects;

  public Map<String, Integer> favorites;

  public Commander() {
  }

  public Commander(String name, String description, String[] features, String[] aspects,
      Map<String, Integer> favorites) {
    this.name = name;
    this.description = description;
    this.features = features;
    this.aspects = aspects;
    this.favorites = favorites;
  }

  private static long hash64(String data) {
    if (data == null) return 0;
    long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
    final int len = data.length();
    for (int i = 0; i < len; i++)
      result += (a ^= 0x8329C6EB9E6AD3E3L * data.charAt(i));
    return result * (a | 1L) ^ (result >>> 27 | result << 37);
  }

  private static long hashBasic(Object data) {
    return (data == null) ? 0 : data.hashCode() * 0x5851F42D4C957F2DL + 0x14057B7EF767814FL;
  }

  public long hash64() {
    long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L, innerR, innerA;
    int len;
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(name));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(description));
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (features == null ? 0 : features.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(features[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (aspects == null ? 0 : aspects.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(aspects[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    result += (a ^= 0x8329C6EB9E6AD3E3L * hashBasic(favorites));
    return result * (a | 1L) ^ (result >>> 27 | result << 37);
  }

  public int hashCode() {
    return (int)(hash64() & 0xFFFFFFFFL);
  }

  private static boolean stringArrayEquals(String[] left, String[] right) {
    if (left == right) return true;
    if (left == null || right == null) return false;
    final int len = left.length;
    if(len != right.length) return false;
    String l, r;
    for (int i = 0; i < len; i++) { if(((l = left[i]) != (r = right[i])) && (((l == null) != (r == null)) || !l.equals(r))) { return false; } }
    return true;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Commander other = (Commander) o;
    if (name != null ? !name.equals(other.name) : other.name != null) return false;
    if (description != null ? !description.equals(other.description) : other.description != null) return false;
    if(!stringArrayEquals(features, other.features)) return false;
    if(!stringArrayEquals(aspects, other.aspects)) return false;
    if (favorites != null ? !favorites.equals(other.favorites) : other.favorites != null) return false;
    return true;
  }

  public static Commander get(String item) {
    return MAPPING.get(item);
  }
}
