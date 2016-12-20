package cm.android.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.database.Cursor;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

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

    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (TextUtils.isEmpty(padStr)) {
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
}