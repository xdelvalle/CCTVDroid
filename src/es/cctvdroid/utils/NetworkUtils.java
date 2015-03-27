package es.cctvdroid.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils {
	
	public static NetworkUtils instance = new NetworkUtils();
	
	private NetworkUtils() 
	{ }

	public static NetworkUtils getInstance() {
		return instance;
	}
	
	public boolean isIpValida(String ip) {
		return true;
	}
	
	public String getIpFromHostname(String hostname) {
		InetAddress ip;
		try {
			ip = InetAddress.getByName(hostname);
		}
		catch (UnknownHostException e) {
			return null;
		}
		return ip.toString();
	}
}
