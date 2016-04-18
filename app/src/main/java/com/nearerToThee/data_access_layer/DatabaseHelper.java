package com.nearerToThee.data_access_layer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nearerToThee.model.File;
import com.nearerToThee.model.Tag;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class for CRUD operations on SQlite db.
 * Created by Betsy on 2/17/2016.
 */
public class DatabaseHelper extends SQLiteAssetHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "nearerToThee.sqlite";

    // Table Names
    private static final String TABLE_FILES = "files";
    private static final String TABLE_TAGS = "tags";
    private static final String TABLE_FILES_TAGS = "file_tags";

    // Common column names
    private static final String KEY_ID = "_id";

    // FILES Table - column nmaes
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_FILE_TITLE = "title";
    private static final String KEY_FILE_IS_FAVORITE = "is_favorite";

    // TAGS Table - column names
    private static final String KEY_TAG_NAME = "tag_name";

    // FILE_TAGS Table - column names
    private static final String KEY_FILE_ID = "file_id";
    private static final String KEY_TAG_ID = "tag_id";

    // Table Create Statements
    // File table create statement
    private static final String CREATE_TABLE_FILES = "CREATE TABLE "
            + TABLE_FILES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FILE_NAME
            + " TEXT," + KEY_FILE_TITLE + " TEXT," + KEY_FILE_IS_FAVORITE
            + " INTEGER DEFAULT 0" + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_TAGS = "CREATE TABLE " + TABLE_TAGS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT" + ")";

    // todo_tag table create statement
    private static final String CREATE_TABLE_FILE_TAGS = "CREATE TABLE "
            + TABLE_FILES_TAGS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_FILE_ID + " INTEGER," + KEY_TAG_ID + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * Creating a file
     */
    public long createFile(File fileName, long[] tag_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_NAME, fileName.getFileName());
        values.put(KEY_FILE_TITLE, fileName.getFileTitle());
        values.put(KEY_FILE_IS_FAVORITE, fileName.getIsFavorite());

        // insert row
        long file_id = db.insert(TABLE_FILES, null, values);

        // assigning tags to fileName
        for (long tag_id : tag_ids) {
            createFileTag(file_id, tag_id);
        }
        db.close();
        return file_id;
    }

    /*
 * get single file
 */
    public File getFile(long file_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FILES + " WHERE "
                + KEY_ID + " = " + file_id;

        Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        File file = new File();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                file.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                file.setFileName((cursor.getString(cursor.getColumnIndex(KEY_FILE_NAME))));
                int isFavorite = cursor.getInt(cursor.getColumnIndex(KEY_FILE_IS_FAVORITE));
                file.setIsFavorite((isFavorite == 1) ? true : false);
            }
            cursor.close();
        }

        db.close();
        return file;
    }

    /*
 * getting all files
 * */
    public List<File> getAllFiles() {
        List<File> files = new ArrayList<File>();
        String selectQuery = "SELECT  * FROM " + TABLE_FILES;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    File file = new File();
                    file.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    file.setFileName((cursor.getString(cursor.getColumnIndex(KEY_FILE_NAME))));
                    int isFavorite = cursor.getInt(cursor.getColumnIndex(KEY_FILE_IS_FAVORITE));
                    file.setIsFavorite((isFavorite == 1) ? true : false);

                    // adding to file list
                    files.add(file);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return files;
    }

    /*
 * getting all files under single tag
 * */
    public List<File> getAllFilesByTag(String tag_name) {
        List<File> files = new ArrayList<File>();

        String selectQuery = "SELECT  * FROM " + TABLE_FILES + " td, "
                + TABLE_TAGS + " tg, " + TABLE_FILES_TAGS + " tt WHERE tg."
                + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_FILE_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    File file = new File();
                    file.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    file.setFileName((cursor.getString(cursor.getColumnIndex(KEY_FILE_NAME))));
                    int isFavorite = cursor.getInt(cursor.getColumnIndex(KEY_FILE_IS_FAVORITE));
                    file.setIsFavorite((isFavorite == 1) ? true : false);

                    // adding to todo list
                    files.add(file);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return files;
    }

    /*
 * Updating a file
 */
    public int updateFile(File file) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_FILE_NAME, file.getFileName());
        //values.put(KEY_FILE_TITLE, file.getFileTitle());
        values.put(KEY_FILE_IS_FAVORITE, file.getIsFavorite());

        // updating row
        int rowsAffected = db.update(TABLE_FILES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(file.getId())});
        db.close();
        return rowsAffected;
    }

    /*
     * Creating tag
     */
    public long createTag(Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_NAME, tag.getTagName());

        // insert row
        long tag_id = db.insert(TABLE_TAGS, null, values);
        db.close();
        return tag_id;
    }

    /**
     * getting all tags
     * */
    public ArrayList<Tag> getAllTags() {
        ArrayList<Tag> tags = new ArrayList<Tag>();
        String selectQuery = "SELECT  * FROM " + TABLE_TAGS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Tag tag = new Tag();
                    tag.setId(cursor.getInt((cursor.getColumnIndex(KEY_ID))));
                    tag.setTagName(cursor.getString(cursor.getColumnIndex(KEY_TAG_NAME)));

                    // adding to tags list
                    tags.add(tag);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return tags;
    }

    /*
     * Deleting a tag
     */
    public void deleteTag(Tag tag, boolean should_delete_all_tag_todos) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting tag
        // check if todos under this tag should also be deleted
        //if (should_delete_all_tag_todos) {
        // get all todos under this tag
        //List<File> allTagFiles = getAllFilesByTag(tag.getTagName());

        // delete all todos
        //for (File file : allTagFiles) {
        // delete file
        //deleteFiles(file.getId());
        //}
        //}

        // now delete the tag
        db.delete(TABLE_TAGS, KEY_ID + " = ?",
                new String[]{String.valueOf(tag.getId())});
        db.close();
    }

    /*
     * Creating file_tag
     */
    public long createFileTag(long todo_id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_ID, todo_id);
        values.put(KEY_TAG_ID, tag_id);

        long id = db.insert(TABLE_FILES_TAGS, null, values);
        db.close();
        return id;
    }

    /*
 * Updating a file tag
 */
    public int updateFileTag(long id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_ID, tag_id);

        // updating row
        int rowsAffected = db.update(TABLE_FILES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id) });
        db.close();
        return rowsAffected;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
