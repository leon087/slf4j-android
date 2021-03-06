
简介
-----

- 为timber添加slf4j框架支持
- 支持FileTree、LogcatTree
- 支持setLevel方式自定义日志权限控制
- FileTree按天进行日志存储，输出文件名：yyyy-MM-dd_[$TAG].log
- 日志输入格式："%date %level5 [%logger:%thread:%method:%line] - msg"
- 每天日志自动压缩成tar.gz，并根据磁盘空间与日志留存时间自动清理

使用
-----

1. Add the JitPack repository to your build file  
```groovy
allprojects {
	repositories {
		maven { url 'https://www.jitpack.io' }
		jcenter()
	}
}
```

2. 添加依赖  
```groovy
//jitpack默认group为:com.github.{username}
compile '{group}:slf4j-android:{latest_version}'
```

3. 初始化与关闭：  
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

4. 添加日志：  
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
5. 混淆：

在项目中使用slf4j-android日志库时，需要在混淆脚本中加入以下配置：
```
-dontwarn org.apache.commons.compress.**
-keep class org.slf4j.** { *; }
```

TODO
---


License
---
Apache 2.0

