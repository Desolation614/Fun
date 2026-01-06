/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.primitives.Bytes
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  net.runelite.http.api.worlds.World
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.worldhopper.ping;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import net.runelite.client.plugins.worldhopper.ping.IPHlpAPI;
import net.runelite.client.plugins.worldhopper.ping.IcmpEchoReply;
import net.runelite.client.plugins.worldhopper.ping.RLLibC;
import net.runelite.client.plugins.worldhopper.ping.Timeval;
import net.runelite.client.util.OSType;
import net.runelite.http.api.worlds.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ping {
    private static final Logger log = LoggerFactory.getLogger(Ping.class);
    private static final byte[] RUNELITE_PING = "RuneLitePing".getBytes(Charsets.UTF_8);
    private static final int TIMEOUT = 2000;
    private static final int PORT = 43594;
    private static final int MAX_IPV4_HEADER_SIZE = 60;
    private static short seq;

    public static int ping(World world) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(world.getAddress());
        }
        catch (UnknownHostException ex) {
            log.debug("error resolving host for world ping", (Throwable)ex);
            return -1;
        }
        if (!(inetAddress instanceof Inet4Address)) {
            log.debug("Only ipv4 ping is supported");
            return -1;
        }
        try {
            switch (OSType.getOSType()) {
                case Windows: {
                    return Ping.windowsPing(inetAddress);
                }
                case MacOS: 
                case Linux: {
                    try {
                        return Ping.icmpPing(inetAddress, OSType.getOSType() == OSType.MacOS);
                    }
                    catch (Exception ex) {
                        log.debug("error during icmp ping", (Throwable)ex);
                        return Ping.tcpPing(inetAddress);
                    }
                }
            }
            return Ping.tcpPing(inetAddress);
        }
        catch (IOException ex) {
            log.warn("error pinging", (Throwable)ex);
            return -1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int windowsPing(InetAddress inetAddress) {
        IPHlpAPI ipHlpAPI = IPHlpAPI.INSTANCE;
        Pointer ptr = ipHlpAPI.IcmpCreateFile();
        try {
            byte[] address = inetAddress.getAddress();
            Memory data = new Memory((long)RUNELITE_PING.length);
            data.write(0L, RUNELITE_PING, 0, RUNELITE_PING.length);
            IcmpEchoReply icmpEchoReply = new IcmpEchoReply((Pointer)new Memory((long)IcmpEchoReply.SIZE + data.size()));
            assert (icmpEchoReply.size() == IcmpEchoReply.SIZE);
            int packed = address[0] & 0xFF | (address[1] & 0xFF) << 8 | (address[2] & 0xFF) << 16 | (address[3] & 0xFF) << 24;
            int ret = ipHlpAPI.IcmpSendEcho(ptr, packed, (Pointer)data, (short)data.size(), Pointer.NULL, icmpEchoReply, IcmpEchoReply.SIZE + (int)data.size(), 2000);
            if (ret != 1) {
                int n = -1;
                return n;
            }
            int n = Math.toIntExact(icmpEchoReply.roundTripTime.longValue());
            return n;
        }
        finally {
            ipHlpAPI.IcmpCloseHandle(ptr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int icmpPing(InetAddress inetAddress, boolean includeIpHeader) throws IOException {
        RLLibC libc = RLLibC.INSTANCE;
        byte[] address = inetAddress.getAddress();
        int sock = libc.socket(2, 2, 1);
        if (sock < 0) {
            throw new IOException("failed to open ICMP socket");
        }
        try {
            block15: {
                long end;
                Timeval tv = new Timeval();
                tv.tv_sec = 2L;
                tv.write();
                if (libc.setsockopt(sock, RLLibC.SOL_SOCKET, RLLibC.SO_RCVTIMEO, tv.getPointer(), tv.size()) < 0) {
                    throw new IOException("failed to set SO_RCVTIMEO");
                }
                if (libc.setsockopt(sock, RLLibC.SOL_SOCKET, RLLibC.SO_SNDTIMEO, tv.getPointer(), tv.size()) < 0) {
                    throw new IOException("failed to set SO_SNDTIMEO");
                }
                short s = seq;
                seq = (short)(s + 1);
                short seqno = s;
                byte[] request = new byte[]{8, 0, 0, 0, 0, 0, (byte)(seqno >> 8 & 0xFF), (byte)(seqno & 0xFF)};
                request = Bytes.concat((byte[][])new byte[][]{request, RUNELITE_PING});
                short checksum = Ping.checksum(request);
                request[2] = (byte)(checksum >> 8 & 0xFF);
                request[3] = (byte)(checksum & 0xFF);
                byte[] byArray = new byte[16];
                byArray[0] = 2;
                byArray[1] = 0;
                byArray[2] = 0;
                byArray[3] = 0;
                byArray[4] = address[0];
                byArray[5] = address[1];
                byArray[6] = address[2];
                byArray[7] = address[3];
                byArray[8] = 0;
                byArray[9] = 0;
                byArray[10] = 0;
                byArray[11] = 0;
                byArray[12] = 0;
                byArray[13] = 0;
                byArray[14] = 0;
                byArray[15] = 0;
                byte[] addr = byArray;
                int size = 8 + RUNELITE_PING.length + (includeIpHeader ? 60 : 0);
                Memory response = new Memory((long)size);
                long start = System.nanoTime();
                if (libc.sendto(sock, request, request.length, 0, addr, addr.length) != request.length) {
                    int n = -1;
                    return n;
                }
                while (true) {
                    if ((System.nanoTime() - start) / 1000000L > 2000L) {
                        log.debug("timeout elapsed checking for echo reply");
                        break block15;
                    }
                    int rlen = libc.recvfrom(sock, (Pointer)response, size, 0, null, null);
                    end = System.nanoTime();
                    if (rlen <= 0) {
                        log.debug("recvfrom() error: len {} errno {}", (Object)rlen, (Object)Native.getLastError());
                        break block15;
                    }
                    int icmpHeaderOffset = 0;
                    if (includeIpHeader) {
                        int ihl = response.getByte(0L) & 0xF;
                        icmpHeaderOffset = ihl << 2;
                    }
                    if (icmpHeaderOffset + 7 >= rlen) {
                        log.warn("packet too short (received {} bytes but icmp header offset is {})", (Object)rlen, (Object)icmpHeaderOffset);
                        continue;
                    }
                    if (response.getByte((long)icmpHeaderOffset) != 0) {
                        log.debug("non-echo reply");
                        continue;
                    }
                    short seq = (short)((response.getByte((long)(icmpHeaderOffset + 6)) & 0xFF) << 8 | response.getByte((long)(icmpHeaderOffset + 7)) & 0xFF);
                    if (seqno == seq) break;
                    log.debug("sequence number mismatch ({} != {})", (Object)seqno, (Object)seq);
                }
                int n = (int)((end - start) / 1000000L);
                return n;
            }
            int n = -1;
            return n;
        }
        finally {
            libc.close(sock);
        }
    }

    private static short checksum(byte[] data) {
        int a = 0;
        for (int i = 0; i < data.length - 1; i += 2) {
            a += (data[i] & 0xFF) << 8 | data[i + 1] & 0xFF;
        }
        if ((data.length & 1) != 0) {
            a += (data[data.length - 1] & 0xFF) << 8;
        }
        a = (a >> 16 & 0xFFFF) + (a & 0xFFFF);
        return (short)(~a & 0xFFFF);
    }

    private static int tcpPing(InetAddress inetAddress) throws IOException {
        try (Socket socket = new Socket();){
            socket.setSoTimeout(2000);
            long start = System.nanoTime();
            socket.connect(new InetSocketAddress(inetAddress, 43594));
            long end = System.nanoTime();
            int n = (int)((end - start) / 1000000L);
            return n;
        }
    }
}

