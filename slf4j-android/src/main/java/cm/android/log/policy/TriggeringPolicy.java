package cm.android.log.policy;

/**
 * A <code>TriggeringPolicy</code> controls the conditions under which roll-over
 * occurs. Such conditions include time of day, file size, an
 * external event, the log request or a combination thereof.
 */

public interface TriggeringPolicy extends LifeCycle {

    /**
     * Should roll-over be triggered at this time?
     */
    boolean isTriggeringEvent();
}
