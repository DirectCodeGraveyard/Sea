package org.directcode.neo.sea;

interface SeaController {
    boolean isLoaded(String name);
    void load(String name);
    void unload(String name);
    List<String> loadedModules();
    List<String> modules();
}
