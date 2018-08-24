package tech.wendler.commission_tracker;

import android.app.AlertDialog;
import android.database.Cursor;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyTotals extends Fragment {

    private TextView lblQuotaNewPhones, lblQuotaUpgPhones, lblQuotaBucket,
            lblCurrentNewPhones, lblCurrentUpgPhones, lblCurrentBucket, lblMonthlyTablets,
            lblMonthlyRev, lblMonthlyHum, lblMonthlyTMP, lblMonthlyCD, lblMonthlyMultiTMP;
    private Button btnAddEditQuota, btnSelectYear;
    private Spinner monthSelectorSpinner;
    private int selectedMonth, selectedYear;
    private String selectedMonthString;
    private Calendar calendar = Calendar.getInstance();
    private DatabaseHelper databaseHelper;
    private AlertDialog alert;

    public MonthlyTotals() {

    }

    public static MonthlyTotals newInstance() {
        MonthlyTotals fragment = new MonthlyTotals();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly_totals, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseHelper = new DatabaseHelper(getContext());

        btnAddEditQuota = getView().findViewById(R.id.btnEditQuota);
        btnSelectYear = getView().findViewById(R.id.btnSelectYear);
        lblQuotaNewPhones = getView().findViewById(R.id.lblQuotaNewPhones);
        lblQuotaUpgPhones = getView().findViewById(R.id.lblQuotaUpgPhones);
        lblQuotaBucket = getView().findViewById(R.id.lblQuotaBucket);
        lblCurrentNewPhones = getView().findViewById(R.id.lblCurrentNewPhones);
        lblCurrentUpgPhones = getView().findViewById(R.id.lblCurrentUpgPhones);
        lblCurrentBucket = getView().findViewById(R.id.lblCurrentBucket);
        lblMonthlyTablets = getView().findViewById(R.id.lblMonthlyTablets);
        lblMonthlyCD = getView().findViewById(R.id.lblMonthlyCD);
        lblMonthlyHum = getView().findViewById(R.id.lblMonthlyHum);
        lblMonthlyRev = getView().findViewById(R.id.lblMonthlyRevenue);
        lblMonthlyTMP = getView().findViewById(R.id.lblMonthlyTMP);
        lblMonthlyMultiTMP = getView().findViewById(R.id.lblMonthlyMultiTMP);

        //Instantiates month selection spinner
        monthSelectorSpinner = getView().findViewById(R.id.monthSelector);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource
                (getContext(), R.array.months, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        monthSelectorSpinner.setAdapter(arrayAdapter);

        //Automatically assigns current month on load
        selectedMonthString = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedYear = calendar.get(Calendar.YEAR);
        monthSelectorSpinner.setSelection(selectedMonth);
        btnSelectYear.setText("" + selectedYear);

        populateData();

        monthSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Gets selected month, displays the word, refreshes data
                selectedMonth = parent.getSelectedItemPosition();
                selectedMonthString = new DateFormatSymbols(Locale.US)
                        .getMonths()[selectedMonth];
                populateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAddEditQuota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuotaDialog();
            }
        });

        btnSelectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] years = {"2018", "2019"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(),
                        R.style.AlertDialogTheme);

                //Auto-selects the current year
                int checkedItem;
                if (selectedYear == 2018) {
                    checkedItem = 0;
                } else {
                    checkedItem = 1;
                }

                dialog.setTitle("Year Selection");

                dialog.setSingleChoiceItems(years, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                selectedYear = 2018;
                                btnSelectYear.setText("2018");
                                populateData();
                                break;
                            case 1:
                                selectedYear = 2019;
                                btnSelectYear.setText("2019");
                                populateData();
                                break;
                        }
                        alert.dismiss();
                    }
                });
                alert = dialog.create();
                alert.show();
            }
        });
    }

    private void populateData() {
        //Returns monthly quota
        String queryString = "SELECT new_phone_quota, upgrade_phone_quota, sales_bucket_quota " +
                "FROM Quota WHERE month LIKE '" + selectedMonth + "' AND year LIKE '"
                + selectedYear + "';";
        int newPhones = 0, totalNewPhones = 0, upgPhones = 0, totalUpgPhones = 0,
                totalTablets = 0, totalHum = 0, totalCD = 0, totalTMP = 0, totalRev = 0,
                totalMultiTMP = 0;
        double salesBucket = 0, totalSalesBucket = 1;

        Cursor quotaCursor = databaseHelper.getData(queryString);

        try {
            while (quotaCursor.moveToNext()) {
                newPhones = quotaCursor.getInt(quotaCursor.getColumnIndex("new_phone_quota"));
                upgPhones = quotaCursor.getInt(quotaCursor.getColumnIndex("upgrade_phone_quota"));
                salesBucket = quotaCursor.getDouble(quotaCursor.getColumnIndex("sales_bucket_quota"));
            }
        } finally {
            quotaCursor.close();
        }

        int selectedMonthQuery = selectedMonth + 1;
        String formattedMonth = String.format("%02d", selectedMonthQuery);

        //Returns sum of monthly totals
        queryString = "SELECT total(new_phones) AS colNewPhones, " +
                "total(upgrade_phones) AS colUpgPhones, " +
                "total(sales_bucket) AS colSalesBucket, " +
                "total(tablets_etc) AS colTablets, " +
                "total(hum) AS colHum, " +
                "total(connected_devices_etc) AS colCD, " +
                "total(new_tmp) AS colTMP, " +
                "total(revenue) AS colRev FROM Transactions " +
                "WHERE date LIKE '" + selectedYear + "-" + formattedMonth + "%';";

        Cursor currentCursor = databaseHelper.getData(queryString);

        try {
            while (currentCursor.moveToNext()) {
                totalNewPhones = currentCursor.getInt(currentCursor.getColumnIndex("colNewPhones"));
                totalUpgPhones = currentCursor.getInt(currentCursor.getColumnIndex("colUpgPhones"));
                totalSalesBucket = currentCursor.getDouble(currentCursor.getColumnIndex("colSalesBucket"));
                totalTablets = currentCursor.getInt(currentCursor.getColumnIndex("colTablets"));
                totalHum = currentCursor.getInt(currentCursor.getColumnIndex("colHum"));
                totalCD = currentCursor.getInt(currentCursor.getColumnIndex("colCD"));
                totalTMP = currentCursor.getInt(currentCursor.getColumnIndex("colTMP"));
                totalRev = currentCursor.getInt(currentCursor.getColumnIndex("colRev"));
            }
        } finally {
            currentCursor.close();
        }

        //Returns the sum of all true values in the new multi TMP column
        queryString = "SELECT COUNT(new_multi_tmp) AS newMultiTMP FROM Transactions " +
                "WHERE new_multi_tmp = 1 AND date LIKE " +
                "'" + selectedYear + "-" + formattedMonth + "%';";

        Cursor multiTMPCursor = databaseHelper.getData(queryString);

        try {
            while (multiTMPCursor.moveToNext()) {
                totalMultiTMP = multiTMPCursor.getInt(multiTMPCursor.getColumnIndex("newMultiTMP"));
            }
        } finally {
            multiTMPCursor.close();
        }

        lblQuotaNewPhones.setText("" + newPhones);
        lblQuotaUpgPhones.setText("" + upgPhones);
        lblQuotaBucket.setText(formatCurrency(salesBucket));
        lblCurrentNewPhones.setText("" + totalNewPhones);
        lblCurrentUpgPhones.setText("" + totalUpgPhones);
        lblCurrentBucket.setText(formatCurrency(totalSalesBucket));
        lblMonthlyTablets.setText("" + totalTablets);
        lblMonthlyRev.setText(formatCurrency(totalRev));
        lblMonthlyHum.setText("" + totalHum);
        lblMonthlyCD.setText("" + totalCD);
        lblMonthlyTMP.setText("" + totalTMP);
        lblMonthlyMultiTMP.setText("" + totalMultiTMP);
    }

    //Formats sales dollars entry into US currency
    private String formatCurrency(double numToFormat) {
        Locale locale = Locale.US;
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        return fmt.format(numToFormat);
    }

    private void addQuotaDialog() {
        //Creates layout & views for dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(),
                R.style.AlertDialogTheme);
        TextView lblNewPhone = new TextView(getContext());
        TextView lblUpgPhone = new TextView(getContext());
        TextView lblSalesBucket = new TextView(getContext());
        final EditText txtNewPhones = new EditText(getContext());
        final EditText txtUpgPhones = new EditText(getContext());
        final EditText txtSalesBucket = new EditText(getContext());

        lblNewPhone.setText("New Phones:");
        lblUpgPhone.setText("Upgrade Phones:");
        lblSalesBucket.setText("Sales Bucket:");
        txtNewPhones.setHint("0");
        txtUpgPhones.setHint("0");
        txtSalesBucket.setHint("0");
        txtNewPhones.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtUpgPhones.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtSalesBucket.setInputType(InputType.TYPE_CLASS_NUMBER);

        lblNewPhone.setPadding(40, 40, 40, 40);
        lblUpgPhone.setPadding(40, 40, 40, 40);
        lblSalesBucket.setPadding(40, 40, 40, 40);
        txtNewPhones.setPadding(40, 40, 40, 40);
        txtUpgPhones.setPadding(40, 40, 40, 40);
        txtSalesBucket.setPadding(40, 40, 40, 40);

        lblNewPhone.setTextColor(Color.DKGRAY);
        lblUpgPhone.setTextColor(Color.DKGRAY);
        lblSalesBucket.setTextColor(Color.DKGRAY);

        LinearLayout dialogLayout = new LinearLayout(getContext());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(lblNewPhone);
        dialogLayout.addView(txtNewPhones);
        dialogLayout.addView(lblUpgPhone);
        dialogLayout.addView(txtUpgPhones);
        dialogLayout.addView(lblSalesBucket);
        dialogLayout.addView(txtSalesBucket);
        dialogLayout.setPadding(40, 40, 40, 40);

        dialog.setView(dialogLayout);

        dialog.setTitle("New quota for " + selectedMonthString + ", " + selectedYear);

        dialog.setPositiveButton("Save Quota", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newPhoneQuota = 0, upgPhoneQuota = 0;
                double bucketQuota = 0;
                boolean badNum = false;

                //Checks for empty fields
                if (txtNewPhones.length() > 0 && txtUpgPhones.length() > 0
                        && txtSalesBucket.length() > 0) {
                    try {
                        newPhoneQuota = Integer.parseInt(txtNewPhones.getText().toString());
                        upgPhoneQuota = Integer.parseInt(txtUpgPhones.getText().toString());
                        bucketQuota = Double.parseDouble(txtSalesBucket.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        badNum = true;
                    }

                    if (!badNum) {
                        //Adds data
                        if (databaseHelper.addQuotaData(selectedMonth, selectedYear,
                                newPhoneQuota, upgPhoneQuota, bucketQuota)) {
                            Toast.makeText(getContext(), "Quota added successfully",
                                    Toast.LENGTH_SHORT).show();

                            //Updates fragment if save was successful
                            populateData();
                        } else {
                            Toast.makeText(getContext(), "Error while saving quota",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Don't do that.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please fill in all fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //User cancelled, nothing happens
                    }
                });
        final AlertDialog alert = dialog.create();
        alert.show();
    }
}
