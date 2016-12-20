package cm.android.app.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Random;

import cm.android.log.LogManager;
import cm.android.log.timber.FileTree;
import cm.android.log.timber.Level;
import cm.android.log.timber.LogcatTree;

public class MainActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger("hhhh");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File dir = Environment.getExternalStoragePublicDirectory(this.getPackageName());
        FileTree fileTree = new FileTree(dir);
        LogcatTree logcatTree = new LogcatTree();

        boolean flag = new Random().nextBoolean();
        android.util.Log.e("hhhhhh", "hhhhhh flag = " + flag);

        Level level = Level.ALL;
        if (flag) {
            level = Level.TRACE;
        } else {
            level = Level.DEBUG;
        }

        LogManager.setLevel(level);
        LogManager.initTree(fileTree, logcatTree);

        logger.error("hhhhhhh dir:" + dir);
        logger.error("hhhhhhh dir:{}", dir);

        logger.error("hhh hello ggg", new UnknownHostException("hhhh:UnknownHostException"));
        logger.error("Hello ggg", new Exception("hhh exception t"));
        logger.error("hhhhhhh {}", "onCreate");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                android.util.Log.e("hhh", "hhh shutdown...");

                LogManager.shutdown();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.util.Log.e("hhh", "hhh### test");
                logger.error("hhh############################## test");
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        logger.error("hhhhhhh {}", "onDestroy");
        LogManager.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        logger.trace("hhhhhhh trace:{}", "onResume");
        logger.debug("hhhhhhh debug:{}", "onResume");
        logger.info("hhhhhhh info:{}", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.trace("hhhhhhh trace:{}", "onPause");
        logger.debug("hhhhhhh debug:{}", "onPause");
        logger.info("hhhhhhh info:{}", "onPause");
    }
}
