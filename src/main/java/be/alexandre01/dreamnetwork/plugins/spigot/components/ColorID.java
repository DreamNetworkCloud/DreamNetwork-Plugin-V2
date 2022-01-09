package be.alexandre01.dreamnetwork.plugins.spigot.components;

public enum ColorID {
        BLACK(0),
        BLUE(4),
        BROWN(3),
        CYAN(6),
        GRAY(8),
        GREEN(2),
        LIGHT_BLUE(12),
        LIGHT_GRAY(7),
        LIME(10),
        MAGENTA(13),
        ORANGE(14),
        PINK(9),
        PURPLE(5),
        RED(1),
        WHITE(15),
        YELLOW(11);

        private final int id;

        ColorID(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }