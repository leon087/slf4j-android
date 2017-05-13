/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2013, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 * or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package cm.android.log.policy;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cm.android.log.LogConstants;
import cm.android.log.policy.helper.FileNamePattern;
import cm.android.log.policy.helper.SizeAndTimeRemover;

import static cm.android.log.Util.addError;

/**
 * trigger和rolling的控制类
 */
public class DefaultBasedPolicy extends RollingPolicyBase implements TriggeringPolicy {
    static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";

    protected FileNamePattern fileNamePattern;

    private String fileNamePatternStr = LogConstants.DATE_FORMAT;

    private int maxHistory = LogConstants.INFINITE_HISTORY;
    private long totalSizeCap = LogConstants.MAX_SPACE_SIZE;

    private SizeAndTimeRemover archiveRemover;

    private TriggeringPolicyBase diskAndNumberBasedTriggerPolicy;

    private Future<?> cleanUpFuture;

    private boolean cleanHistoryOnStart = LogConstants.CLEAN_HISTORY_ONSTART;

    public void start() {
        if (isStarted()) {
            return;
        }
        if (fileNamePatternStr != null) {
            fileNamePattern = new FileNamePattern(fileNamePatternStr);
        } else {
            throw new IllegalStateException(FNP_NOT_SET
                    + LogConstants.SEE_FNP_NOT_SET);
        }

        if (diskAndNumberBasedTriggerPolicy == null) {
            diskAndNumberBasedTriggerPolicy = new DefaultBasedTriggerPolicy();
        }
        diskAndNumberBasedTriggerPolicy.setTimeBasedRollingPolicy(this);
        diskAndNumberBasedTriggerPolicy.start();

        archiveRemover = diskAndNumberBasedTriggerPolicy.getArchiveRemover();
        if (maxHistory != -1) {
            archiveRemover.setMaxHistory(maxHistory);
            archiveRemover.setTotalSizeCap(totalSizeCap);
            if (cleanHistoryOnStart) {
                cleanUpFuture = archiveRemover.cleanAsynchronously(getActiveFileName());
            }
        }

        super.start();
    }

    @Override
    public void stop() {
        if (!isStarted())
            return;
        waitForAsynchronousJobToStop(cleanUpFuture, "clean-up");
        super.stop();
    }

    private void waitForAsynchronousJobToStop(Future<?> aFuture, String jobDescription) {
        if (aFuture != null) {
            try {
                aFuture.get(LogConstants.SECONDS_TO_WAIT_FOR_COMPRESSION_JOBS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                addError("Timeout while waiting for " + jobDescription + " job to finish", e);
            } catch (Exception e) {
                addError("Unexpected exception while waiting for " + jobDescription + " job to finish", e);
            }
        }
    }

    public void setDiskBasedAndNumberTriggeringPolicy(
            TriggeringPolicyBase diskAndNumberBasedTriggerPolicy) {
        this.diskAndNumberBasedTriggerPolicy = diskAndNumberBasedTriggerPolicy;
    }

    public TriggeringPolicyBase getDiskBasedAndNumberTriggeringPolicy() {
        return diskAndNumberBasedTriggerPolicy;
    }

    public void rollover() throws RolloverFailure {
        if (archiveRemover != null) {
            this.cleanUpFuture = archiveRemover.cleanAsynchronously(getActiveFileName());
        }
    }

    //获取当前处于激活中的文件
    public String getActiveFileName() {
        String parentsRawFileProperty = getParentsRawFileProperty();
        if (parentsRawFileProperty != null) {
            return parentsRawFileProperty;
        } else {
            return diskAndNumberBasedTriggerPolicy
                    .getCurrentPeriodsFileNameWithoutCompressionSuffix();
        }
    }

    public boolean isTriggeringEvent() {
        if (diskAndNumberBasedTriggerPolicy != null) {
            return diskAndNumberBasedTriggerPolicy.isTriggeringEvent();
        }
        return false;
    }

    /**
     * Get the number of archive files to keep.
     *
     * @return number of archive files to keep
     */
    public int getMaxHistory() {
        return maxHistory;
    }

    /**
     * Set the maximum number of archive files to keep.
     *
     * @param maxHistory number of archive files to keep
     */
    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public boolean isCleanHistoryOnStart() {
        return cleanHistoryOnStart;
    }

    /**
     * Should archive removal be attempted on application start up? Default is false.
     *
     * @param cleanHistoryOnStart true to remove old logs on startup
     * @since 1.0.1
     */
    public void setCleanHistoryOnStart(boolean cleanHistoryOnStart) {
        this.cleanHistoryOnStart = cleanHistoryOnStart;
    }

    @Override
    public String toString() {
        return "cm.android.log.policy.TimeBasedRollingPolicy";
    }
}
