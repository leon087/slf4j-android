package cm.android.log.timber;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cm.android.log.Util;

public class Formatter {
    public static String generateTag(StackTraceElement caller) {
        String tag = "%s:%s:%s:%d";
//        String callerFileName = caller.getFileName();
        String className = getClassName(caller);
        return String.format(Locale.getDefault(), tag, Thread.currentThread().getName(), className, caller.getMethodName(), caller.getLineNumber());
    }

    private static final int CALL_STACK_INDEX = 10;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    private static String getClassName(StackTraceElement element) {
        String tag = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1);
    }

    private static String getThread() {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();

        if (stackTrace.length <= CALL_STACK_INDEX) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        return generateTag(stackTrace[CALL_STACK_INDEX]);
    }

    public static String formatTag(String tag) {
        String format = "[%s:%s]";
        return String.format(Locale.getDefault(), format, tag, Formatter.getThread());
    }

    public static String format(String level, String msg) {
//        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
//        android.util.Log.e("hhhhhhh", "hhhhhhh begin " + msg);
//        for (StackTraceElement stack : stackTrace) {
//            android.util.Log.e("hhhhhhh", "hhhhhhh " + stack.getClassName() + "," + stack.getMethodName());
//        }
//        android.util.Log.e("hhhhhhh", "hhhhhhh end");

//        String format = "%date %level5 [%logger:%thread:%method:%line] - msg";
        String format = "%s %s %s";
        String data = Util.formatDate("yyyy-MM-dd HH:mm:ss.SSS", System.currentTimeMillis());
        return String.format(Locale.getDefault(), format, data, level, msg);
    }

}
