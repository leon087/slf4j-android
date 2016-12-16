package cm.android.log.slf4j;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

import cm.android.log.timber.Level;
import timber.log.Timber;
import timber.log.Timber.Tree;

public class TimberLogger extends MarkerIgnoringBase {
    private static final long serialVersionUID = -1227274521521287937L;
    private static Level level = Level.ALL;

    TimberLogger(String tag) {
        this.name = tag;
    }

    public static void setLevel(Level level) {
        TimberLogger.level = level;
    }

    @Override
    public boolean isTraceEnabled() {
        return Level.TRACE.toInt() >= level.toInt();
    }

    @Override
    public void trace(String msg) {
        formatAndLog(Level.TRACE, msg, null, null);
    }

    @Override
    public void trace(String format, Object arg) {
        formatAndLog(Level.TRACE, format, new Object[]{arg}, null);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        formatAndLog(Level.TRACE, format, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void trace(String format, Object... argArray) {
        formatAndLog(Level.TRACE, format, argArray, null);
    }

    @Override
    public void trace(String msg, Throwable t) {
        formatAndLog(Level.TRACE, msg, null, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return Level.DEBUG.toInt() >= level.toInt();
    }

    @Override
    public void debug(String msg) {
        formatAndLog(Level.DEBUG, msg, null, null);
    }

    @Override
    public void debug(String format, Object arg) {
        formatAndLog(Level.DEBUG, format, new Object[]{arg}, null);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        formatAndLog(Level.DEBUG, format, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void debug(String format, Object... argArray) {
        formatAndLog(Level.DEBUG, format, argArray, null);
    }

    @Override
    public void debug(String msg, Throwable t) {
        formatAndLog(Level.DEBUG, msg, null, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return Level.INFO.toInt() >= level.toInt();
    }

    @Override
    public void info(String msg) {
        formatAndLog(Level.INFO, msg, null, null);
    }

    @Override
    public void info(String format, Object arg) {
        formatAndLog(Level.INFO, format, new Object[]{arg}, null);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(Level.INFO, format, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void info(String format, Object... argArray) {
        formatAndLog(Level.INFO, format, argArray, null);
    }

    @Override
    public void info(String msg, Throwable t) {
        formatAndLog(Level.INFO, msg, null, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return Level.WARN.toInt() >= level.toInt();
    }

    @Override
    public void warn(String msg) {
        formatAndLog(Level.WARN, msg, null, null);
    }

    @Override
    public void warn(String format, Object arg) {
        formatAndLog(Level.WARN, format, new Object[]{arg}, null);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(Level.WARN, format, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void warn(String format, Object... argArray) {
        formatAndLog(Level.WARN, format, argArray, null);
    }

    @Override
    public void warn(String msg, Throwable t) {
        formatAndLog(Level.WARN, msg, null, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return Level.ERROR.toInt() >= level.toInt();
    }

    @Override
    public void error(String msg) {
        formatAndLog(Level.ERROR, msg, null, null);
    }

    @Override
    public void error(String format, Object arg) {
        formatAndLog(Level.ERROR, format, new Object[]{arg}, null);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(Level.ERROR, format, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void error(String format, Object... argArray) {
        formatAndLog(Level.ERROR, format, argArray, null);
    }

    @Override
    public void error(String msg, Throwable t) {
        formatAndLog(Level.ERROR, msg, null, t);
    }

    private void formatAndLog(Level level, String messagePattern, Object[] argArray, Throwable t) {
        if (argArray == null && t != null) {
            argArray = new Object[]{t.getMessage()};
        }
        FormattingTuple ft = MessageFormatter.arrayFormat(messagePattern, argArray, t);
        log(level, ft.getMessage(), ft.getThrowable());
    }

    private void log(Level level, String message, Throwable throwable) {
        Tree tree = Timber.tag(name);

        switch (level.toInt()) {
            case Level.TRACE_INT:
                trace(tree, message, throwable);
                break;

            case Level.DEBUG_INT:
                debug(tree, message, throwable);
                break;

            case Level.INFO_INT:
                info(tree, message, throwable);
                break;

            case Level.WARN_INT:
                warn(tree, message, throwable);
                break;

            case Level.ERROR_INT:
                error(tree, message, throwable);
                break;
            default:
                wtf(tree, message, throwable);
                break;
        }
    }

    private void trace(Tree tree, String message, Throwable throwable) {
        if (isTraceEnabled()) {
            if (throwable != null) {
                tree.v(throwable, message);
            } else {
                tree.v(message);
            }
        }
    }

    private void debug(Tree tree, String message, Throwable throwable) {
        if (isDebugEnabled()) {
            if (throwable != null) {
                tree.d(throwable, message);
            } else {
                tree.d(message);
            }
        }
    }

    private void info(Tree tree, String message, Throwable throwable) {
        if (isInfoEnabled()) {
            if (throwable != null) {
                tree.i(throwable, message);
            } else {
                tree.i(message);
            }
        }
    }

    private void warn(Tree tree, String message, Throwable throwable) {
        if (isWarnEnabled()) {
            if (throwable != null) {
                tree.w(throwable, message);
            } else {
                tree.w(message);
            }
        }
    }

    private void error(Tree tree, String message, Throwable throwable) {
        if (isErrorEnabled()) {
            if (throwable != null) {
                tree.e(throwable, message);
            } else {
                tree.e(message);
            }
        }
    }

    private void wtf(Tree tree, String message, Throwable throwable) {
        if (throwable != null) {
            tree.wtf(throwable, message);
        } else {
            tree.wtf(message);
        }
    }
}
