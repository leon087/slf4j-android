package cm.android.log.policy;

abstract class RollingPolicyBase extends LifeCycleBase implements RollingPolicy {
    private PolicyManager parent;

    public String getParentsRawFileProperty() {
        return parent.rawFileProperty();
    }

    public PolicyManager getParent() {
        return parent;
    }

    @Override
    public void setParent(PolicyManager engine) {
        this.parent = engine;
    }
}
