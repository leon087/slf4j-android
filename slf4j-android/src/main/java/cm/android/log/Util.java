package cm.android.log;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

/**
 * IO读写Util类
 */
public class Util {

    private static final int BUF_SIZE = 8 * 1024;

    private static final Logger logger = LoggerFactory.getLogger("util");

    private Util() {
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if
     * 'closeable' is null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (RuntimeException rethrown) {
            throw rethrown;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

//    public static void closeQuietly(Cursor cursor) {
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//    }

    private static SimpleDateFormat getFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    public static String formatDate(String formatStr, long time) {
        SimpleDateFormat dateFormat = getFormat(formatStr);
        return dateFormat.format(new Date(time));
    }

    public static boolean checkDirectory(File file) {
        if (file == null) {
            throw new IllegalStateException("file == null");
        }

        if (file.exists()) {
            if (!file.isDirectory()) {
                logger.error(file.getAbsolutePath() + " already exists and is not a directory");
                return false;
            }
        } else {
            if (!file.mkdirs()) {
                logger.error("Unable to create directory: " + file.getAbsolutePath());
                return false;
            }
        }
        return true;
    }

    public static void addError(String msg, Throwable t) {
        android.util.Log.e("ggg", "addError:msg = " + msg);
        t.printStackTrace();
    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        // if (padLen == 1 && pads <= PAD_LIMIT) {
        // return rightPad(str, size, padStr.charAt(0));
        // }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * gz文件
     */
    public static boolean isGzFile(File file) {
        if (file != null && file.getName() != null) {
            return file.getName().endsWith(LogConstants.FILE_GZ);
        }
        return false;
    }

    /**
     * log文件
     */
    public static boolean isLogFile(File file) {
        if (file != null && file.getName() != null) {
            return file.getName().endsWith(LogConstants.FILE_EXT);
        }
        return false;
    }

    //    /**
//     * 多个文件压缩成gzip文件
//     *
//     * @param files         需要压缩的文件列表
//     * @param gzFile        压缩的文件名
//     * @param deleteWhenZip 是否在压缩后删除文件
//     */
//    public static void multiFileToGzip(List<File> files, File gzFile, boolean deleteWhenZip) {
//        TarArchiveOutputStream tarOutput = null;
//        BufferedInputStream bin = null;
//        File tarFile = new File(gzFile.getParentFile(), gzFile.getName().substring(0, gzFile.getName().indexOf(".")) + ".tar");
//        try {
//            FileOutputStream gzFileOutput = new FileOutputStream(tarFile);
//            tarOutput = new TarArchiveOutputStream(new BufferedOutputStream(gzFileOutput));
//            for (File file : files) {
//                String fileName = file.getName();
//                if (tarFile.getName().equals(fileName)) {
//                    continue;
//                }
//                if (fileName.contains(LogConstants.SPLIT_COLON)) {
//                    fileName = fileName.replaceAll(LogConstants.SPLIT_COLON, LogConstants.SPLIT_PLUS);
//                    File newFile = new File(file.getParentFile(), fileName);
//                    file.renameTo(newFile);
//                    file = newFile;
//                }
//                TarArchiveEntry entry = new TarArchiveEntry(file.getName());
//                entry.setSize(file.length());
//                tarOutput.putArchiveEntry(entry);
//
//                bin = new BufferedInputStream(new FileInputStream(file));
//                int count;
//                byte data[] = new byte[BUF_SIZE];
//                while ((count = bin.read(data)) != -1) {
//                    tarOutput.write(data, 0, count);
//                }
//                tarOutput.flush();
//                tarOutput.closeArchiveEntry();
//                bin.close();
//                if (deleteWhenZip) {
//                    deleteInternal(file);
//                }
//            }
//        } catch (Exception e) {
//            addError("multiple File to Gzip error while tar log", e);
//        } finally {
//            closeQuietly(tarOutput);
//            closeQuietly(bin);
//        }
//        compress(tarFile, new File(tarFile.getPath() + LogConstants.FILE_GZ));
//        deleteInternal(tarFile);
//    }

    /**
     * 获取缓存cache
     */
    public static File getExternalCacheDir() {
//        context.getCacheDir().getAbsolutePath()
        File externalFilesDir = new File(System.getProperty("java.io.tmpdir", "."));
        checkDirectory(externalFilesDir);
        return externalFilesDir;
    }

    /**
     * 多个文件压缩成gzip文件
     *
     * @param files  需要压缩的文件列表
     * @param gzFile 压缩的文件名
     */
    public static void multiFileToGzip(List<File> files, File gzFile) throws IOException {
        BufferedInputStream bin = null;
        TarArchiveOutputStream tarOutput = null;
        File tarFile = new File(getExternalCacheDir(), gzFile.getName().substring(0, gzFile.getName().indexOf(".")) + ".tar");
        try {
            tarOutput = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(tarFile)));
            for (File file : files) {
                String fileName = file.getName();
                if (tarFile.getName().equals(fileName)) {
                    continue;
                }
                if (fileName.contains(LogConstants.SPLIT_COLON)) {
                    fileName = fileName.replaceAll(LogConstants.SPLIT_COLON, LogConstants.SPLIT_PLUS);
                    File newFile = new File(file.getParentFile(), fileName);
                    file.renameTo(newFile);
                    file = newFile;
                }
                TarArchiveEntry entry = new TarArchiveEntry(file.getName());
                entry.setSize(file.length());
                tarOutput.putArchiveEntry(entry);

                bin = new BufferedInputStream(new FileInputStream(file));

                //write
                write(bin, tarOutput, BUF_SIZE);

                tarOutput.flush();
                tarOutput.closeArchiveEntry();
            }
        } catch (IOException e) {
            addError("multiple File to Gzip error while tar log", e);
            throw e;
        } finally {
            closeQuietly(bin);
            closeQuietly(tarOutput);
        }

        compress(tarFile, gzFile);
        deleteInternal(tarFile);
    }

    /**
     * 压缩tar文件到gz
     */
    public static boolean compress(File srcFile, File destFile) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(new FileInputStream(srcFile));
            os = new BufferedOutputStream(new FileOutputStream(destFile));

            return compress(is, os);
        } catch (FileNotFoundException e) {
            addError("file not found error while compress tar to gz", e);
            return false;
        } catch (IOException e) {
            addError("io error while compress tar to gz", e);
            return false;
        } finally {
            closeQuietly(is);
            closeQuietly(os);
        }
    }

    public static boolean compress(InputStream is, OutputStream os) throws IOException {
        GZIPOutputStream gos = new GZIPOutputStream(os);
        write(is, gos);
        gos.finish();
        return true;
    }

    /**
     * 将{@link java.io.InputStream}中内容写入{@link java.io.OutputStream}
     */
    public static void write(InputStream inputStream, OutputStream outputStream, int bufSize)
            throws IOException {
        int count = -1;
        byte[] buffer = new byte[bufSize];
        while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
    }

    public static void write(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        write(inputStream, outputStream, BUF_SIZE);
    }

    private static boolean deleteInternal(File file) {
        try {
            if (file != null && file.exists()) {
                return file.delete();
            }
        } catch (SecurityException se) {
            addError("Security error while delete file path=" + file.getAbsolutePath(), se);
            return false;
        }
        return false;
    }

    /**
     * 删除文件list
     */
    public static void deleteListFile(List<File> listList) {
        Iterator<File> iterator = listList.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            deleteInternal(file);
        }
    }
}