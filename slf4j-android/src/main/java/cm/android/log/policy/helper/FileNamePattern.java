package cm.android.log.policy.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNamePattern {
    private String pattern;
    private SimpleDateFormat format;
    static Map<String, String> regexMap = new HashMap<>();

    static {
        regexMap.put("yyyy-MM-dd HH:mm:ss", "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
        regexMap.put("yyyy-MM-dd HH:mm", "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$");//2014-03-12 12:05
        regexMap.put("yyyy-MM-dd HH", "^\\d{4}-\\d{2}-\\d{2} \\d{2}$");//2014-03-12 12
        regexMap.put("yyyy-MM-dd", "(\\d{4}-\\d{2}-\\d{2})"); //2014-03-12
        regexMap.put("yyyy-MM", "^\\d{4}-\\d{2}$");//2014-03
        regexMap.put("yyyy", "^\\d{4}$");//2014

        regexMap.put("yyyyMMddHHmmss", "^\\d{14}$");//20140312120534
        regexMap.put("yyyyMMddHHmm", "^\\d{12}$");//201403121205
        regexMap.put("yyyyMMddHH", "^\\d{10}$");//2014031212
        regexMap.put("yyyyMMdd", "^\\d{8}$");//20140312
        regexMap.put("yyyyMM", "^\\d{6}$");//201403
    }

    public FileNamePattern(String patternArg) {
        setPattern(FileFilterUtil.slashify(patternArg));
    }

    public void setPattern(String pattern) {
        if (pattern != null) {
            this.pattern = pattern.trim();
            this.format = new SimpleDateFormat(pattern);
        }
    }

    public String getPattern() {
        return pattern;
    }

    public String convert(Date o) {
        if (format != null) {
            return format.format(o);
        }
        return o.toString();
    }

    public String convert(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Date) {
            return convert((Date) o);
        }
        throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
    }

    /**
     * 获取format对象的匹配规则
     */
    public Pattern getFormatPattern(String format) {
        String regex = regexMap.get(format);
        return Pattern.compile(regex);
    }

    public Pattern getDefaultFormatPattern() {
        return getFormatPattern(pattern);
    }

    /**
     * 名字中间是否有相同的时间格式的串
     */
    public boolean compare(String name1, String name2) {
        if (pattern != null) {
            String regex = regexMap.get(pattern);
            if (regex != null) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(name1);
                if (matcher.find()) {
                    return name2.contains(matcher.group());
                }
            }
        }
        return false;
    }

    /**
     * 是否相同的文件名(不带有后缀)
     */
    public boolean isSameFileNameWithOutSuffix(String name1, String name2) {
        if (name1 == null || name2 == null) {
            return false;
        }
        int index = name1.indexOf(".");
        if (index != -1) {
            name1 = name1.substring(0, index);
        }
        index = name2.indexOf(".");
        if (index != -1) {
            name2 = name2.substring(0, index);
        }
        return name1.endsWith(name2);
    }
}
