package org.neo.sea.core;

interface IRemoteModule {
    String name();
    void load();
    void unload();
}
