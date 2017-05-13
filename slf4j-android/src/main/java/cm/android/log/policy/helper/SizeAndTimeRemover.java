package cm.android.log.policy.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import cm.android.log.LogConstants;
import cm.android.log.Util;
import cm.android.log.policy.PolicyManager;
import cm.android.log.policy.runnable.DiskRunnable;
import cm.android.log.policy.runnable.ProcessFileRunnable;

public class SizeAndTimeRemover implements ArchiveRemover {

    static protected final long UNINITIALIZED = -1;

    final FileNamePattern fileNamePattern;
    final RollingCalendar rc;
    private String activeFile;
    private int maxHistory = LogConstants.INFINITE_HISTORY;
    private long totalSizeCap = LogConstants.MAX_SPACE_SIZE;

    public SizeAndTimeRemover(FileNamePattern fileNamePattern,
                              RollingCalendar rc) {
        this.fileNamePattern = fileNamePattern;
        this.rc = rc;
    }

    @Override
    public void clean() {

    }

    @Override
    public Future<?> cleanAsynchronously(String activeFile) {
        this.activeFile = activeFile;
        ArchiveRunnable runnable = new ArchiveRunnable();
        ExecutorService executorService = PolicyManager.getInstance().getScheduledExecutorService();
        Future<?> future = executorService.submit(runnable);
        return future;
    }

    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    @Override
    public void setTotalSizeCap(long totalSizeCap) {
        this.totalSizeCap = totalSizeCap;
    }

    class ArchiveRunnable implements Runnable {

        public ArchiveRunnable() {
        }

        @Override
        public void run() {
            /**
             * 1. 获取所有文件时通过filter排除gz、排除正在编辑的文件
             * 2. 得到files进行分组-->List<List>
             * 3. 迭代器遍历List，压缩，成功后删除|不成功continue
             */
            //按天数查看并处理
//            List<File> files = new ArrayList<>();
            List<File> logFiles = listFiles(LogConstants.FILE_EXT);
            sortListFiles(logFiles, true);
            List<List<File>> groupFiles = groupFile(logFiles);
            processFile(groupFiles);

            //清理模块
            List<File> gzFiles = listFiles(LogConstants.FILE_GZ);
            sortListFiles(gzFiles, true);
            cleanHistoryByCount(gzFiles);

            //磁盘大小
            cleanHistoryBySize(gzFiles);
        }

        /**
         * 处理文件list 并把压缩好后的list返回
         */
        private void processFile(List<List<File>> list) {
//            去除当前正在编辑的文件
//            if (list != null && !list.isEmpty()) {
//                List<File> files = list.get(0);
//                if (compareFileName(activeFile, files.get(0).getName())) {
//                    list.remove(0);
//                }
//            }
            new ProcessFileRunnable(list, fileNamePattern).run();
        }

//        private File removeGzFile(List<File> fileList) {
//            if (fileList.isEmpty()) {
//                return null;
//            }
//            File file = fileList.get(0);
//            String oldFile = file.getName();
//            int index = oldFile.indexOf(LogConstants.SPLIT_UNDERLINE);
//            String newFile = index != -1 ? oldFile.substring(0, index) :
//                    oldFile.substring(0, oldFile.indexOf("."));
//            File gzFile = new File(file.getParentFile(), newFile + LogConstants.FILE_TAR + LogConstants.FILE_GZ);
//
//            if (fileList.size() == 1 && Util.isGzFile(file)) {
//                return gzFile;
//            }
//
//            //删除当前分组list下gz文件,并未真正删除，只是从list中移除
//            Iterator<File> iterator = fileList.iterator();
//            while (iterator.hasNext()) {
//                File f = iterator.next();
//                //就是需要压缩的文件名
//                if (fileNamePattern.isSameFileNameWithOutSuffix(f.getName(), gzFile.getName())
//                        && f.getName().endsWith(LogConstants.FILE_GZ)) {
//                    iterator.remove();
//                }
//            }
//        }

        /**
         * 清除多余文件并返回清理后的文件
         * gz文件
         */
        private void cleanHistoryByCount(List<File> lists) {
            List<File> removerList = new ArrayList<>();
            if (lists.size() > maxHistory) {
                removerList.addAll(lists.subList(maxHistory, lists.size()));
                lists.removeAll(removerList);
            }
            Util.deleteListFile(removerList);
        }

        /**
         * 根据磁盘大小清理
         */
        private void cleanHistoryBySize(List<File> fileList) {
            if (fileList != null && !fileList.isEmpty()) {
                new DiskRunnable(fileList, totalSizeCap).run();
            }
        }

        /**
         * 按日期分组
         */
        private List<List<File>> groupFile(List<File> files) {
            List<List<File>> listList = new ArrayList<>();
            Iterator<File> iterator = files.iterator();
            while (iterator.hasNext()) {
                File file = iterator.next();
                if (listList.isEmpty()) {
                    List<File> subList = new ArrayList<>();
                    subList.add(file);
                    listList.add(subList);
                } else {
                    List<File> list = listList.get(listList.size() - 1);
                    File old = list.get(0);
                    if (compareFileName(file.getName(), old.getName())) {
                        list.add(file);
                    } else {
                        List<File> subList = new ArrayList<>();
                        subList.add(file);
                        listList.add(subList);
                    }
                }
            }
            return listList;
        }

        /**
         * 排序list
         * reverse 为true 为时间倒叙排列
         */
        void sortListFiles(List<File> fileList, boolean reverse) {
            Collections.sort(fileList, new Comparable(reverse));
        }

        /**
         * 遍历file list 排除当前正在编辑的文件
         */
        List<File> listFiles(final String ext) {
            String dir = PolicyManager.getInstance().getFileDir();
            File file = new File(dir);
            Util.checkDirectory(file);
            if (!file.isDirectory()) {
                return Collections.EMPTY_LIST;
            }
            final Pattern pattern = fileNamePattern.getDefaultFormatPattern();
            File[] files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    //去除当前正在编辑的(log文件)
                    //根据后缀名获取文件
                    //根据日期规则选择文件
                    if (filename != null && pattern.matcher(filename).find() && filename.endsWith(ext)) {
                        if (filename.endsWith(LogConstants.FILE_EXT) && compareFileName(activeFile, filename)) {
                            return false;
                        }
                        return true;
                    }
                    return false;
                }
            });
            if (files == null) {
                return Collections.EMPTY_LIST;
            }
            List<File> fileList = new ArrayList<>();
            fileList.addAll(Arrays.asList(files));
            return fileList;
        }
    }

    /**
     * 比较文件名
     */
    protected boolean compareFileName(String fileName1, String fileName2) {
        if (fileNamePattern != null) {
            return fileNamePattern.compare(fileName1, fileName2);
        }
        return false;
    }

    //日期由大到小排列
    class Comparable implements Comparator<File> {
        boolean reverse;

        public Comparable(boolean reverse) {
            this.reverse = reverse;
        }

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs != null && rhs != null) {
                if (reverse) {
                    return rhs.getName().compareTo(lhs.getName());
                } else {
                    return lhs.getName().compareTo(rhs.getName());
                }
            }
            return 0;
        }
    }

    /**
     * Will remove the directory passed as parameter if empty. After that, if the
     * parent is also becomes empty, remove the parent dir as well but at most 3
     * times.
     */
    private void removeFolderIfEmpty(File dir, int depth) {
        // we should never go more than 3 levels higher
        if (depth >= 3) {
            return;
        }
        if (dir.isDirectory() && FileFilterUtil.isEmptyDirectory(dir)) {
            dir.delete();
            removeFolderIfEmpty(dir.getParentFile(), depth + 1);
        }
    }

}
