package cm.android.log.policy;

public interface LifeCycle {
  void start();
  void stop();
  boolean isStarted();
}
