package org.directcode.neo.sea;

public abstract class Module {
    public abstract String name();
    public abstract void load();
    public abstract void unload();
}
