package com.nexpaq.lib;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Tom-Hu
 * 
 */
public class Tools {
	/**
	 * get json node value by the key
	 * 
	 * @param content
	 *            the json string
	 * @param key
	 *            the key
	 * @return
	 */
	public static String getTargetInfo(String content, String key) {
		String result = "";
		try {
			JSONObject obj = new JSONObject(content);
			if (obj.has(key)) {
				result = obj.getString(key);
			}
		} catch (JSONException e) {
			result = "";
		}
		return result;
	}

	private final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String dumpHexString(byte[] array) {
		return dumpHexString(array, 0, array.length);
	}

	public static String dumpHexString(byte[] array, int offset, int length) {
		StringBuilder result = new StringBuilder();

		byte[] line = new byte[16];
		int lineIndex = 0;

		result.append("\n0x");
		result.append(toHexString(offset));

		for (int i = offset; i < offset + length; i++) {
			if (lineIndex == 16) {
				result.append(" ");

				for (int j = 0; j < 16; j++) {
					if (line[j] > ' ' && line[j] < '~') {
						result.append(new String(line, j, 1));
					} else {
						result.append(".");
					}
				}

				result.append("\n0x");
				result.append(toHexString(i));
				lineIndex = 0;
			}

			byte b = array[i];
			result.append(" ");
			result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
			result.append(HEX_DIGITS[b & 0x0F]);

			line[lineIndex++] = b;
		}

		if (lineIndex != 16) {
			int count = (16 - lineIndex) * 3;
			count++;
			for (int i = 0; i < count; i++) {
				result.append(" ");
			}

			for (int i = 0; i < lineIndex; i++) {
				if (line[i] > ' ' && line[i] < '~') {
					result.append(new String(line, i, 1));
				} else {
					result.append(".");
				}
			}
		}

		return result.toString();
	}

	public static String toHexString(byte[] array) {
		return toHexString(array, 0, array.length);
	}

	public static String toHexString(int i) {
		return toHexString(toByteArray(i));
	}

	public static byte[] toByteArray(int i) {
		byte[] array = new byte[4];

		array[3] = (byte) (i & 0xFF);
		array[2] = (byte) ((i >> 8) & 0xFF);
		array[1] = (byte) ((i >> 16) & 0xFF);
		array[0] = (byte) ((i >> 24) & 0xFF);

		return array;
	}

	public static String toHexString(byte[] array, int offset, int length) {
		char[] buf = new char[length * 2];

		int bufIndex = 0;
		for (int i = offset; i < offset + length; i++) {
			byte b = array[i];
			buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
			buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
		}

		return new String(buf);
	}

	private static byte[] intToBytes(int value) {
		byte[] byte_src = new byte[2];
		byte_src[0] = (byte) (value & 0xFF);
		byte_src[1] = (byte) ((value >> 8) & 0xFF);
		return byte_src;
	}

	public static byte[] generateCtrl(int node, byte[] cmd, byte[] params) {
		int length = params.length;
		int size = length + 10;
		byte[] data = new byte[size];
		data[0] = (byte) 0x7E;
		data[1] = (byte) 0x00;
		data[2] = (byte) node;
		data[3] = (byte) (length + 3);
		data[4] = cmd[0];
		data[5] = cmd[1];
		data[6] = (byte) length;
		for (int j = 0; j < length; j++) {
			data[j + 7] = params[j];
		}
		data[size - 3] = (byte) 0xEE;
		data[size - 2] = (byte) 0xEE;
		data[size - 1] = (byte) 0x7E;

		return setCrcResult(data);
	}

	static int[] crc16_table = { 0x0000, 0x1021, 0x2042, 0x3063, 0x4084,
			0x50a5, 0x60c6, 0x70e7, 0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c,
			0xd1ad, 0xe1ce, 0xf1ef, 0x1231, 0x0210, 0x3273, 0x2252, 0x52b5,
			0x4294, 0x72f7, 0x62d6, 0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd,
			0xc39c, 0xf3ff, 0xe3de, 0x2462, 0x3443, 0x0420, 0x1401, 0x64e6,
			0x74c7, 0x44a4, 0x5485, 0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee,
			0xf5cf, 0xc5ac, 0xd58d, 0x3653, 0x2672, 0x1611, 0x0630, 0x76d7,
			0x66f6, 0x5695, 0x46b4, 0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df,
			0xe7fe, 0xd79d, 0xc7bc, 0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840,
			0x1861, 0x2802, 0x3823, 0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948,
			0x9969, 0xa90a, 0xb92b, 0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71,
			0x0a50, 0x3a33, 0x2a12, 0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79,
			0x8b58, 0xbb3b, 0xab1a, 0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22,
			0x3c03, 0x0c60, 0x1c41, 0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a,
			0xbd0b, 0x8d68, 0x9d49, 0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13,
			0x2e32, 0x1e51, 0x0e70, 0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b,
			0xaf3a, 0x9f59, 0x8f78, 0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c,
			0xc12d, 0xf14e, 0xe16f, 0x1080, 0x00a1, 0x30c2, 0x20e3, 0x5004,
			0x4025, 0x7046, 0x6067, 0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d,
			0xd31c, 0xe37f, 0xf35e, 0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235,
			0x5214, 0x6277, 0x7256, 0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e,
			0xe54f, 0xd52c, 0xc50d, 0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466,
			0x6447, 0x5424, 0x4405, 0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f,
			0xf77e, 0xc71d, 0xd73c, 0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657,
			0x7676, 0x4615, 0x5634, 0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8,
			0x89e9, 0xb98a, 0xa9ab, 0x5844, 0x4865, 0x7806, 0x6827, 0x18c0,
			0x08e1, 0x3882, 0x28a3, 0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9,
			0x9bd8, 0xabbb, 0xbb9a, 0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1,
			0x1ad0, 0x2ab3, 0x3a92, 0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa,
			0xad8b, 0x9de8, 0x8dc9, 0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2,
			0x2c83, 0x1ce0, 0x0cc1, 0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b,
			0xbfba, 0x8fd9, 0x9ff8, 0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93,
			0x3eb2, 0x0ed1, 0x1ef0 };

	/**
	 * 
	 * @Title: caluCRC
	 * @Description: CRC16校验
	 * @param @param pByte
	 * @param @return
	 * @return char
	 * @throws
	 */
	public static char caluCRC(byte[] pByte) {
		int len = pByte.length;
		char crc;
		byte da;
		crc = 0x0;
		int i = 0;
		while (len-- != 0) {
			da = (byte) (crc / 256);
			crc <<= 8;
			int num = da ^ pByte[i];
			if (num < 0)
				num += 256;
			crc ^= crc16_table[num];
			++i;
		}
		return crc;
	}

	public static byte[] setCrcResult(byte[] data) {
		int len = data[3] + 3;
		byte[] value = new byte[len];
		value[0] = data[1];
		value[1] = data[2];
		value[2] = data[3];
		for (int i = 0; i < data[3]; i++) {
			value[i + 3] = data[i + 4];
		}
		int num = caluCRC(value);
		byte[] abc = intToBytes(num);
		data[data.length - 2] = abc[0];
		data[data.length - 3] = abc[1];
		return data;
	}

}
