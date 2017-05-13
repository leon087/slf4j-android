package cm.android.log;

public class LogConstants {
    // TODO: lyd 2017/4/14: 以下可配置
    //保留历史记录7天
    public static final int INFINITE_HISTORY = 7;

    //保留文件大小
    public static final int MAX_SPACE_SIZE = 50 * 1024 * 1024;

    //文件后缀
    public static final String FILE_EXT = ".log";

    //压缩文件后缀
    public static final String FILE_GZ = ".gz";

    //tar文件后缀
    public static final String FILE_TAR = ".tar";

    //日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    //冒号分隔符
    public static final String SPLIT_COLON = ":";

    //加号分隔符
    public static final String SPLIT_PLUS = "+";

    //下划线分隔符
    public static final String SPLIT_UNDERLINE = "_";

    //是否在开始的时候清理
    public static final boolean CLEAN_HISTORY_ONSTART = true;

    public static final int MILLIS_IN_ONE_SECOND = 1000;
    public static final int MILLIS_IN_ONE_MINUTE = MILLIS_IN_ONE_SECOND * 60;
    public static final int MILLIS_IN_ONE_HOUR = MILLIS_IN_ONE_MINUTE * 60;
    public static final int MILLIS_IN_ONE_DAY = MILLIS_IN_ONE_HOUR * 24;
    public static final int MILLIS_IN_ONE_WEEK = MILLIS_IN_ONE_DAY * 7;

    /**
     * The number of seconds to wait for compression jobs to finish.
     */
    public static final int SECONDS_TO_WAIT_FOR_COMPRESSION_JOBS = 30;

    public static final String SEE_FNP_NOT_SET = "not set FileNamePattern";

}
