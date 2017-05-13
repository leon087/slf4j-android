package cm.android.log.policy.helper;

/**
 * 日期格式的枚举类型
 */
public enum PeriodicityType {

    ERRONEOUS, TOP_OF_MILLISECOND, TOP_OF_SECOND, TOP_OF_MINUTE, TOP_OF_HOUR, HALF_DAY, TOP_OF_DAY, TOP_OF_WEEK, TOP_OF_MONTH;

    static PeriodicityType[] VALID_ORDERED_LIST = new PeriodicityType[]{
            TOP_OF_MILLISECOND, PeriodicityType.TOP_OF_SECOND,
            PeriodicityType.TOP_OF_MINUTE, PeriodicityType.TOP_OF_HOUR,
            PeriodicityType.TOP_OF_DAY, PeriodicityType.TOP_OF_WEEK,
            PeriodicityType.TOP_OF_MONTH};

}
