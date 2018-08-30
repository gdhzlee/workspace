package com.zemcho.pe.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP工具类
 *
 * @Author Jetvin
 * @Date 2018/7/24
 * @Time 14:54
 * @Version ╮(╯▽╰)╭
 *
 * <!--         ░░░░░░░░░░░░░░░░░░░░░░░░▄░░         -->
 * <!--         ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░         -->
 * <!--         ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐          -->
 * <!--         ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐          -->
 * <!--         ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐          -->
 * <!--         ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌          -->
 * <!--         ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒          -->
 * <!--         ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐         -->
 * <!--         ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄         -->
 * <!--         -░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒        -->
 * <!--         ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒         -->
 * <!--                                             -->
 * <!--                 咦！有人在改BUG               -->
 */
@Slf4j
public class IpAddressUtil {

    /**
     * \d          ->      {0 -9}
     * [1-9]        ->      {1 -9}
     * [1-9]\d     ->      {10 - 99}
     * 1\d{2}      ->      {100 - 199}
     * 2[0-4]\d    ->      {200 - 249}
     * 25[0-5]      ->      {250 - 255}
     * ^([1-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3}$
     *
     * 1.0.0.0 - 255.255.255.255
     */
    private final static String REGEXP = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0){
            ip = "172.0.0.1";
        }
        return ip;
    }

    public static String getMAC(String ip) {
        String str = null;
        String macAddress = null;
        try {
            Process p = Runtime.getRuntime().exec(" nbtstat -A " + ip);
            InputStreamReader ir = new InputStreamReader(p.getInputStream(), "UTF-8");
            LineNumberReader input = new LineNumberReader(ir);
            for (; true; ) {
                str = input.readLine();
                System.out.println(str);
                if (str != null) {
                    if (str.indexOf("MAC Address") > 1) {
                        macAddress = str
                                .substring(str.indexOf("MAC Address") + 14);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return null;
        }
        return macAddress;
    }

    public static boolean checkIp(String ip) {

        Pattern pattern = Pattern.compile(REGEXP);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public static boolean checkIpRange(String ipRange){
        if (ipRange == null || "".equals(ipRange.trim())){
            log.info("IP范围不能为空");
            return false;
        }

        String[] ipRangeSplit = ipRange.split("-");
        if (ipRangeSplit.length != 2){
            log.info("IP范围的格式错误");
            return false;
        }

        for (String s : ipRangeSplit){
            if (!checkIp(s)){
                log.info("IP范围组成的IP的格式错误");
                return false;
            }
        }

        String ipRangePrefix = ipToBinary(ipRangeSplit[0]);
        String ipRangeSuffix = ipToBinary(ipRangeSplit[1]);

        long ipRangePrefixLong = ipBinaryToLong(ipRangePrefix);
        long ipRangeSuffixLong = ipBinaryToLong(ipRangeSuffix);

        log.info("ipRangePrefixLong:{}",ipRangePrefixLong);
        log.info("ipRangeSuffixLong:{}",ipRangeSuffixLong);
        if (ipRangePrefixLong > ipRangeSuffixLong){
            log.info("IP范围组成的IP必须是前者小于或等于后者");
            return false;
        }

        return true;
    }

    public static String ipToBinary(String ip) {
        String[] ipArray = ip.split("\\.");
        StringBuffer ipBinary = new StringBuffer();
        for (int i = 0; i < ipArray.length; i++) {
            String str = ipArray[i];
            String binary = Integer.toBinaryString(Integer.parseInt(str));
            if (binary.length() < 8) {
                int num = 8 - binary.length();
                String filling = "";
                for (int j = 0; j < num; j++) {
                    filling += "0";
                }
                ipBinary.append(filling + binary);
            } else {
                ipBinary.append(binary);
            }
        }
        return ipBinary.toString();
    }

    public static long ipBinaryToLong(String ipBinary){

        return Long.parseLong(new BigInteger(ipBinary, 2).toString());
    }

    public static boolean checkIpInIpRange(String ipRange, String ip) {

        String[] ipRangeSplit = ipRange.split("-");
        String ipRangePrefixBinary = ipToBinary(ipRangeSplit[0]);
        String ipRangeSuffixBinary = ipToBinary(ipRangeSplit[1]);
        String ipBinary = ipToBinary(ip);

        long ipRangePrefixLong = ipBinaryToLong(ipRangePrefixBinary);
        long ipRangeSuffixLong = ipBinaryToLong(ipRangeSuffixBinary);
        long ipLong = ipBinaryToLong(ipBinary);

        return ipRangePrefixLong <= ipLong && ipLong <= ipRangeSuffixLong;
    }

    public static void main(String[] args) {
        checkIpRange("183.22.29.150-183.22.29.150");
    }
}
