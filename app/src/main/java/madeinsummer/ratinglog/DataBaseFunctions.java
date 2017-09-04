package madeinsummer.ratinglog;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;


public class DataBaseFunctions {

    private static final String table_name = "products";

    private static void createTableIfNotExist() {
        try {
            MainActivity.myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + table_name + " (name VARCHAR, rate VARCHAR, datetime VARCHAR, image BLOB)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFromDatabase() {
        try {
            createTableIfNotExist();
            MainActivity.products = new ArrayList<>();
            MainActivity.ratings = new ArrayList<>();
            MainActivity.images = new ArrayList<>();
            MainActivity.dates  = new ArrayList<>();
            Cursor c = MainActivity.myDatabase.rawQuery("SELECT * FROM " + table_name, null);
            c.moveToFirst();
            int name_index = c.getColumnIndex("name");
            int rate_index = c.getColumnIndex("rate");
            int datetime_index = c.getColumnIndex("datetime");
            int image_index = c.getColumnIndex("image");
            while (c.getPosition() < c.getCount()) {
                MainActivity.products.add(c.getString(name_index));
                MainActivity.ratings.add(c.getString(rate_index));
                MainActivity.dates.add(c.getString(datetime_index));
                MainActivity.images.add(c.getBlob(image_index));
                c.moveToNext();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveItemToDatabase(String name, String rate, String datetime, byte[] image) {
        try {
            createTableIfNotExist();
            String sql = "INSERT INTO " + table_name + " (name, rate, datetime, image) VALUES (?, ?, ?, ?)";
            SQLiteStatement statement = MainActivity.myDatabase.compileStatement(sql);
            statement.bindString(1, name);
            statement.bindString(2, rate);
            statement.bindString(3, datetime);
            statement.bindBlob(4, image);
            statement.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteItemFromDatabase(String datetime) {
        try {
            MainActivity.myDatabase.execSQL("DELETE FROM " + table_name + " where datetime = '" + String.valueOf(datetime) + "'");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dropTable() {
        try {
            MainActivity.myDatabase.execSQL("DROP TABLE " + table_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}