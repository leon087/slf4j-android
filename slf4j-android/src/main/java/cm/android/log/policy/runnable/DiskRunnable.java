package cm.android.log.policy.runnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cm.android.log.Util;
import cm.android.log.policy.PolicyManager;

public class DiskRunnable implements Runnable {

    /**
     * 磁盘控件最小值
     */
    private long totalSizeCap;
    /**
     * 文件list
     */
    private List<File> files;

    public DiskRunnable(List<File> files, long totalSizeCap) {
        this.totalSizeCap = totalSizeCap;
        this.files = files;
    }

    @Override
    public void run() {
        //按时间倒叙排列
        if (files == null && files.isEmpty()) {
            return;
        }
        Collections.reverse(files);
        long availableSpace = getAvailableSpace();
        if (availableSpace > totalSizeCap) {
            return;
        }
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext() && availableSpace < totalSizeCap) {
            File file = iterator.next();
            if (availableSpace < totalSizeCap) {
                try {
                    long length = file.length();
                    file.delete();
                    availableSpace += length;
                } catch (Exception e) {
                    Util.addError("diskRunnable file error", e);
                }
            }
            if (availableSpace > totalSizeCap) {
                break;
            }
        }
    }

    //一种获取文件大小的
    public long getFileSize(File f) {
        if (f.exists() && f.isFile()) {
            try {
                FileInputStream fis = new FileInputStream(f);
                FileChannel fc = fis.getChannel();
                return fc.size();
            } catch (IOException e) {
                Util.addError("get file size error", e);
            }
        }
        return 0;
    }

    public long getAvailableSpace() {
        File dir = new File(PolicyManager.getInstance().getFileDir());
        return dir.getUsableSpace();
    }
}
