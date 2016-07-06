//
// Created by Jim on 16/7/5.
//

#include <jni.h>

#include "enet/enet.h"
#include "com_xiaoyezi_enet_Host.h"

#ifdef DEBUG
#define debug(fmt,args...) printf(fmt, ##args)
#else
#define debug(fmt,args...)
#endif

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    create
 * Signature: (IIIIII)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_xiaoyezi_enet_Host_create
        (JNIEnv *env, jclass cls, jint address, jint port, jint peerCount, jint channelCount,
         jint inbw, jint outbw) {

    const ENetAddress addr = {(enet_uint32) address, (enet_uint16) port};
    debug("create addr: { %08x, %d }\n", addr.host, addr.port);
    ENetHost *host = enet_host_create(&addr, peerCount, channelCount, inbw, outbw);
    if (host == NULL) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "com/xiaoyezi/enet/EnetException"),
                         "failed to create enet host");
        return NULL;
    }

    return (*env)->NewDirectByteBuffer(env, host, sizeof(ENetHost));
}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    connect
 * Signature: (Ljava/nio/ByteBuffer;IIII)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_xiaoyezi_enet_Host_connect
        (JNIEnv *env, jclass cls, jobject ctx, jint address, jint port, jint channelCount, jint data) {

    return NULL;
}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    broadcast
 * Signature: (Ljava/nio/ByteBuffer;ILjava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_com_xiaoyezi_enet_Host_broadcast
        (JNIEnv *env, jclass cls, jobject ctx, jint channel, jobject packet) {

}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    channel_limit
 * Signature: (Ljava/nio/ByteBuffer;I)V
 */
JNIEXPORT void JNICALL Java_com_xiaoyezi_enet_Host_channel_1limit
        (JNIEnv *env, jclass cls, jobject ctx, jint channelLimit) {

}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    bandwidth_limit
 * Signature: (Ljava/nio/ByteBuffer;II)V
 */
JNIEXPORT void JNICALL Java_com_xiaoyezi_enet_Host_bandwidth_1limit
        (JNIEnv *env, jclass cls, jobject ctx, jint inbw, jint outbw) {

}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    flush
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_com_xiaoyezi_enet_Host_flush
        (JNIEnv *env, jclass cls, jobject ctx) {

}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    checkEvents
 * Signature: (Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_com_xiaoyezi_enet_Host_checkEvents
        (JNIEnv *env, jclass cls, jobject ctx, jobject ev) {

}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    service
 * Signature: (Ljava/nio/ByteBuffer;ILjava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_com_xiaoyezi_enet_Host_service
        (JNIEnv *env, jclass cls, jobject ctx, jint timeout, jobject ev) {

}

/*
 * Class:     com_xiaoyezi_enet_Host
 * Method:    destroy
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_com_xiaoyezi_enet_Host_destroy
        (JNIEnv *env, jclass cls, jobject ctx) {

}