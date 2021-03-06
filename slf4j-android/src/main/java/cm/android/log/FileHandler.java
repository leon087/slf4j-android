package cm.android.log;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import cm.android.log.policy.PolicyManager;

public class FileHandler implements Closeable {
    private OutputStream os;
    private Writer writer;
    private boolean writerInitialized;
    private File logFile;
    private File dir;
    private boolean append;
    private String tag;
    private String fileName;

    public FileHandler(File dir, String tag) {
        this.dir = dir;
        this.append = true;
        this.tag = tag;

        PolicyManager.getInstance().attach(dir.getAbsolutePath());
    }

    private void initializeWriter(String fileName) throws FileNotFoundException {
        if (!writerInitialized) {
            Util.checkDirectory(dir);
            logFile = new File(dir, fileName);
            os = new MeasureOutputStream(new BufferedOutputStream(
                    new FileOutputStream(logFile, append)), logFile.length());

            this.writerInitialized = true;
            this.writer = new OutputStreamWriter(this.os);

            PolicyManager.getInstance().setFile(logFile.getAbsolutePath());
        }
    }

    private boolean checkWriter() {
        String format = Util.formatDate(LogConstants.DATE_FORMAT, System.currentTimeMillis());
        fileName = String.format("%s" + LogConstants.SPLIT_UNDERLINE + "[%s].log", format, tag);

        if (logFile != null && !logFile.getName().contains(fileName)) {
//            android.util.Log.e("hhhh", "hhhh format = " + format);
//            android.util.Log.e("hhhh", "hhhh logFile = " + logFile.getName());
            close();
        }

        try {
            initializeWriter(fileName);
            checkFileAndDisk();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    @Override
    public synchronized final void close() {
        if (this.os != null) {
            flush();
            try {
                this.writer.close();
//                engine.stop();
                android.util.Log.e("hhhhh", "hhhhh:close");
            } catch (IOException e) {
                Util.addError("close", e);
            } finally {
                this.writer = null;
                this.os = null;
            }
        }
        writerInitialized = false;
    }

    public synchronized void flush() {
        if (this.os != null) {
            try {
                if (this.writer != null) {
                    this.writer.flush();
                } else {
                    this.os.flush();
                }
            } catch (IOException e) {
                Util.addError("flush", e);
            }
        }
    }

    private synchronized void publish(String msg) {
        try {
            if (checkWriter()) {
                this.writer.write(msg);
                this.writer.write(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            Util.addError("publish", e);
            close();
        }
    }

    public void log(String message, boolean immediateFlush) {
        publish(message);
        if (immediateFlush) {
            flush();
        }
    }

    public File getDir() {
        return dir;
    }

    /**
     * 检测
     */
    private void checkFileAndDisk() {
        PolicyManager.getInstance().checkFileAppend();
    }
}
