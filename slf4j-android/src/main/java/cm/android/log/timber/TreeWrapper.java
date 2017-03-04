package cm.android.log.timber;

import timber.log.Timber;

public abstract class TreeWrapper extends Timber.Tree {
    protected Level level = Level.ALL;

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    protected boolean isLoggable(String tag, int priority) {
        if (Level.convert(priority).toInt() >= level.toInt()) {
            return true;
//            return Log.isLoggable(tag, priority);
        } else {
            return false;
        }
    }
}
