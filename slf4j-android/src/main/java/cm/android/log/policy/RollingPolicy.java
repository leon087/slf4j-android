package cm.android.log.policy;

/**
 * A <code>RollingPolicy</code> is responsible for performing the rolling over
 * of the active log file. The <code>RollingPolicy</code> is also responsible
 * for providing the <em>active log file</em>, that is the live file where
 * logging output will be directed.
 *
 */
public interface RollingPolicy extends LifeCycle {

    /**
     * Rolls over log files according to implementation policy.
     *
     * <p>This method is invoked by {@link PolicyManager}, usually at the
     * behest of its {@link TriggeringPolicy}.
     *
     * @throws RolloverFailure Thrown if the rollover operation fails for any reason.
     */
    void rollover() throws RolloverFailure;

    /**
     * Get the name of the active log file.
     *
     * <p>With implementations such as {@link DefaultBasedPolicy}, this
     * method returns a new file name, where the actual output will be sent.
     *
     * <p>On other implementations, this method might return the FileAppender's
     * file property.
     *
     * @return the name of the active log file
     */
    String getActiveFileName();

    /**
     * relate the policyManager to policy
     * @param engine
     */
    void setParent(PolicyManager engine);
}
