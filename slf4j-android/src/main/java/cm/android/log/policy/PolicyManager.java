package cm.android.log.policy;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

import cm.android.log.Util;

/**
 * 文件检测类
 */
public class PolicyManager {

    private static final ReentrantLock lock = new ReentrantLock(false);

    private TriggeringPolicy triggeringPolicy;
    private RollingPolicy rollingPolicy;

    private String fileName;
    private File activeFile;
    private String dir;
    private ScheduledExecutorService single;

    private static class Singleton {
        private static PolicyManager instance = new PolicyManager(null, null);
    }

    public static PolicyManager getInstance() {
        return Singleton.instance;
    }

    private PolicyManager(String dir, String file) {
        single = Executors.newSingleThreadScheduledExecutor();
        createRollingPolicy();
        this.dir = dir;
        fileName = file;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return single;
    }

    private void createRollingPolicy() {
        rollingPolicy = new DefaultBasedPolicy();
        rollingPolicy.setParent(this);
        if (rollingPolicy instanceof TriggeringPolicy) {
            triggeringPolicy = (TriggeringPolicy) rollingPolicy;
        }
    }

    public String getFileDir() {
        return dir;
    }

    public void attach(String dir) {
        this.dir = dir;
    }

    public void setFile(String file) {
        fileName = file;
    }

    final public String rawFileProperty() {
        return fileName;
    }

    public String getFile() {
        return rollingPolicy.getActiveFileName();
    }

    /**
     * 开始检测
     */
    public void start() {
        lock.lock();
        try {
            if (triggeringPolicy != null && !triggeringPolicy.isStarted()) {
                triggeringPolicy.start();
            }

            if (rollingPolicy != null && !rollingPolicy.isStarted()) {
                rollingPolicy.start();
            }
        } finally {
            lock.unlock();
        }
    }

    public File getActiveFile() {
        if (activeFile != null) {
            return activeFile;
        }
        if (getFile() != null) {
            return new File(getFile());
        }
        return null;
    }

    /**
     * 关闭检测
     */
    public void stop() {
        lock.lock();
        try {
            if (rollingPolicy != null) {
                rollingPolicy.stop();
            }
            if (triggeringPolicy != null) {
                triggeringPolicy.stop();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 文件check入口
     */
    public void checkFileAppend() {
        lock.lock();
        try {
            if (triggeringPolicy.isTriggeringEvent()) {
                rollover();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implemented by delegating most of the rollover work to a rolling policy.
     */

    public void rollover() {
        try {
            rollingPolicy.rollover();
        } catch (RolloverFailure rf) {
            Util.addError("file rollover error", rf);
        }
        String filename = rollingPolicy.getActiveFileName();
        activeFile = new File(filename);
    }
}
