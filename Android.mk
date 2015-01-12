LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_SRC_FILES += \
  src/org/neo/sea/SeaController.aidl \
  src/org/neo/sea/IRemoteModule.aidl

LOCAL_SDK_VERSION := current
LOCAL_PACKAGE_NAME := Sea

include $(BUILD_PACKAGE)
