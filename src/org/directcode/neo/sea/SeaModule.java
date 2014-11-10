package org.directcode.neo.sea;

public abstract class SeaModule {
    public abstract String name();
    public abstract void load(Sea sea);
    public abstract void unload(Sea sea);
}
