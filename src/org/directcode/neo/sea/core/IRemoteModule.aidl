package org.directcode.neo.sea;

interface IRemoteModule {
    String name();
    void load();
    void unload();
}
