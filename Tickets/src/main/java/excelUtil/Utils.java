package excelUtil;

import java.util.*;

/**
 * Class that sort HashMap
 * Created by zhangyang33 on 2017/11/1.
 */
public class Utils {

    /**
     * This method used to sort map by int key
     * @param map   Given map to sort
     * @return      Sorted map list
     */
    public static List<Map.Entry<String, RecordHandler>> sortmap(Map<String, RecordHandler> map){
        List<Map.Entry<String, RecordHandler>> list = new ArrayList<Map.Entry<String, RecordHandler>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, RecordHandler>>() {
            @Override
            public int compare(Map.Entry<String, RecordHandler> o1, Map.Entry<String, RecordHandler> o2) {
                int order = 0;
                int c1 = Integer.parseInt(o1.getKey());
                int c2 = Integer.parseInt(o2.getKey());

                if (c1>c2){
                    order = 1;
                }
                else if (c1<c2){
                    order = -1;
                }

                return order;
            }
        });

        return list;
    }
}
