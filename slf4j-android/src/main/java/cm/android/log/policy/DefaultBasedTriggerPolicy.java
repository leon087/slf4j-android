package cm.android.log.policy;

import java.util.Date;

import cm.android.log.policy.helper.SizeAndTimeRemover;

/**
 * 1.判断触发是否触发
 * 2.代理remover类
 */
public class DefaultBasedTriggerPolicy extends TriggeringPolicyBase {

    public void start() {
        archiveRemover = new SizeAndTimeRemover(dbp.fileNamePattern, rc);
        super.start();
    }

    @Override
    public boolean isTriggeringEvent() {
        long time = getCurrentTime();
        if (time >= nextCheck) {
            Date dateOfElapsedPeriod = dateInCurrentPeriod;
            elapsedPeriodsFileName = dbp.fileNamePattern.convert(dateOfElapsedPeriod);
            setDateInCurrentPeriod(time);
            computeNextCheck();
            return true;
        } else {
            return false;
        }
    }

    public void stop() {
        super.stop();
    }

    public SizeAndTimeRemover getArchiveRemover() {
        return archiveRemover;
    }

}
