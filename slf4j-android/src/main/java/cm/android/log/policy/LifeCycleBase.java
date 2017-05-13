package cm.android.log.policy;

public class LifeCycleBase implements LifeCycle{

    protected boolean started = false;

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
