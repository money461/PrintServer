package com.bondex.util;

public class Bytes {

    public static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 转为16进制
     */
    public static String toHexString(byte b) {
        StringBuffer sb = new StringBuffer();
        int high = b >> 4 & 0xF;
        int low = b & 0xF;
        sb.append(HEX[high]);
        sb.append(HEX[low]);
        return sb.toString();
    }

    /**
     * 转为16进制
     */
    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, 0, bytes == null ? 0 : bytes.length);
    }

    /**
     * 转为16进制
     */
    public static String toHexString(byte[] bytes, int start, int offset) {
        if (bytes == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = start; i < start + offset; i++) {
            sb.append(HEX[(bytes[i] >> 4 & 0xF)]);
            sb.append(HEX[(bytes[i] & 0xF)]);
        }
        return sb.toString();
    }

    /**
     * 转为16进制
     */
    public static String toHexStringX(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hv = Integer.toHexString(bytes[i] & 0xFF);
            if (hv.length() == 1) {
                hv = "%0" + hv;
            } else {
                hv = "%" + hv;
            }
            result.append(hv.toUpperCase());
        }
        return result.toString();
    }
}
