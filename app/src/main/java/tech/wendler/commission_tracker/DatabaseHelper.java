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
    private final static String COL11 = "customer_name";
    private final static String COL12 = "phone_number";
    private final static String COL13 = "order_number";
    private final static String COL14 = "sales_force_leads";
    private final static String COL15 = "rep_assisted_order";
    private final static String COL16 = "direct_fulfillment_order";
    private final static String COL17 = "in_store_pickup_order";
    private final static String COL18 = "pre_order";
    private final static String COL19 = "extra_sales_dollars";

    private final static String QUOTA_TABLE_NAME = "Quota";
    private final static String QUOTA_COL0 = "month";
    private final static String QUOTA_COL1 = "year";
    private final static String QUOTA_COL2 = "new_phone_quota";
    private final static String QUOTA_COL3 = "upgrade_phone_quota";
    private final static String QUOTA_COL4 = "sales_bucket_quota";
    private final static String QUOTA_COL5 = "paycheck_target";
    private final static String QUOTA_COL6 = "new_phone_chargeback";
    private final static String QUOTA_COL7 = "upgrade_phone_chargeback";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
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
                COL10 + " DOUBLE, " +
                COL11 + " TEXT, " +
                COL12 + " TEXT, " +
                COL13 + " TEXT, " +
                COL14 + " INTEGER, " +
                COL15 + " BOOLEAN, " +
                COL16 + " BOOLEAN, " +
                COL17 + " BOOLEAN, " +
                COL18 + " BOOLEAN, " +
                COL19 + " DOUBLE);";

        String createQuotaTable = "CREATE TABLE " + QUOTA_TABLE_NAME + " (" +
                QUOTA_COL0 + " INTEGER NOT NULL, " +
                QUOTA_COL1 + " INTEGER NOT NULL, " +
                QUOTA_COL2 + " INTEGER, " +
                QUOTA_COL3 + " INTEGER, " +
                QUOTA_COL4 + " DOUBLE, " +
                QUOTA_COL5 + " DOUBLE, " +
                QUOTA_COL6 + " INTEGER, " +
                QUOTA_COL7 + " INTEGER, " +
                "PRIMARY KEY (" + QUOTA_COL0 + ", " + QUOTA_COL1 + "));";

        db.execSQL(createTransactionTable);
        db.execSQL(createQuotaTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                //Adds column to quota table for paycheck estimation
                db.execSQL("ALTER TABLE " + QUOTA_TABLE_NAME + " ADD COLUMN paycheck_target DOUBLE");
                break;
            case 2:
                db.execSQL("ALTER TABLE " + QUOTA_TABLE_NAME + " ADD COLUMN new_phone_chargeback INTEGER");
                db.execSQL("ALTER TABLE " + QUOTA_TABLE_NAME + " ADD COLUMN upgrade_phone_chargeback INTEGER");
                break;
            case 4:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL11 + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL12 + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL13 + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL14 + " INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL15 + " BOOLEAN");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL16 + " BOOLEAN");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL17 + " BOOLEAN");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL18 + " BOOLEAN");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL19 + " DOUBLE");
        }
    }

    boolean addTransactionData(String date, Transaction transaction, TransactionInfo extraInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, date);
        contentValues.put(COL2, transaction.getTotalNewPhones());
        contentValues.put(COL3, transaction.getTotalUpgPhones());
        contentValues.put(COL4, transaction.getTotalTablets());
        contentValues.put(COL5, transaction.getTotalHum());
        contentValues.put(COL6, transaction.getTotalConnected());
        contentValues.put(COL7, transaction.getTotalTMP());
        contentValues.put(COL8, transaction.isNewMultiTMP());
        contentValues.put(COL9, transaction.getTotalRev());
        contentValues.put(COL10, transaction.getTotalSalesDollars());
        contentValues.put(COL11, extraInfo.getCustomerName());
        contentValues.put(COL12, extraInfo.getPhoneNumber());
        contentValues.put(COL13, extraInfo.getOrderNumber());
        contentValues.put(COL14, extraInfo.getSalesForceLeads());
        contentValues.put(COL15, extraInfo.isRepAssistedOrder());
        contentValues.put(COL16, extraInfo.isdFillOrder());
        contentValues.put(COL17, extraInfo.isInStorePickupOrder());
        contentValues.put(COL18, extraInfo.isPreOrder());
        contentValues.put(COL19, extraInfo.getExtraSalesDollars());

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    boolean addQuotaData(int month, int year, int newPhones, int upgPhones,
                         double salesBucket, double paycheckTarget, int newPhoneCB,
                         int upgPhoneCB) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(QUOTA_COL0, month);
        contentValues.put(QUOTA_COL1, year);
        contentValues.put(QUOTA_COL2, newPhones);
        contentValues.put(QUOTA_COL3, upgPhones);
        contentValues.put(QUOTA_COL4, salesBucket);
        contentValues.put(QUOTA_COL5, paycheckTarget);
        contentValues.put(QUOTA_COL6, newPhoneCB);
        contentValues.put(QUOTA_COL7, upgPhoneCB);

        //If quota already exists for a given month, replace it with the new one
        long result = db.insertWithOnConflict(QUOTA_TABLE_NAME, null, contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);

        return result != -1;
    }

    Cursor getData(String queryString) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(queryString, null);
    }

    void updateTransaction(Transaction editedTrans, TransactionInfo editedTransInfo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String custName, phoneNum, orderNum;
        int totalTablets, totalConnected, totalHum, totalTMP,
                totalNewPhones, totalUpgPhones, transID, boolNewMultiTMP,
                salesForceLeads, repAssisted, dFill, ispu, preOrder;
        double totalRev, totalBucketAchieved, extraSalesDollars;


        totalNewPhones = editedTrans.getTotalNewPhones();
        totalUpgPhones = editedTrans.getTotalUpgPhones();
        totalTablets = editedTrans.getTotalTablets();
        totalHum = editedTrans.getTotalHum();
        totalConnected = editedTrans.getTotalConnected();
        totalTMP = editedTrans.getTotalTMP();
        totalRev = editedTrans.getTotalRev();
        totalBucketAchieved = editedTrans.getTotalSalesDollars();
        transID = editedTrans.getTransactionID();

        custName = editedTransInfo.getCustomerName();
        phoneNum = editedTransInfo.getPhoneNumber();
        orderNum = editedTransInfo.getOrderNumber();
        salesForceLeads = editedTransInfo.getSalesForceLeads();
        repAssisted = editedTransInfo.isRepAssistedOrder() ? 1 : 0;
        dFill = editedTransInfo.isdFillOrder() ? 1 : 0;
        ispu = editedTransInfo.isInStorePickupOrder() ? 1 : 0;
        preOrder = editedTransInfo.isPreOrder() ? 1 : 0;
        extraSalesDollars = editedTransInfo.getExtraSalesDollars();

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
                ", customer_name = '" + custName +
                "', phone_number = '" + phoneNum +
                "', order_number = '" + orderNum +
                "', sales_force_leads = '" + salesForceLeads +
                "', rep_assisted_order = '" + repAssisted +
                "', direct_fulfillment_order = '" + dFill +
                "', in_store_pickup_order = '" + ispu +
                "', pre_order = '" + preOrder +
                "', extra_sales_dollars = " + extraSalesDollars +
                " WHERE transID = " + transID + ";";

        db.execSQL(queryString);

        db.close();
    }

    void deleteTransaction(int transIdToBeDeleted) {
        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "DELETE FROM Transactions WHERE transID = " + transIdToBeDeleted + ";";
        db.execSQL(queryString);

        db.close();
    }
}