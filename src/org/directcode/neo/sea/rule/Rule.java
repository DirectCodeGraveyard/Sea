package org.directcode.neo.sea.rule;

public class Rule {
    private final int id;
    private final String name;
    private final String triggerId;
    private final String actionId;

    public Rule(int id, String name, String triggerId, String actionId) {
        this.id = id;
        this.name = name;
        this.triggerId = triggerId;
        this.actionId = actionId;
    }

    public String getName() {
        return name;
    }

    public String getActionId() {
        return actionId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public int getId() {
        return id;
    }
}
