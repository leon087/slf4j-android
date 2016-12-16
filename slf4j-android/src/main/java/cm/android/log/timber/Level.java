package cm.android.log.timber;

import org.slf4j.event.EventConstants;

import android.util.Log;

public class Level {
    private int levelInt;
    private String levelStr;

    private Level(int levelInt, String levelStr) {
        this.levelInt = levelInt;
        this.levelStr = levelStr;
    }

    public String toString() {
        return levelStr;
    }

    public int toInt() {
        return levelInt;
    }


    final public static int ALL_INT = Integer.MIN_VALUE;
    final public static int OFF_INT = Integer.MAX_VALUE;
    final public static int TRACE_INT = EventConstants.TRACE_INT;
    final public static int DEBUG_INT = EventConstants.DEBUG_INT;
    final public static int INFO_INT = EventConstants.INFO_INT;
    final public static int WARN_INT = EventConstants.WARN_INT;
    final public static int ERROR_INT = EventConstants.ERROR_INT;

    final public static Level ALL = new Level(ALL_INT, "ALL");
    final public static Level OFF = new Level(OFF_INT, "OFF");
    final public static Level TRACE = new Level(TRACE_INT, "TRACE");
    final public static Level DEBUG = new Level(DEBUG_INT, "DEBUG");
    final public static Level INFO = new Level(INFO_INT, "INFO");
    final public static Level WARN = new Level(WARN_INT, "WARN");
    final public static Level ERROR = new Level(ERROR_INT, "ERROR");

    public static Level toLevel(int levelInt) {
        switch (levelInt) {
            case ALL_INT:
                return ALL;
            case TRACE_INT:
                return TRACE;
            case DEBUG_INT:
                return DEBUG;
            case INFO_INT:
                return INFO;
            case WARN_INT:
                return WARN;
            case ERROR_INT:
                return ERROR;
            case OFF_INT:
                return OFF;
            default:
                throw new IllegalArgumentException(levelInt + " not a valid level value");
        }
    }

    public static Level convert(int priority) {
        switch (priority) {
            case Log.VERBOSE:
                return Level.TRACE;
            case Log.DEBUG:
                return Level.DEBUG;
            case Log.INFO:
                return Level.INFO;
            case Log.WARN:
                return Level.WARN;
            case Log.ERROR:
                return Level.ERROR;
            default:
                throw new IllegalArgumentException(priority + " not a valid level value");
        }
    }

//    public static org.slf4j.event.Level toLevel(int priority) {
//        switch (priority) {
//            case Log.VERBOSE:
//                return org.slf4j.event.Level.TRACE;
//            case Log.DEBUG:
//                return org.slf4j.event.Level.DEBUG;
//            case Log.INFO:
//                return org.slf4j.event.Level.INFO;
//            case Log.WARN:
//                return org.slf4j.event.Level.WARN;
//            case Log.ERROR:
//                return org.slf4j.event.Level.ERROR;
//            default:
//                throw new IllegalArgumentException(priority + " not a valid level value");
//        }
//    }
}
