package cm.android.log.slf4j;

import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import timber.log.Timber;
import timber.log.Timber.Tree;

class TimberLoggerAdapter extends MarkerIgnoringBase {

    private static final long serialVersionUID = -7141877940268893698L;

    TimberLoggerAdapter(String tag) {
        this.name = tag;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
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
        return true;
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
        return true;
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
        return true;
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
        return true;
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
        FormattingTuple ft = MessageFormatter.arrayFormat(messagePattern, argArray, t);
        log(level, ft.getMessage(), ft.getThrowable());
    }

    private void log(org.slf4j.event.Level level, String message, Throwable throwable) {
        Tree tree = Timber.tag(name);

        switch (level.toInt()) {
            case LocationAwareLogger.TRACE_INT:
                if (throwable != null) {
                    tree.v(throwable, message);
                } else {
                    tree.v(message);
                }
                break;

            case LocationAwareLogger.DEBUG_INT:
                if (throwable != null) {
                    tree.d(throwable, message);
                } else {
                    tree.d(message);
                }
                break;

            case LocationAwareLogger.INFO_INT:
                if (throwable != null) {
                    tree.i(throwable, message);
                } else {
                    tree.i(message);
                }
                break;

            case LocationAwareLogger.WARN_INT:
                if (throwable != null) {
                    tree.w(throwable, message);
                } else {
                    tree.w(message);
                }
                break;

            case LocationAwareLogger.ERROR_INT:
                if (throwable != null) {
                    tree.e(throwable, message);
                } else {
                    tree.e(message);
                }
                break;

            default:
                if (throwable != null) {
                    tree.wtf(throwable, message);
                } else {
                    tree.wtf(message);
                }

                break;
        }
    }
}
