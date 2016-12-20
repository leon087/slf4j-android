package cm.android.log.timber;

import java.io.Closeable;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cm.android.log.FileHandler;
import cm.android.log.Util;

public class FileTree extends AbstractTree implements Closeable {
    private FileHandler handler;
    private ScheduledExecutorService single;
    private ScheduledFuture closeFuture;

    private volatile Object lock = new Object();

    public FileTree(File dir) {
        handler = new FileHandler(dir);
        single = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void close() {
        synchronized (lock) {
            if (handler == null) {
                return;
            }

            handler.close();
            single.shutdownNow();
            try {
                single.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Util.addError("close:awaitTermination", e);
            }

            single = null;
            handler = null;
            closeFuture = null;
            flushFuture = null;
        }
    }

    @Override
    protected void log(int priority, String tag, String message, final Throwable t) {
        Level level = Level.convert(priority);

        String levelStr = Util.rightPad(level.toString(), 5, " ");
        message = Formatter.formatTag(tag, Formatter.CALL_STACK_INDEX) + " - " + message;
        final String msg = Formatter.format(levelStr, message);
        writeLog(msg);
    }

    private void writeLog(final String msg) {
        single.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (handler == null) {
                        return;
                    }
                    handler.log(msg, false);
                    //延迟flush
                    scheduleFlush();
                    //设置超时,超时n秒未写日志则close，每次写日志时重置n
                    scheduleClose();
                }
            }
        });
    }

    private ScheduledFuture flushFuture;

    private void scheduleFlush() {
        if (flushFuture != null) {
            flushFuture.cancel(true);
        }

        flushFuture = single.schedule(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (handler == null) {
                        return;
                    }
                    handler.flush();
                    flushFuture = null;
                }
            }
        }, 10, TimeUnit.SECONDS);
    }

    //TODO ggg 超时n秒未写日志则close，每次写日志时重置n
    private void scheduleClose() {
        if (closeFuture != null) {
            closeFuture.cancel(true);
        }
        closeFuture = single.schedule(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (handler == null) {
                        return;
                    }

                    handler.close();
                    closeFuture = null;
                }
            }
        }, 30, TimeUnit.MINUTES);
    }
}
