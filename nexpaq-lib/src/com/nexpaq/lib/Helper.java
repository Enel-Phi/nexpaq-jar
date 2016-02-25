package com.nexpaq.lib;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fathzer.soft.javaluator.DoubleEvaluator;

/**
 * @author Tom-Hu
 * 
 */
public class Helper {
	private static boolean isOver;
	private static InputStream inStream = null;
	
//	public static void main(String[] args){
//		
//	}

	public static String binaryToJson(byte[] data, String parse) {
		JSONObject json = new JSONObject();
		try {
			JSONArray array = new JSONArray(parse);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				String fun = object.getString("fun");
				JSONArray dataArray = object.getJSONArray("data");

				if (object.has("state")) {
					byte[] content = new byte[dataArray.length()];
					for (int j = 0; j < content.length; j++) {
						content[j] = data[dataArray.getInt(j)];
					}

					JSONObject state = object.getJSONObject("state");
					String value = state.getString(Tools.toHexString(content));
					json.put(fun, value);
				} else if (object.has("format")) {
					List<Integer> content = new ArrayList<Integer>();
					for (int j = 0; j < dataArray.length(); j++) {
						content.add(j, data[dataArray.getInt(j)] & 0xFF);
					}

					String format = object.getString("format");
					String src = String.format(format, content.toArray());

					if (!src.isEmpty()) {
						DoubleEvaluator evaluator = new DoubleEvaluator();
						Double value = evaluator.evaluate(src);
						BigDecimal bg = new BigDecimal(value);
						value = bg.setScale(2, BigDecimal.ROUND_HALF_UP)
								.doubleValue();
						json.put(fun, value);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public static byte[] jsonToBinary(String content, String ctrl, int node) {
		byte[] data = null;
		JSONObject ctrlObject;
		JSONObject valueJson;
		JSONArray paramArray;
		String name = "";
		byte[] cmd = new byte[2];
		byte[] params = null;
		try {
			ctrlObject = new JSONObject(content);
			name = ctrlObject.getString("Name");
			JSONArray ctrlArray = new JSONArray(ctrl);
			for (int i = 0; i < ctrlArray.length(); i++) {
				valueJson = ctrlArray.getJSONObject(i);
				if (name.equals(valueJson.getString("name"))) {
					cmd[0] = (byte) valueJson.getJSONArray("cmd").getInt(0);
					cmd[1] = (byte) valueJson.getJSONArray("cmd").getInt(1);
				}
			}

			paramArray = ctrlObject.getJSONArray("Param");
			params = new byte[paramArray.length()];
			for (int i = 0; i < paramArray.length(); i++) {
				params[i] = (byte) paramArray.getInt(i);
			}

			if (cmd != null && params != null) {
				data = Tools.generateCtrl(node, cmd, params);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static void downloadResource(final String fileUrl) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				inStream = null;
				isOver = false;
				try {
					URL url = new URL(fileUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					inStream = conn.getInputStream();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isOver = true;
			}
		}).start();
	}

	public static boolean downloadResult() {
		return isOver;
	}

	public static InputStream getResourceStream() {
		isOver = false;
		return inStream;
	}
}
