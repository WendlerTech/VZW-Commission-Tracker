package tech.wendler.commission_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "Commission_Tracker_Database";
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

    private final static String QUOTA_TABLE_NAME = "Quota";
    private final static String QUOTA_COL0 = "month";
    private final static String QUOTA_COL1 = "year";
    private final static String QUOTA_COL2 = "new_phone_quota";
    private final static String QUOTA_COL3 = "upgrade_phone_quota";
    private final static String QUOTA_COL4 = "sales_bucket_quota";
    private final static String QUOTA_COL5 = "paycheck_target";

    private int totalTablets = 0, totalConnected = 0, totalHum = 0, totalTMP = 0,
            totalNewPhones = 0, totalUpgPhones = 0, transID, boolNewMultiTMP = 0;
    private double totalRev = 0, totalBucketAchieved = 0;
    private boolean newMultiTMP;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTransactionTable = "CREATE TABLE " + TABLE_NAME + " (" +
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

 /*       String createQuotaTable = "CREATE TABLE " + QUOTA_TABLE_NAME + " (" +
                QUOTA_COL0 + " INTEGER NOT NULL, " +
                QUOTA_COL1 + " INTEGER NOT NULL, " +
                QUOTA_COL2 + " INTEGER, " +
                QUOTA_COL3 + " INTEGER, " +
                QUOTA_COL4 + " DOUBLE, PRIMARY KEY (" +
                QUOTA_COL0 + ", " + QUOTA_COL1 + "));";*/



        String createQuotaTable = "CREATE TABLE " + QUOTA_TABLE_NAME + " (" +
                QUOTA_COL0 + " INTEGER NOT NULL, " +
                QUOTA_COL1 + " INTEGER NOT NULL, " +
                QUOTA_COL2 + " INTEGER, " +
                QUOTA_COL3 + " INTEGER, " +
                QUOTA_COL4 + " DOUBLE, " +
                QUOTA_COL5 + " DOUBLE, " +
                "PRIMARY KEY (" + QUOTA_COL0 + ", " + QUOTA_COL1 + "));";

        db.execSQL(createTransactionTable);
        db.execSQL(createQuotaTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                //Adds column to quota table for paycheck estimation
                db.execSQL("ALTER TABLE " + QUOTA_TABLE_NAME + " ADD COLUMN paycheck_target DOUBLE");
                break;
        }
    }

    public boolean addTransactionData(String date, int newPhones, int upgPhones, int tablets, int hum,
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

    public boolean addQuotaData(int month, int year, int newPhones, int upgPhones,
                                double salesBucket, double paycheckTarget) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(QUOTA_COL0, month);
        contentValues.put(QUOTA_COL1, year);
        contentValues.put(QUOTA_COL2, newPhones);
        contentValues.put(QUOTA_COL3, upgPhones);
        contentValues.put(QUOTA_COL4, salesBucket);
        contentValues.put(QUOTA_COL5, paycheckTarget);

        //If quota already exists for a given month, replace it with the new one
        long result = db.insertWithOnConflict(QUOTA_TABLE_NAME, null, contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);

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
