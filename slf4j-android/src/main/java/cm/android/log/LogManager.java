package cm.android.log;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

import cm.android.log.policy.PolicyManager;
import cm.android.log.slf4j.TimberLogger;
import cm.android.log.timber.Level;
import timber.log.Timber;

public class LogManager {
    public static void shutdown() {
        List<Timber.Tree> trees = Timber.forest();
        Iterator<Timber.Tree> it = trees.iterator();
        while (it.hasNext()) {
            Timber.Tree tree = it.next();
            if (tree instanceof Closeable) {
                Util.closeQuietly((Closeable) tree);
            }
        }
        Timber.uprootAll();

        PolicyManager.getInstance().stop();
    }

    public static void initTree(Timber.Tree... trees) {
        Timber.uprootAll();
        Timber.plant(trees);

        PolicyManager.getInstance().start();
    }

    public static void setLevel(Level level) {
        TimberLogger.setLevel(level);
    }
}
