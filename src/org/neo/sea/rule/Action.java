package org.neo.sea.rule;

public abstract class Action {
    private final String id;

    public Action(String id) {
        this.id = id;
    }

    public abstract void invoke();

    public String getId() {
        return id;
    }
}
