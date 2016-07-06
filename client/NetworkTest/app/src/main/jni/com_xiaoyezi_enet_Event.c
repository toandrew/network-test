//
// Created by Jim on 16/7/6.
//

#include <jni.h>

#include "enet/enet.h"
#include "com_xiaoyezi_enet_Event.h"

/*
 * Class:     com_xiaoyezi_enet_Event
 * Method:    sizeof
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_xiaoyezi_enet_Event_sizeof
    (JNIEnv *env, jclass cls) {
	return sizeof(ENetEvent);
}

/*
 * Class:     com_xiaoyezi_enet_Event
 * Method:    peer
 * Signature: (Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_xiaoyezi_enet_Event_peer
    (JNIEnv *env, jclass cls, jobject ctx) {
	ENetEvent *event = (*env)->GetDirectBufferAddress(env, ctx);
	if (event != NULL) {
		if (event->peer != NULL) {
			return (*env)->NewDirectByteBuffer(env, event->peer, sizeof(ENetPeer));
		}
	}

	return NULL;
}

/*
 * Class:     com_xiaoyezi_enet_Event
 * Method:    type
 * Signature: (Ljava/nio/ByteBuffer;)Lcom/xiaoyezi/enet/Event/Type;
 */
JNIEXPORT jobject JNICALL Java_com_xiaoyezi_enet_Event_type
    (JNIEnv *env, jclass cls, jobject ctx) {
	ENetEvent *event = (*env)->GetDirectBufferAddress(env, ctx);
	if (event != NULL) {
		jclass enumClass = (*env)->FindClass(env, "com/xiaoyezi/enet/Event$Type");
		if (enumClass == NULL) {
			return NULL;
		}

		jfieldID field = NULL;
		switch (event->type) {
		case ENET_EVENT_TYPE_CONNECT:
			field = (*env)->GetStaticFieldID(env, enumClass, "Connect", "Lcom/xiaoyezi/enet/Event$Type;");
			break;

		case ENET_EVENT_TYPE_DISCONNECT:
			field = (*env)->GetStaticFieldID(env, enumClass, "Disconnect", "Lcom/xiaoyezi/enet/Event$Type;");
			break;

		case ENET_EVENT_TYPE_NONE:
			field = (*env)->GetStaticFieldID(env, enumClass, "None", "Lcom/xiaoyezi/enet/Event$Type;");
			break;

		case ENET_EVENT_TYPE_RECEIVE:
			field = (*env)->GetStaticFieldID(env, enumClass, "Receive", "Lcom/xiaoyezi/enet/Event$Type;");
			break;
		}

		if (field != NULL) {
			return (*env)->GetStaticObjectField(env, enumClass, field);
		}
	}

	return NULL;
}

/*
 * Class:     com_xiaoyezi_enet_Event
 * Method:    channelID
 * Signature: (Ljava/nio/ByteBuffer;)B
 */
JNIEXPORT jbyte JNICALL Java_com_xiaoyezi_enet_Event_channelID
    (JNIEnv *env, jclass cls, jobject ctx) {
	ENetEvent *event = (*env)->GetDirectBufferAddress(env, ctx);
	if (event == NULL) {
		return 0;
	}

	return (jbyte) event->channelID;
}

/*
 * Class:     com_xiaoyezi_enet_Event
 * Method:    data
 * Signature: (Ljava/nio/ByteBuffer;)I
 */
JNIEXPORT jint JNICALL Java_com_xiaoyezi_enet_Event_data
    (JNIEnv *env, jclass cls, jobject ctx) {
	ENetEvent *event = (*env)->GetDirectBufferAddress(env, ctx);
	if (event == NULL) {
		return 0;
	}

	return event->data;
}

/*
 * Class:     com_xiaoyezi_enet_Event
 * Method:    packet
 * Signature: (Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_xiaoyezi_enet_Event_packet
    (JNIEnv *env, jclass cls, jobject ctx) {
	ENetEvent *event = (*env)->GetDirectBufferAddress(env, ctx);
	if (event != NULL) {
		if (event->packet != NULL) {
			return (*env)->NewDirectByteBuffer(env, event->packet, sizeof(ENetPacket));
		}
	}

	return NULL;
}