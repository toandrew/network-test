package com.xiaoyezi.enet;

import android.util.Log;

import com.xiaoyezi.tools.networktest.utils.Constants;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

public class Host {
    private static final String TAG = "Host";

    ByteBuffer mNativeState;

    static int addressToInt(InetAddress address) throws EnetException {
        if (!(address instanceof Inet4Address)) {
            throw new EnetException("enet only supports IPv4");
        }

        ByteBuffer buf = ByteBuffer.wrap(address.getAddress());
        buf.order(ByteOrder.nativeOrder());
        return buf.getInt(0);
    }

    public Host(InetSocketAddress address, int peerCount, int channelLimit, int incomingBandwidth, int outgoingBandwidth)
            throws EnetException {
        if (address == null) {
            mNativeState = create(0, 0, peerCount, channelLimit, incomingBandwidth, outgoingBandwidth);
            return;
        }

        mNativeState = create(addressToInt(address.getAddress()), address.getPort(), peerCount, channelLimit, incomingBandwidth, outgoingBandwidth);
    }

    public Peer connect(InetSocketAddress address, int channelCount, int data)
            throws EnetException {
        return new Peer(connect(mNativeState, addressToInt(address.getAddress()), address.getPort(), channelCount, data));
    }

    public void broadcast(int channelID, Packet packet) {
        broadcast(mNativeState, channelID, packet.nativeState);
    }

    public void channelLimit(int channelLimit) {
        channel_limit(mNativeState, channelLimit);
    }

    public void bandwidthLimit(int incomingBandwidth, int outgoingBandwidth) {
        bandwidth_limit(mNativeState, incomingBandwidth, outgoingBandwidth);
    }

    public void flush() {
        flush(mNativeState);
    }

    public Event checkEvents() throws EnetException {
        Event event = new Event();
        int ret = checkEvents(mNativeState, event.nativeState);
        if (ret <= 0)
            return null;
        return event;
    }

    public Event service(int timeout) throws EnetException {
        Event event = new Event();
        int ret = service(mNativeState, timeout, event.nativeState);
        if (ret <= 0)
            return null;
        return event;
    }

    public Event service(long timeout, TimeUnit unit) throws EnetException {
        return service((int) TimeUnit.MILLISECONDS.convert(timeout, unit));
    }


    /**
     * Do some clean work
     * @throws Throwable
     */
    public void clean() throws Throwable {
        Log.d(TAG, "clean!!!!");
        if (mNativeState != null) {
            destroy(mNativeState);
            mNativeState = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d(TAG, "finalize!!!!!!!");
        if (mNativeState != null) {
            destroy(mNativeState);
        }

        super.finalize();
    }

    private static native ByteBuffer create(int address, int port, int peerCount, int channelLimit, int incomingBandwidth, int outgoingBandwidth) throws EnetException;

    private static native ByteBuffer connect(ByteBuffer ctx, int address, int port, int channelCount, int data) throws EnetException;

    private static native void broadcast(ByteBuffer ctx, int channelID, ByteBuffer packet);

    private static native void channel_limit(ByteBuffer ctx, int channelLimit);

    private static native void bandwidth_limit(ByteBuffer ctx, int in, int out);

    private static native void flush(ByteBuffer ctx);

    private static native int checkEvents(ByteBuffer ctx, ByteBuffer event) throws EnetException;

    private static native int service(ByteBuffer ctx, int timeout, ByteBuffer event) throws EnetException;

    private static native void destroy(ByteBuffer ctx) throws EnetException;
}
