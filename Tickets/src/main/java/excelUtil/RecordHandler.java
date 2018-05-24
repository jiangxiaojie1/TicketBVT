package excelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that record test case infomation
 * Created by zhangyang33 on 2017/9/18.
 */
public class RecordHandler {

    /**
     * define three record type
     */
    private enum RecordType {
        VALUE, NAMED_MAP, INDEXED_LIST
    }

    private String single_value = "";
    private HashMap<String, String> named_value_map = new HashMap<String, String>();
    private List<String> indexed_value_list = new ArrayList<String>();
    private RecordType myType;

    /**
     * define three record type
     */
    public RecordHandler() {
        this("");
    }

    /**
     * Primary constructor, record type is value, the value is String type
     * @param value    This is RecordHandler value, a String value
     */
    public RecordHandler(String value) {
        this.myType = RecordType.VALUE;
        this.single_value = value;
    }

    /**
     * Primary constructor, record type is value, the value is String type
     * @param map    This is RecordHandler value, a HashMap value
     */
    public RecordHandler(HashMap<String, String> map) {
        this.myType = RecordType.NAMED_MAP;
        this.named_value_map = map;
    }

    /**
     * Primary constructor, record type is value, the value is String type
     * @param list    This is RecordHandler value, a List<String> value
     */
    public RecordHandler(List<String> list) {
        this.myType = RecordType.INDEXED_LIST;
        this.indexed_value_list = list;
    }

    /**
     * Get named_value_map value
     * @return HashMap<String, String>   Return map value
     */
    public HashMap<String, String> get_map() {
        return named_value_map;
    }

    /**
     * Get value length
     * @return  int   return value length
     */
    public int size() {
        int result = 0;

        if(myType.equals(RecordType.VALUE)) {
            result = 1;
        } else if(myType.equals(RecordType.NAMED_MAP)) {
            result = named_value_map.size();
        } else if(myType.equals(RecordType.INDEXED_LIST)) {
            result = indexed_value_list.size();
        }

        return result;
    }

    /**
     * Get RecordType.VALUE value
     * @return String return single_value
     */
    public String get() {
        String result = "";

        if(myType.equals(RecordType.VALUE)) result = single_value;
        else {
            System.out.println("Called get() on wrong type:" + myType.toString());
        }

        return result;
    }

    /**
     * Get map value by key
     * @param key      map key which used to get value
     * @return String  Return map value by key
     */
    public String get(String key) {
        String result = "";

        if(myType.equals(RecordType.NAMED_MAP)) result = named_value_map.get(key);

        return result;
    }

    /**
     * Get map value by index
     * @param index    List index which used to get value
     * @return String  Return list value by index
     */
    public String get(Integer index) {
        String result = "";

        if(myType.equals(RecordType.INDEXED_LIST)) result = indexed_value_list.get(index);

        return result;
    }

    /**
     * Set value for record type which is Value or INDEXED_LIST
     * @param value    the value to set
     * @return Boolean Return true if set value to single_value success, else return false
     */
    public Boolean set(String value) {
        Boolean result = false;

        if(myType.equals(RecordType.VALUE)) {
            this.single_value = value;
            result = true;
        } else if(myType.equals(RecordType.INDEXED_LIST)) {
            this.indexed_value_list.add(value);
            result = true;
        }

        return result;
    }

    /**
     * Set value for record type which is NAMED_MAP
     * @param key      map key to set
     * @param value    map value to set
     * @return Boolean Return true if set value to map success, else return false
     */
    public Boolean set(String key, String value) {
        Boolean result = false;

        if(myType.equals(RecordType.NAMED_MAP)) {
            this.named_value_map.put(key, value);
            result = true;
        }

        return result;
    }

    /**
     * Set value for record type which is INDEXED_LIST
     * @param index    List index to set
     * @param value    List value to set
     * @return Boolean Return true if set value to list success, else return false
     */
    public Boolean set(Integer index, String value) {
        Boolean result = false;

        if(myType.equals(RecordType.INDEXED_LIST)) {
            if(this.indexed_value_list.size() > index) this.indexed_value_list.set(index, value);

            result = true;
        }

        return result;
    }

    /**
     * If a value was contained
     * @param value    The value need to verify
     * @return Boolean Return true if The value was contained, else return false
     */
    public Boolean has(String value) {
        Boolean result = false;

        if(myType.equals(RecordType.VALUE) && this.single_value.equals(value)) {
            result = true;
        } else if(myType.equals(RecordType.NAMED_MAP) && this.named_value_map.containsKey(value)) {
            result = true;
        } else if(myType.equals(RecordType.INDEXED_LIST) && this.indexed_value_list.contains(value)) {
            result = true;
        }

        return result;
    }

    /**
     * Return a boolean result that if a value was removed
     * @param value     The value need to removed
     * @return Boolean  Return true if a value was removed, else return false
     */
    public Boolean remove(String value) {
        Boolean result = false;

        if(myType.equals(RecordType.VALUE) && this.single_value.equals(value)) {
            this.single_value = "";
            result = true;
        }
        if(myType.equals(RecordType.NAMED_MAP) && this.named_value_map.containsKey(value)) {
            this.named_value_map.remove(value);
            result = true;
        } else if(myType.equals(RecordType.INDEXED_LIST) && this.indexed_value_list.contains(value)) {
            this.indexed_value_list.remove(value);
            result = true;
        }

        return result;
    }

    /**
     * Return a boolean result that if a value was removed from list
     * @param index     The value need to removed from list
     * @return Boolean  Return true if a value was removed, else return false
     */
    public Boolean remove(Integer index) {
        Boolean result = false;

        if(myType.equals(RecordType.INDEXED_LIST) && this.indexed_value_list.contains(index)) {
            this.indexed_value_list.remove(index);
            result = true;
        }

        return result;
    }

}
