LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := xenet_jni
LOCAL_C_INCLUDES := $(LOCAL_PATH)/enet/include
LOCAL_SRC_FILES := com_xiaoyezi_enet_Host.c
LOCAL_STATIC_LIBRARIES +=  xenet
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_CFLAGS := -DHAS_SOCKLEN_T
LOCAL_C_INCLUDES := $(LOCAL_PATH)/enet/include

LOCAL_MODULE := xenet
LOCAL_SRC_FILES :=\
	enet/callbacks.c \
	enet/compress.c \
	enet/host.c \
	enet/list.c \
	enet/packet.c \
	enet/peer.c \
	enet/protocol.c \
	enet/unix.c

LOCAL_LDLIBS :=

include $(BUILD_STATIC_LIBRARY)
