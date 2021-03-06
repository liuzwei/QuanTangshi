package animalize.github.com.quantangshi.Database;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;


public class RecentAgent {
    // 最多记录60条
    private static final int limit = 60;
    private static MyLinkedHashMap recentList;

    public static synchronized void addToRecent(final InfoItem info) {
        if (recentList == null) {
            loadRecentList();
        }

        // 删已有的
        recentList.remove(info.getId());

        // 添新的，会自动删过量的
        recentList.put(info.getId(), info);

        // 进数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyDatabaseHelper.addToRecentList(info, limit);
            }
        }).start();
    }

    public static synchronized List<InfoItem> getRecentList() {
        if (recentList == null) {
            loadRecentList();
        }

        List<InfoItem> t = new ArrayList<>(recentList.values());

        // 反转
        Collections.reverse(t);
        return t;
    }

    public static synchronized void invalideRecent() {
        recentList = null;
    }

    private static void loadRecentList() {
        recentList = new MyLinkedHashMap();

        ArrayList<InfoItem> tempList = MyDatabaseHelper.getRecentList();

        // 添加到本地
        for (InfoItem info : tempList) {
            recentList.put(info.getId(), info);
        }
    }

    private static class MyLinkedHashMap extends LinkedHashMap {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > RecentAgent.limit;
        }
    }
}
