package com.android.systemui;

interface SystemUIProxy {
    boolean getBatteryIconPercentage();
    void setBatteryIconPercentage(boolean enabled);
}
