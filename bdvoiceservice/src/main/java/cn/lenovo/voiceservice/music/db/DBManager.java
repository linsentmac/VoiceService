package cn.lenovo.voiceservice.music.db;

import android.content.Context;

import cn.lenovo.voiceservice.music.db.greendao.DaoMaster;
import cn.lenovo.voiceservice.music.db.greendao.DaoSession;
import cn.lenovo.voiceservice.music.db.greendao.MusicDao;

import org.greenrobot.greendao.database.Database;

/**
 * DBManager
 * Created by chao on 2018/4/1.
 */
public class DBManager {
    private static final String DB_NAME = "database";
    private MusicDao musicDao;

    public static DBManager get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static DBManager instance = new DBManager();
    }

    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        musicDao = daoSession.getMusicDao();
    }

    private DBManager() {
    }

    public MusicDao getMusicDao() {
        return musicDao;
    }
}
