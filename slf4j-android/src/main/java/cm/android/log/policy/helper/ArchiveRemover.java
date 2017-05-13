package cm.android.log.policy.helper;

import java.util.concurrent.Future;

public interface ArchiveRemover {
    /**
     * 同步清理数据
     */
    void clean();
    /**
     * 设置最大文件个数
     */
    void setMaxHistory(int history);
    /**
     * 设置磁盘有效大小
     */
    void setTotalSizeCap(long totalSizeCap);
    /**
     * 异步删除文件
     */
    Future<?> cleanAsynchronously(String activeFile);
}
