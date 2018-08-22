package tech.wendler.commission_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String TABLE_NAME = "Transactions";
    private final static String COL0 = "transID";
    private final static String COL1 = "date";
    private final static String COL2 = "new_phones";
    private final static String COL3 = "upgrade_phones";
    private final static String COL4 = "tablets_etc";
    private final static String COL5 = "hum";
    private final static String COL6 = "connected_devices_etc";
    private final static String COL7 = "new_tmp";
    private final static String COL8 = "new_multi_tmp";
    private final static String COL9 = "revenue";
    private final static String COL10 = "sales_bucket";

    private int totalTablets = 0, totalConnected = 0, totalHum = 0, totalTMP = 0,
            totalNewPhones = 0, totalUpgPhones = 0, transID, boolNewMultiTMP = 0;
    private double totalRev = 0, totalBucketAchieved = 0;
    private boolean newMultiTMP;

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " DATETIME, " +
                COL2 + " INTEGER, " +
                COL3 + " INTEGER, " +
                COL4 + " INTEGER, " +
                COL5 + " INTEGER, " +
                COL6 + " INTEGER, " +
                COL7 + " INTEGER, " +
                COL8 + " BOOLEAN, " +
                COL9 + " DOUBLE, " +
                COL10 + " DOUBLE);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String date, int newPhones, int upgPhones, int tablets, int hum,
                           int CD, int tmp, boolean multiTMP, double rev, double salesBucket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, date);
        contentValues.put(COL2, newPhones);
        contentValues.put(COL3, upgPhones);
        contentValues.put(COL4, tablets);
        contentValues.put(COL5, hum);
        contentValues.put(COL6, CD);
        contentValues.put(COL7, tmp);
        contentValues.put(COL8, multiTMP);
        contentValues.put(COL9, rev);
        contentValues.put(COL10, salesBucket);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(String queryString) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery(queryString, null);
        return data;
    }

    public void updateTransaction(Transaction editedTrans) {
        SQLiteDatabase db = this.getReadableDatabase();
        totalNewPhones = editedTrans.getTotalNewPhones();
        totalUpgPhones = editedTrans.getTotalUpgPhones();
        totalTablets = editedTrans.getTotalTablets();
        totalHum = editedTrans.getTotalHum();
        totalConnected = editedTrans.getTotalConnected();
        totalTMP = editedTrans.getTotalTMP();
        totalRev = editedTrans.getTotalRev();
        totalBucketAchieved = editedTrans.getTotalSalesDollars();
        transID = editedTrans.getTransactionID();

        if (editedTrans.isNewMultiTMP()) {
            boolNewMultiTMP = 1;
        } else {
            boolNewMultiTMP = 0;
        }

        String queryString = "UPDATE Transactions " +
                "SET new_phones = '" + totalNewPhones +
                "', upgrade_phones = '" + totalUpgPhones +
                "', tablets_etc = '" + totalTablets +
                "', hum = '" + totalHum +
                "', connected_devices_etc = '" + totalConnected +
                "', new_tmp = '" + totalTMP +
                "', new_multi_tmp = '" + boolNewMultiTMP +
                "', revenue = " + totalRev +
                ", sales_bucket = " + totalBucketAchieved +
                " WHERE transID = " + transID + ";";

        db.execSQL(queryString);

        db.close();
    }

    public void deleteTransaction(int transIdToBeDeleted) {
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "DELETE FROM Transactions WHERE transID = " + transIdToBeDeleted + ";";
        db.execSQL(queryString);

        db.close();
    }
}
