package cm.android.log.policy;

public class CmLogException extends RuntimeException {

    public CmLogException(String msg) {
        super(msg);
    }

    public CmLogException(String msg, Throwable nested) {
        super(msg, nested);
    }

}
