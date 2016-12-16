package cm.android.app.test;

import android.os.Environment;

import java.io.File;

import cm.android.log.LogManager;
import cm.android.log.timber.FileTree;
import cm.android.log.timber.Level;
import cm.android.log.timber.LogcatTree;

public class App {
    /**
     * 初始化
     */
    public void init() {
        File dir = Environment.getExternalStoragePublicDirectory("log");
        FileTree fileTree = new FileTree(dir);
        LogcatTree logcatTree = new LogcatTree();

        LogManager.setLevel(Level.ALL);
        LogManager.initTree(fileTree, logcatTree);
    }

    /**
     * 退出日志模块
     */
    public void deInit() {
        LogManager.shutdown();
    }
}
