package cm.android.log.policy.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import cm.android.log.LogConstants;
import cm.android.log.policy.DefaultBasedPolicy;

/**
 * RollingCalendar is a helper class to
 * {@link DefaultBasedPolicy } or similar
 * timed-based rolling policies. Given a periodicity type and the current time,
 * it computes the start of the next interval (i.e. the triggering date).
 */
public class RollingCalendar extends GregorianCalendar {

    private static final long serialVersionUID = -5937537740925066161L;

    // The gmtTimeZone is used only in computeCheckPeriod() method.
    static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

    private PeriodicityType periodicityType = PeriodicityType.ERRONEOUS;

    public RollingCalendar() {
        super();
    }

    public RollingCalendar(TimeZone tz, Locale locale) {
        super(tz, locale);
    }

    public void init(String datePattern) {
        periodicityType = computePeriodicityType(datePattern);
    }

    private void setPeriodicityType(PeriodicityType periodicityType) {
        this.periodicityType = periodicityType;
    }

    public PeriodicityType getPeriodicityType() {
        return periodicityType;
    }

    public long getNextTriggeringMillis(Date now) {
        return getNextTriggeringDate(now).getTime();
    }

    /**
     * 根据日期格式确定类型
     * @param datePattern
     * @return
     */
    public PeriodicityType computePeriodicityType(String datePattern) {
        RollingCalendar rollingCalendar = new RollingCalendar(GMT_TIMEZONE, Locale
                .getDefault());

        // set sate to 1970-01-01 00:00:00 GMT
        Date epoch = new Date(0);

        if (datePattern != null) {
            for (PeriodicityType i : PeriodicityType.VALID_ORDERED_LIST) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                simpleDateFormat.setTimeZone(GMT_TIMEZONE); // all date formatting done
                // in GMT

                String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setPeriodicityType(i);

                Date next = new Date(rollingCalendar.getNextTriggeringMillis(epoch));
                String r1 = simpleDateFormat.format(next);

                // System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);
                if ((r0 != null) && (r1 != null) && !r0.equals(r1)) {
                    return i;
                }
            }
        }
        return PeriodicityType.ERRONEOUS;
    }

    public long periodsElapsed(long start, long end) {
        if (start > end)
            throw new IllegalArgumentException("Start cannot come before end");

        long diff = end - start;
        switch (periodicityType) {

            case TOP_OF_MILLISECOND:
                return diff;
            case TOP_OF_SECOND:
                return diff / LogConstants.MILLIS_IN_ONE_SECOND;
            case TOP_OF_MINUTE:
                return diff / LogConstants.MILLIS_IN_ONE_MINUTE;
            case TOP_OF_HOUR:
                return (int) diff / LogConstants.MILLIS_IN_ONE_HOUR;
            case TOP_OF_DAY:
                return diff / LogConstants.MILLIS_IN_ONE_DAY;
            case TOP_OF_WEEK:
                return diff / LogConstants.MILLIS_IN_ONE_WEEK;
            case TOP_OF_MONTH:
                return diffInMonths(start, end);
            default:
                throw new IllegalStateException("Unknown periodicity type.");
        }
    }

    public static int diffInMonths(long startTime, long endTime) {
        if (startTime > endTime)
            throw new IllegalArgumentException("startTime cannot be larger than endTime");
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startTime);
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(endTime);
        int yearDiff = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        int monthDiff = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
        return yearDiff * 12 + monthDiff;
    }

    public Date getRelativeDate(Date now, int periods) {
        this.setTime(now);

        switch (periodicityType) {
            case TOP_OF_MILLISECOND:
                this.add(Calendar.MILLISECOND, periods);
                break;

            case TOP_OF_SECOND:
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.SECOND, periods);
                break;

            case TOP_OF_MINUTE:
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MINUTE, periods);
                break;

            case TOP_OF_HOUR:
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.HOUR_OF_DAY, periods);
                break;

            case TOP_OF_DAY:
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.DATE, periods);
                break;

            case TOP_OF_WEEK:
                this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.WEEK_OF_YEAR, periods);
                break;

            case TOP_OF_MONTH:
                this.set(Calendar.DATE, 1);
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MONTH, periods);
                break;

            default:
                throw new IllegalStateException("Unknown periodicity type.");
        }

        return getTime();
    }

    public Date getNextTriggeringDate(Date now) {
        return getRelativeDate(now, 1);
    }
}
