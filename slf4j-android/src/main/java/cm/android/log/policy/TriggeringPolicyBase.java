package cm.android.log.policy;

import java.io.File;
import java.util.Date;

import cm.android.log.policy.helper.RollingCalendar;
import cm.android.log.policy.helper.SizeAndTimeRemover;

/**
 * 触发类抽象类
 */
abstract class TriggeringPolicyBase extends LifeCycleBase implements TriggeringPolicy {

    protected DefaultBasedPolicy dbp;

    protected SizeAndTimeRemover archiveRemover = null;
    protected String elapsedPeriodsFileName;
    protected RollingCalendar rc;

    protected long artificialCurrentTime = -1;
    protected Date dateInCurrentPeriod = null;

    protected long nextCheck;
    protected boolean started = false;

    public boolean isStarted() {
        return started;
    }

    public void start() {
        rc = new RollingCalendar();
        rc.init(dbp.fileNamePattern.getPattern());

        setDateInCurrentPeriod(new Date(getCurrentTime()));

        if (dbp.getParentsRawFileProperty() != null) {
            File currentFile = new File(dbp.getParentsRawFileProperty());
            if (currentFile.exists() && currentFile.canRead()) {
                setDateInCurrentPeriod(new Date(currentFile.lastModified()));
            }
        }
        computeNextCheck();
        super.start();
    }

    public void stop() {
        super.stop();
    }

    protected void computeNextCheck() {
        nextCheck = rc.getNextTriggeringMillis(dateInCurrentPeriod);
    }

    protected void setDateInCurrentPeriod(long now) {
        dateInCurrentPeriod.setTime(now);
    }

    public void setDateInCurrentPeriod(Date _dateInCurrentPeriod) {
        this.dateInCurrentPeriod = _dateInCurrentPeriod;
    }

    public String getElapsedPeriodsFileName() {
        return elapsedPeriodsFileName;
    }

    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return dbp.fileNamePattern.convert(dateInCurrentPeriod);
    }

    public void setCurrentTime(long timeInMillis) {
        artificialCurrentTime = timeInMillis;
    }

    public long getCurrentTime() {
        if (artificialCurrentTime >= 0) {
            return artificialCurrentTime;
        } else {
            return System.currentTimeMillis();
        }
    }

    public void setTimeBasedRollingPolicy(DefaultBasedPolicy _dbp) {
        this.dbp = _dbp;
    }

    public SizeAndTimeRemover getArchiveRemover() {
        return archiveRemover;
    }

}
