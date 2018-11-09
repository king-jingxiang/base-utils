package org.base.utils.net;

import org.junit.Before;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtilTest {
    InetAddress addr = null;

    @Before
    public void before() throws UnknownHostException {
        addr = InetAddress.getLocalHost();
        String hostname = addr.getHostName();
        System.out.println(hostname);
    }

    @org.junit.Test
    public void toInt() {
        IPUtil.toInt(addr);
    }

    @org.junit.Test
    public void fromInt() {
    }

    @org.junit.Test
    public void fromIpString() {
    }

    @org.junit.Test
    public void fromIpv4String() {
    }

    @org.junit.Test
    public void intToIpv4String() {

    }

    @org.junit.Test
    public void ipv4StringToInt() {
        int i = IPUtil.ipv4StringToInt("127.0.0.1");
        System.out.println(i);
    }
}
