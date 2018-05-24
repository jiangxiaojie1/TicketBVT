package helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by zhangyang33 on 2018/3/19.
 */
public class GetJsonKeys {
    public static List<Map<String, String>> getKeys(JSONObject test) throws JSONException {
        List<Map<String, String>> allkeys = new ArrayList<>();
        Map<String, String> keyValue;
        Iterator keys = test.keys();

        while (keys.hasNext()) {
            try {
                String key = keys.next().toString();
                String value = test.optString(key);
                int i = testIsArrayORObject(value);

                if (allkeys.size()==0) {
                    if (i == 0) {
                        keyValue = new HashMap<>();
                        keyValue.put(key, value);
                        allkeys.add(keyValue);
                    } else if (i == 1) {
                        allkeys.addAll(getKeys(new JSONObject(value)));
                    } else if (i == 2) {
                        JSONArray arrays = new JSONArray(value);
                        for (int k = 0; k < arrays.length(); k++) {
                            JSONObject array = (JSONObject) arrays.get(k);
                            allkeys.addAll(getKeys(array));
                        }
                    }
                } else {
                    if (i == 0) {
                        keyValue = new HashMap<>();
                        keyValue.put(key, value);
                        allkeys.add(keyValue);
                    } else if (i == 1) {
                        allkeys.addAll(getKeys(new JSONObject(value)));
                    } else if (i == 2) {
                        JSONArray arrays = new JSONArray(value);
                        for (int k = 0; k < arrays.length(); k++) {
                            JSONObject array = (JSONObject) arrays.get(k);
                            allkeys.addAll(getKeys(array));
                        }
                    }
                }
            } catch (JSONException e) {
                throw e;
            }
        }

        return allkeys;
    }

    public static int testIsArrayORObject(String sJSON) {
    /*
     * return 0:既不是array也不是object
     * return 1：是object
     * return 2 ：是Array
     */
        try {
            JSONArray array = new JSONArray(sJSON);
            return 2;
        } catch (JSONException e) {// 抛错 说明JSON字符不是数组或根本就不是JSON
            try {
                JSONObject object = new JSONObject(sJSON);
                return 1;
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                return 0;
            }
        }
    }

    public static List<Map<String, String>> getKeyListByName(String keyName,JSONObject test) throws JSONException {
        List<Map<String, String>> allKeys = getKeys(test);
        List<Map<String, String>> result = new ArrayList<>();

        for(Map<String, String> key : allKeys){
            if(key.containsKey(keyName)){
                result.add(key);
            }
        }

        return result;
    }

    public static JSONObject setKeys(JSONObject body, String expectKey, String expectValue) throws JSONException {
        Iterator keys = body.keys();
        while (keys.hasNext()) {
            try {
                String key = keys.next().toString();
                String value = body.optString(key);
                int i = testIsArrayORObject(value);

                if (i == 0) {
                    if (key.equals(expectKey)) {
                        body.put(key, expectValue);
                        break;
                    }
                } else if (i == 1) {
                    body.put(key, setKeys(new JSONObject(value), expectKey, expectValue));
                } else if (i == 2) {
                    JSONArray arrays = new JSONArray(value);
                    for (int k = 0; k < arrays.length(); k++) {
                        JSONObject array = (JSONObject) arrays.get(k);
                        body.put(key, setKeys(array, expectKey, expectValue));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return body;
    }
}
