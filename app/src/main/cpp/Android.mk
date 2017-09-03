LOCAL_PATH := $(call my-dir)

MY_VAR_BZIP2_SRC := ${wildcard bzip2/*.c}

include $(CLEAR_VARS)

LOCAL_MODULE    := bsdiff
LOCAL_SRC_FILES := bdiffjni.c bsdiff.c $(MY_VAR_BZIP2_SRC)

include $(BUILD_SHARED_LIBRARY)



include $(CLEAR_VARS)

LOCAL_MODULE    := bspatch
LOCAL_SRC_FILES := bpatchjni.c bspatch.c $(MY_VAR_BZIP2_SRC)

include $(BUILD_SHARED_LIBRARY)
