package cm.android.log.policy.runnable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import cm.android.log.LogConstants;
import cm.android.log.Util;
import cm.android.log.policy.helper.FileNamePattern;

//归并list
public class ProcessFileRunnable implements Runnable {

    private List<List<File>> listList;
    private FileNamePattern fileNamePattern = null;

    public ProcessFileRunnable(List<List<File>> listList, FileNamePattern pattern) {
        this.listList = listList;
        this.fileNamePattern = pattern;
    }

    @Override
    public void run() {
        Iterator<List<File>> iterator = listList.iterator();
        while (iterator.hasNext()) {
            List<File> files = iterator.next();
            try {
                doZipToGz(files);
                removeLogFile(files);
            } catch (Exception e) {
                Util.addError(e.getMessage(), e);
            }
        }
    }

    /**
     * 删除已经tar的log文件
     */
    private void removeLogFile(List<File> files) {
        Util.deleteListFile(files);
    }

    /**
     * 压缩文件到gz
     */
    public void doZipToGz(List<File> fileList) throws Exception {
        if (fileList.isEmpty()) {
            return;
        }
        File file = fileList.get(0);
        String oldFile = file.getName();
        int index = oldFile.indexOf(LogConstants.SPLIT_UNDERLINE);
        String newFile = index != -1 ? oldFile.substring(0, index) :
                oldFile.substring(0, oldFile.indexOf("."));
        File gzFile = new File(file.getParentFile(), newFile + LogConstants.FILE_TAR + LogConstants.FILE_GZ);

//        if (fileList.size() == 1 && Util.isGzFile(file)) {
//            return gzFile;
//        }
//
//        //删除当前分组list下gz文件,并未真正删除，只是从list中移除
//        Iterator<File> iterator = fileList.iterator();
//        while (iterator.hasNext()) {
//            File f = iterator.next();
//            //就是需要压缩的文件名
//            if (fileNamePattern.isSameFileNameWithOutSuffix(f.getName(), gzFile.getName())
//                    && f.getName().endsWith(LogConstants.FILE_GZ)) {
//                iterator.remove();
//            }
//        }
        compressFile(fileList, gzFile);
    }

    /**
     * 压缩文件
     *
     * @param files  需要压缩的文件列表
     * @param gzFile gzFile
     */
    private static void compressFile(List<File> files, File gzFile) throws Exception {
        if (files == null || files.isEmpty()) {
            return;
        }
        try {
            if (!gzFile.exists()) {
                gzFile.createNewFile();
            }
            Util.multiFileToGzip(files, gzFile);
        } catch (IOException e) {
            Util.addError("compress file error", e);
            throw e;
        }
    }
}
