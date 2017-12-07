package com.kanven.cloud.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtil {

	private final static Logger log = LoggerFactory.getLogger(IPUtil.class);
	
	public static final String ip = IPUtil.getIP();

	private final static String JMX_HOST_NAME = "java.rmi.server.hostname";

	public static String getIP() {
		String host = System.getenv(JMX_HOST_NAME);
		if (StringUtils.isNotBlank(host)) {
			String[] items = host.split("\\.");
			if (items != null && items.length == 4) {
				boolean flag = true;
				for (String item : items) {
					try {
						int v = Integer.parseInt(item);
						if (v < 0 && v > 255) {
							flag = false;
							break;
						}
					} catch (Exception e) {
						flag = false;
						break;
					}
				}
				if (flag) {
					return host;
				}
			}
		}
		return getHostIP();
	}

	public static String getHostIP() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumeration = NetworkInterface
					.getNetworkInterfaces();
			while (enumeration.hasMoreElements()) {
				NetworkInterface networkInterface = enumeration.nextElement();
				Enumeration<InetAddress> inetAddresss = networkInterface
						.getInetAddresses();
				while (inetAddresss.hasMoreElements()) {
					InetAddress inetAddress = inetAddresss.nextElement();
					if (inetAddress.isSiteLocalAddress()
							&& !inetAddress.isLoopbackAddress()
							&& inetAddress.getHostAddress().indexOf(":") == -1) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			log.error("获取ip地址出现异常！", e);
		}
		return ip;
	}
}
