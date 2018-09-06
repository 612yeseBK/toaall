package com.expect.admin.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtil {

	private static JsonUtil jsonUtil = new JsonUtil();
	private Gson gson = new Gson();

	private JsonUtil() {

	}

	public static JsonUtil getInstance() {
		return jsonUtil;
	}

	/**
	 * ������json
	 */
	public String toJson(Object bean) {
		return gson.toJson(bean);
	}

	/**
	 * ת��Ϊ��ͨ���x
	 */
	public <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	/**
	 * ת��list
	 */
	public List<Object> fromJsonArray(String json) {
		List<Object> list = gson.fromJson(json, new TypeToken<List<Object>>() {
		}.getType());
		return list;
	}
	

	/**
	 * ת��list
	 */
	public List<String> fromJsonStringArray(String json) {
		List<String> list = gson.fromJson(json, new TypeToken<List<Object>>() {
		}.getType());
		return list;
	}

	public <T> List<T> fromJsonArray(String json, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, cls));
		}
		return list;
	}

	/**
	 * ת��list����map
	 */
	public <T> List<Map<String, T>> fromJsonArrayMap(String json) {
		List<Map<String, T>> list = gson.fromJson(json, new TypeToken<List<Map<String, T>>>() {
		}.getType());
		return list;
	}

	/**
	 * ת��map
	 */
	public <T> Map<String, T> fromJsonMap(String json) {
		Map<String, T> map = gson.fromJson(json, new TypeToken<Map<String, T>>() {
		}.getType());
		return map;
	}

}
