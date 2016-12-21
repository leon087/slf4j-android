
简介
-----

- 为timber添加slf4j框架支持
- 支持FileTree、LogcatTree
- 支持setLevel方式自定义日志权限控制
- FileTree按天进行日志存储，输出文件名：yyyy-MM-dd.log
- 日志输入格式："%date %level5 [%logger:%thread:%method:%line] - msg"

使用
-----
1. Add the JitPack repository to your build file  
```groovy
allprojects {
	repositories {
		maven { url 'https://www.jitpack.io' }
	}
}
```
2. 添加依赖  
```groovy
    compile 'com.github.leon087:slf4j-android:+'
```

3. 添加其他依赖  
```groovy
compile "org.slf4j:slf4j-api:1.7.22"
compile 'com.jakewharton.timber:timber:4.4.0'
```

初始化与关闭：  
```java
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
```

添加日志：  
```java
public class Demo {
    private static final Logger logger = LoggerFactory.getLogger("hhhh");

    public void method() {
        logger.trace("ggg trace:{}", "ggg");
        logger.debug("ggg debug:{}", "ggg");
        logger.info("ggg info:{}", "ggg");
        logger.error("ggg {}", "ggg", new Exception("hhh exception"));
    }
}
```

License
---
Apache 2.0

