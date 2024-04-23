package fan.summer.hmoneta.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ip地址相关工具
 */
public class IpUtil {
    public static boolean isIpMatchMask(String ip, String maskCidr) {
        int ipInt = ipToInt(ip);
        int maskInt = maskToInt(maskCidr);
        int networkAddress = getNetworkAddress(ipInt, maskInt);

        // 将网络地址转换回 IP 地址字符串
        String networkAddressStr = intToIp(networkAddress);

        // 比较网络地址与输入的 IP 地址是否相同
        return networkAddressStr.equals(ip);
    }
    public static boolean isValidNetworkAddress(String networkAddress) {
        String[] parts = networkAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        if (Integer.parseInt(parts[parts.length -1]) != 0) {
            return false;
        }

        for (String part : parts) {
            if (part.length() > 3 || Integer.parseInt(part) < 0 || Integer.parseInt(part) > 255) {
                return false;
            }
        }

        return true;
    }
    public static boolean isValidMask(String maskCidr) {
        int cidr = Integer.parseInt(maskCidr.replaceAll("/", ""));
        return cidr > 0 && 32 - cidr >= 0;
    }
    public static boolean isValidIpAddress(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            if (part.length() > 3 || Integer.parseInt(part) < 0 || Integer.parseInt(part) > 255) {
                return false;
            }
        }

        return true;
    }
    public static boolean isValidMACAddress(String macAddress) {
        // 检查长度是否为 17 个字符
        if (macAddress.length() != 17) {
            return false;
        }

        // 检查每个字符是否为合法的十六进制数字
        for (int i = 0; i < macAddress.length(); i++) {
            char c = macAddress.charAt(i);
            if (i % 3 != 2 && !Character.isDigit(c) && !Character.isUpperCase(c) && !Character.isLowerCase(c)) {
                return false;
            } else if (i % 3 == 2 && c != ':' && c != '-') {
                return false;
            }
        }

        return true;
    }
    public static boolean isInSameSubnet(String ip1, String ip2, String mask) {
        // 将 IP 地址字符串解析为整数
        int addr1 = ipToInt(ip1);
        int addr2 = ipToInt(ip2);
        int mk = maskToInt(mask);

        // 将 IP 地址与子网掩码进行按位与操作
        int network1 = addr1 & mk;
        int network2 = addr2 & mk;

        // 比较两个 IP 地址经过子网掩码运算后的结果是否相同
        return network1 == network2;
    }

    public static int ipToInt(String ip) {
        String[] ipArray = ip.split("\\.");
        int ipInt = 0;
        for (int i = 0; i < ipArray.length; i++) {
            ipInt = (ipInt << 8) + Integer.parseInt(ipArray[i]);
        }
        return ipInt;
    }
    public static String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(ipInt >>> (24 - i * 8) & 0xFF);
            if (i < 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }
    public static int maskToInt(String maskCidr) {
        int cidr = Integer.parseInt(maskCidr.replaceAll("/", ""));
        int maskInt = 0xFFFFFFFF << (32 - cidr);
        return maskInt;
    }
    public static int getNetworkAddress(int ipInt, int maskInt) {
        return ipInt & maskInt;
    }
    public static int getBroadcastAddress(int ipInt, int maskInt) {
        return getNetworkAddress(ipInt, maskInt) | (~maskInt);
    }
    public static Map<String,String> getAvailableIpRange(String ip, String maskCidr) {
        Map<String, String> result = new HashMap<>();
        int ipInt = ipToInt(ip);
        int maskInt = maskToInt(maskCidr);
        int networkAddress = getNetworkAddress(ipInt, maskInt);
        int broadcastAddress = getBroadcastAddress(ipInt, maskInt);

        int firstAvailableIp = networkAddress + 1;
        int lastAvailableIp = broadcastAddress - 1;

        result.put("NetworkAddress", intToIp(networkAddress));
        result.put("BroadcastAddress", intToIp(broadcastAddress));
        result.put("FirstIP", intToIp(firstAvailableIp));
        result.put("LastIP", intToIp(lastAvailableIp));
        return result;
    }
    public static int getAvailableIPNum(String startIPAddress, String endIPAddress) {
        int startIP = ipToInt(startIPAddress);
        int endIP = ipToInt(endIPAddress);
        return (endIP - startIP + 1);
    }

    public static void ipOrder(List<String> ipList, String orderWay) {
        if (orderWay.equals("asc")) {
            ipList.sort((o1, o2) -> {
                int ip1 = ipToInt(o1);
                int ip2 = ipToInt(o2);
                return Integer.compare(ip1, ip2);
            });
        }else if (orderWay.equals("desc")) {
            ipList.sort((o1, o2) -> {
                int ip1 = ipToInt(o1);
                int ip2 = ipToInt(o2);
                return Integer.compare(ip2, ip1);
            });
        }
    }
}
