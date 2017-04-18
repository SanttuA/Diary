package com.example.santtu.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class maintains the database connection and handles adding, removing, updating and fetching diary entries
 */

public class DiaryDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_ENTRY };

    public DiaryDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void Open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void Close() {
        dbHelper.close();
    }

    public DiaryEntry createDiaryEntry(String date) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DATE, date);
        values.put(MySQLiteHelper.COLUMN_ENTRY, "");
        long insertId = database.insert(MySQLiteHelper.TABLE_DIARY, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_DIARY,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        DiaryEntry newEntry = cursorToEntry(cursor);
        cursor.close();
        return newEntry;
    }

    public void deleteEntry(DiaryEntry entry) {
        long id = entry.getId();
        System.out.println("Entry deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_DIARY, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void updateDiaryEntry(DiaryEntry entry)
    {
        long id = entry.getId();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ENTRY, entry.getDiaryEntry());
        System.out.println("Updating diary entry with: "+entry.getDiaryEntry());
        int testInt = database.update(MySQLiteHelper.TABLE_DIARY, values, MySQLiteHelper.COLUMN_ID + " = ?",
                new String[] {String.valueOf(id)});

        System.out.println("TestInt: "+testInt);
    }

    public List<DiaryEntry> getAllEntries() {
        List<DiaryEntry> entries = new ArrayList<DiaryEntry>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_DIARY,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DiaryEntry entry = cursorToEntry(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    public DiaryEntry getDiaryEntryById(long id) {

        Cursor cursor = database.query(MySQLiteHelper.TABLE_DIARY, allColumns, MySQLiteHelper.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DiaryEntry entry = cursorToEntry(cursor);
        return entry;
    }

    public int getDiaryEntryCount() {
        String countQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_DIARY;
        Cursor cursor = database.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    private DiaryEntry cursorToEntry(Cursor cursor) {
        DiaryEntry entry = new DiaryEntry();
        entry.setId(cursor.getLong(0));
        entry.setDate(cursor.getString(1));
        entry.setDiaryEntry(cursor.getString((2)));
        return entry;
    }

}
