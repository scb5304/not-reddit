package com.jollyremedy.notreddit.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.jollyremedy.notreddit.db.daos.PostDao;
import com.jollyremedy.notreddit.models.Post;

@Database(entities = {Post.class}, version = 1)
@TypeConverters(DateTypeConverter.class)
public abstract class NotRedditDatabase extends RoomDatabase {

    private static NotRedditDatabase sInstance;

    private static final String DATABASE_NAME = "not-reddit";
    public abstract PostDao postDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static NotRedditDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (NotRedditDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext());
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static NotRedditDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, NotRedditDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        NotRedditDatabase database = NotRedditDatabase.getInstance(appContext);
                        database.setDatabaseCreated();
                    }
                }).build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}