LOCAL_PATH := $(call my-dir)
MAIN_LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := qcomm
LOCAL_CFLAGS := -w -s -Wno-error=format-security -fvisibility=hidden -fpermissive -fexceptions
LOCAL_CPPFLAGS := -w -s -Wno-error=format-security -fvisibility=hidden -Werror -std=c++17
LOCAL_CPPFLAGS += -Wno-error=c++11-narrowing -fpermissive -Wall -fexceptions
LOCAL_CFLAGS += -mllvm -split_num=6 -mllvm -sub_loop=2 -mllvm -bcf_loop=2 -mllvm -bcf_prob=100 -mllvm -sobf
LOCAL_LDFLAGS += -Wl,--gc-sections,--strip-all,-llog
LOCAL_LDLIBS := -llog -landroid -lz
LOCAL_ARM_NEON := true
LOCAL_ARM_MODE := arm

LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)
LOCAL_SRC_FILES := src/main.cpp \
	src/KittyMemory/KittyMemory.cpp \
	src/KittyMemory/MemoryPatch.cpp \
    src/KittyMemory/MemoryBackup.cpp \
    src/KittyMemory/KittyUtils.cpp \
    src/AES.cpp \
    src/base64.cpp

include $(BUILD_SHARED_LIBRARY)
