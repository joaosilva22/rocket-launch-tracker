package me.joaosilva22.rocketlaunchtracker.models;

public class LaunchDatabaseContract {

    public LaunchDatabaseContract() {}

    public static abstract class LaunchEntry {
        public static final String TABLE_NAME = "launch";
        public static final String COLUMN_LAUNCH_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NET = "net";
    }

    public static abstract class DetailsEntry {
        public static final String TABLE_NAME = "details";
        public static final String COLUMN_LAUNCH_ID = "_id";
        public static final String COLUMN_MISSION_NAME = "name";
        public static final String COLUMN_MISSION_DESCRIPTION = "description";
        public static final String COLUMN_PAD_NAME = "pad";
        public static final String COLUMN_ROCKET_NAME = "rocket";
        public static final String COLUMN_WINDOW_START = "window_start";
        public static final String COLUMN_WINDOW_END = "window_end";
        public static final String COLUMN_NOTIFY = "notify";
    }
}
