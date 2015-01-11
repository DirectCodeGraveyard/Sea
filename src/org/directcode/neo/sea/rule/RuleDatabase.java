package org.directcode.neo.sea.rule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RuleDatabase {
    private final Context context;
    private RuleOpener opener;

    public RuleDatabase(Context context) {
        this.context = context;
        this.opener = new RuleOpener(context);
    }

    public List<Rule> getRules() {
        SQLiteDatabase db = opener.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM rules;", new String[0]);
        List<Rule> rules = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String trigger = cursor.getString(2);
            String action = cursor.getString(3);

            rules.add(new Rule(id, name, trigger, action));
        }
        cursor.close();
        db.close();
        return rules;
    }

    public void deleteRule(Rule rule) {
        SQLiteDatabase db = opener.getWritableDatabase();
        db.delete("rules", "id=" + rule.getId(), null);
        db.close();
    }

    public Rule createRule(String name, String trigger, String action) {
        SQLiteDatabase db = opener.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("trigger", trigger);
        values.put("action", action);
        int id = (int) db.insert("rules", null, values);
        db.close();
        return new Rule(id, name, trigger, action);
    }

    private class RuleOpener extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;

        public RuleOpener(Context context) {
            super(context, "rules", null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE rules (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT, trigger TEXT, action TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
