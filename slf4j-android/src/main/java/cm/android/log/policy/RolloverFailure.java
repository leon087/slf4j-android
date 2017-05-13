package cm.android.log.policy;

/**
 * A RolloverFailure occurs if, for whatever reason a rollover fails.
 */
public class RolloverFailure extends CmLogException {

  public RolloverFailure(String msg) {
    super(msg);
  }

  public RolloverFailure(String message, Throwable cause) {
    super(message, cause);
  }
}
