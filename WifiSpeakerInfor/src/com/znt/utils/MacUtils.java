package com.znt.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MacUtils 
{
	
	public static String getMac(Context context) {
		 
        String strMac = null;
 
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.e("=====", "6.0����");
            //Toast.makeText(context, "6.0����", Toast.LENGTH_SHORT).show();
            strMac = getLocalMacAddressFromWifiInfo(context);
            return strMac;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("=====", "6.0����7.0����");
            //Toast.makeText(context, "6.0����7.0����", Toast.LENGTH_SHORT).show();
            strMac = getMacAddress(context);
            return strMac;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e("=====", "7.0����");
            if (!TextUtils.isEmpty(getMacAddress())) {
                Log.e("=====", "7.0����1");
                //Toast.makeText(context, "7.0����1", Toast.LENGTH_SHORT).show();
                strMac = getMacAddress();
                return strMac;
            } else if (!TextUtils.isEmpty(getMachineHardwareAddress())) {
                Log.e("=====", "7.0����2");
                //Toast.makeText(context, "7.0����2", Toast.LENGTH_SHORT).show();
                strMac = getMachineHardwareAddress();
                return strMac;
            } else {
                Log.e("=====", "7.0����3");
                //Toast.makeText(context, "7.0����3", Toast.LENGTH_SHORT).show();
                strMac = getLocalMacAddressFromBusybox();
                return strMac;
            }
        }
 
        return "02:00:00:00:00:00";
    }
	
	/**
     * ����wifi��Ϣ��ȡ����mac
     * @param context
     * @return
     */
    public static String getLocalMacAddressFromWifiInfo(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo winfo = wifi.getConnectionInfo();
        String mac =  winfo.getMacAddress();
        return mac;
    }
    
    /**
     * android 6.0�����ϡ�7.0���� ��ȡmac��ַ
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
 
        // �����6.0���£�ֱ��ͨ��wifimanager��ȡ
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String macAddress0 = getMacAddress0(context);
            if (!TextUtils.isEmpty(macAddress0)) {
                return macAddress0;
            }
        }
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// ȥ�ո�
                    break;
                }
            }
        } catch (Exception ex) {
            Log.e("----->" + "NetInfoManager", "getMacAddress:" + ex.toString());
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("----->" + "NetInfoManager",
                        "getMacAddress:" + e.toString());
            }
 
        }
        return macSerial;
    }
 
    private static String getMacAddress0(Context context) {
        if (isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = null;
            try {
                wifiInfo = wifiMgr.getConnectionInfo();
                return wifiInfo.getMacAddress();
            } catch (Exception e) {
                Log.e("----->" + "NetInfoManager",
                        "getMacAddress0:" + e.toString());
            }
 
        }
        return "";
 
    }
 
    /**
     * Check whether accessing wifi state is permitted
     *
     * @param context
     * @return
     */
    private static boolean isAccessWifiStateAuthorized(Context context) {
        if (PackageManager.PERMISSION_GRANTED == context
                .checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
            Log.e("----->" + "NetInfoManager", "isAccessWifiStateAuthorized:"
                    + "access wifi state is enabled");
            return true;
        } else
            return false;
    }
 
    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }
 
    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }
    
    /**
     * ����IP��ַ��ȡMAC��ַ
     *
     * @return
     */
    public static String getMacAddress() {
        String strMacAddr = null;
        try {
            // ���IpD��ַ
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }
/**
     * ��ȡ�ƶ��豸����IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // �о�
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// �Ƿ���Ԫ��
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// �õ���һ��Ԫ��
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// �õ�һ��ip��ַ���о�
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
 
                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
 
            e.printStackTrace();
        }
        return ip;
    }
 
    /**
     * ��ȡ����IP
     *
     * @return
     */
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * android 7.0������ ��2��ɨ���������ӿڻ�ȡmac��ַ
     *
     */
    /**
     * ��ȡ�豸HardwareAddress��ַ
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null)
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }
 
    /***
     * byteתΪString
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
    
    /**
     * android 7.0������ ��3��ͨ��busybox��ȡ���ش洢��mac��ַ
     *
     */
 
    /**
     * ����busybox��ȡ����Mac
     *
     * @return
     */
    public static String getLocalMacAddressFromBusybox() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");
        // ������ص�result == null����˵�����粻��ȡ
        if (result == null) {
            return "�����쳣";
        }
        // �Ը������ݽ��н���
        // ���磺eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            Mac = result.substring(result.indexOf("HWaddr") + 6,
                    result.length() - 1);
            result = Mac;
        }
        return result;
    }
 
    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
 
            while ((line = br.readLine()) != null
                    && line.contains(filter) == false) {
                result += line;
            }
 
            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    

}
