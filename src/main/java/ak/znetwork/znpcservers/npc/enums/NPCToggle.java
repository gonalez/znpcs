package ak.znetwork.znpcservers.npc.enums;

public enum  NPCToggle {

    HOLO,NAME,GLOW,MIRROR,LOOK;

    public static NPCToggle fromString(String text) {
        for (NPCToggle b : NPCToggle.values()) {
            if (text != null && b.name().toUpperCase().equalsIgnoreCase(text.toUpperCase())) {
                return b;
            }
        }
        return null;
    }
}
