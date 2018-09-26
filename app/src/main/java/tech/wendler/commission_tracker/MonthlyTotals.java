package tech.wendler.commission_tracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.database.Cursor;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyTotals extends Fragment {

    private TextView lblQuotaNewPhones, lblQuotaUpgPhones, lblQuotaBucket,
            lblCurrentNewPhones, lblCurrentUpgPhones, lblCurrentBucket, lblMonthlyTablets,
            lblMonthlyRev, lblMonthlyHum, lblMonthlyTMP, lblMonthlyCD, lblMonthlyMultiTMP,
            lblMetricsTablets, lblMetricsHum, lblMetricsNewTMP, lblMetricsRev, lblMetricsCD,
            lblMetricsMultiTMP, lblShowHide, lblTargetPaycheck, lblExpectedPaycheck,
            lblPhoneMultiplier, lblSalesMultiplier, lblTargetPaycheckTitle, lblExpectedPayTitle,
            lblTargetPaySubTitle, lblPhoneMultTitle, lblSalesMultTitle;
    private Button btnSelectYear ;
    private int selectedMonth, selectedYear;
    private String selectedMonthString;
    private Calendar calendar = Calendar.getInstance();
    private DatabaseHelper databaseHelper;
    private AlertDialog alert;
    private int newPhoneQuotaGlobal = 0, upgradeQuotaGlobal = 0, totalPhonesGlobal = 0;
    private double salesDollarQuotaGlobal = 0, expectedCheckGlobal = 0, totalSalesDollarsGlobal = 0;

    public MonthlyTotals() {

    }

    public static MonthlyTotals newInstance() {
        return new MonthlyTotals();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly_totals, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btnAddEditQuota, btnMetrics, btnPaycheck;
        Spinner monthSelectorSpinner;

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

        lblMetricsTablets = getView().findViewById(R.id.lblMetricsTablet);
        lblMetricsHum = getView().findViewById(R.id.lblMetricsHum);
        lblMetricsNewTMP = getView().findViewById(R.id.lblMetricsNewTMP);
        lblMetricsRev = getView().findViewById(R.id.lblMetricsRev);
        lblMetricsCD = getView().findViewById(R.id.lblMetricsCD);
        lblMetricsMultiTMP = getView().findViewById(R.id.lblMetricsMultiTMP);
        lblShowHide = getView().findViewById(R.id.lblShowHide);
        btnMetrics = getView().findViewById(R.id.btnMetrics);
        btnPaycheck = getView().findViewById(R.id.btnPaycheck);

        lblTargetPaycheck = getView().findViewById(R.id.lblTargetPaycheck);
        lblExpectedPaycheck = getView().findViewById(R.id.lblExpectedPaycheck);
        lblPhoneMultiplier = getView().findViewById(R.id.lblPhoneMultiplier);
        lblSalesMultiplier = getView().findViewById(R.id.lblSalesMultiplier);
        lblTargetPaycheckTitle = getView().findViewById(R.id.lblTargetPayTitle);
        lblTargetPaySubTitle = getView().findViewById(R.id.lblTargetPaySubTitle);
        lblPhoneMultTitle = getView().findViewById(R.id.lblPhoneMultTitle);
        lblSalesMultTitle = getView().findViewById(R.id.lblSalesMultTitle);
        lblExpectedPayTitle = getView().findViewById(R.id.lblExpectedPayTitle);

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
        btnSelectYear.setText(String.valueOf(selectedYear));

        hideMetrics();
        hidePaycheck();
        populateData();
        calculatePaycheck();

        monthSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Gets selected month, displays the word, refreshes data
                selectedMonth = parent.getSelectedItemPosition();
                selectedMonthString = new DateFormatSymbols(Locale.US)
                        .getMonths()[selectedMonth];
                populateData();
                calculatePaycheck();
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
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                selectedYear = 2018;
                                btnSelectYear.setText("2018");
                                populateData();
                                calculatePaycheck();
                                break;
                            case 1:
                                selectedYear = 2019;
                                btnSelectYear.setText("2019");
                                populateData();
                                calculatePaycheck();
                                break;
                        }
                        alert.dismiss();
                    }
                });
                alert = dialog.create();
                alert.show();
            }
        });

        btnPaycheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lblExpectedPaycheck.getVisibility() == View.INVISIBLE) {
                    hideMetrics();
                    showPaycheck();
                } else {
                    hidePaycheck();
                    hideMetrics();
                }
            }
        });

        btnMetrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lblMetricsTablets.getVisibility() == View.VISIBLE) {
                    hideMetrics();
                } else {
                    hidePaycheck();
                    showMetrics();
                }
            }
        });
    }

    private void populateData() {
        //Returns monthly quota
        String queryString = "SELECT new_phone_quota, upgrade_phone_quota, sales_bucket_quota, " +
                "paycheck_target FROM Quota WHERE month LIKE '" + selectedMonth + "' AND year LIKE '"
                + selectedYear + "';";
        int newPhones = 0, totalNewPhones = 0, upgPhones = 0, totalUpgPhones = 0,
                totalTablets = 0, totalHum = 0, totalCD = 0, totalTMP = 0,
                totalMultiTMP = 0;
        double salesBucket = 0, totalSalesBucket = 1, expectedCheck = 0, totalRev = 0;

        try (Cursor quotaCursor = databaseHelper.getData(queryString)) {
            while (quotaCursor.moveToNext()) {
                newPhones = quotaCursor.getInt(quotaCursor.getColumnIndex("new_phone_quota"));
                upgPhones = quotaCursor.getInt(quotaCursor.getColumnIndex("upgrade_phone_quota"));
                salesBucket = quotaCursor.getDouble(quotaCursor.getColumnIndex("sales_bucket_quota"));
                expectedCheck = quotaCursor.getDouble(quotaCursor.getColumnIndex("paycheck_target"));
            }
        }

        newPhoneQuotaGlobal = newPhones;
        upgradeQuotaGlobal = upgPhones;
        salesDollarQuotaGlobal = salesBucket;
        expectedCheckGlobal = expectedCheck;

        int selectedMonthQuery = selectedMonth + 1;
        @SuppressLint("DefaultLocale") String formattedMonth = String.format("%02d", selectedMonthQuery);

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

        try (Cursor currentCursor = databaseHelper.getData(queryString)) {
            while (currentCursor.moveToNext()) {
                totalNewPhones = currentCursor.getInt(currentCursor.getColumnIndex("colNewPhones"));
                totalUpgPhones = currentCursor.getInt(currentCursor.getColumnIndex("colUpgPhones"));
                totalSalesBucket = currentCursor.getDouble(currentCursor.getColumnIndex("colSalesBucket"));
                totalTablets = currentCursor.getInt(currentCursor.getColumnIndex("colTablets"));
                totalHum = currentCursor.getInt(currentCursor.getColumnIndex("colHum"));
                totalCD = currentCursor.getInt(currentCursor.getColumnIndex("colCD"));
                totalTMP = currentCursor.getInt(currentCursor.getColumnIndex("colTMP"));
                totalRev = currentCursor.getDouble(currentCursor.getColumnIndex("colRev"));
            }
        }

        totalPhonesGlobal = totalNewPhones + totalUpgPhones;
        totalSalesDollarsGlobal = totalSalesBucket;

        //Returns the sum of all true values in the new multi TMP column
        queryString = "SELECT COUNT(new_multi_tmp) AS newMultiTMP FROM Transactions " +
                "WHERE new_multi_tmp = 1 AND date LIKE " +
                "'" + selectedYear + "-" + formattedMonth + "%';";

        try (Cursor multiTMPCursor = databaseHelper.getData(queryString)) {
            while (multiTMPCursor.moveToNext()) {
                totalMultiTMP = multiTMPCursor.getInt(multiTMPCursor.getColumnIndex("newMultiTMP"));
            }
        }

        if (expectedCheck == 0) {
            lblTargetPaycheck.setText(R.string.missing_paycheck_target_msg);

        } else {
            lblTargetPaycheck.setText(formatCurrency(expectedCheck));
        }

        lblQuotaNewPhones.setText(String.valueOf(newPhones));
        lblQuotaUpgPhones.setText(String.valueOf(upgPhones));
        lblQuotaBucket.setText(formatCurrency(salesBucket));
        lblCurrentNewPhones.setText(String.valueOf(totalNewPhones));
        lblCurrentUpgPhones.setText(String.valueOf(totalUpgPhones));
        lblCurrentBucket.setText(formatCurrency(totalSalesBucket));
        lblMonthlyTablets.setText(String.valueOf(totalTablets));
        lblMonthlyRev.setText(formatCurrency(totalRev));
        lblMonthlyHum.setText(String.valueOf(totalHum));
        lblMonthlyCD.setText(String.valueOf(totalCD));
        lblMonthlyTMP.setText(String.valueOf(totalTMP));
        lblMonthlyMultiTMP.setText(String.valueOf(totalMultiTMP));
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
        TextView lblExpectedCheck = new TextView(getContext());
        final EditText txtNewPhones = new EditText(getContext());
        final EditText txtUpgPhones = new EditText(getContext());
        final EditText txtSalesBucket = new EditText(getContext());
        final EditText txtExpectedCheck = new EditText(getContext());

        lblNewPhone.setText(R.string.new_phones);
        lblUpgPhone.setText(R.string.upgrade_phones);
        lblSalesBucket.setText(R.string.sales_bucket);
        lblExpectedCheck.setText(R.string.paycheck_at_risk);
        txtNewPhones.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtUpgPhones.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtSalesBucket.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtExpectedCheck.setInputType(InputType.TYPE_CLASS_NUMBER);

        //Auto-fills fields if quota was previously entered
        if (newPhoneQuotaGlobal == 0) {
            txtNewPhones.setHint("0");
        } else {
            txtNewPhones.setText(String.valueOf(newPhoneQuotaGlobal));
        }

        if (upgradeQuotaGlobal == 0) {
            txtUpgPhones.setHint("0");
        } else {
            txtUpgPhones.setText(String.valueOf(upgradeQuotaGlobal));
        }

        if (salesDollarQuotaGlobal == 0) {
            txtSalesBucket.setHint("0");
        } else {
            txtSalesBucket.setText(String.valueOf((int) salesDollarQuotaGlobal));
        }

        if (expectedCheckGlobal == 0) {
            txtExpectedCheck.setHint("0");
        } else {
            txtExpectedCheck.setText(String.valueOf((int) expectedCheckGlobal));
        }

        lblNewPhone.setPadding(40, 40, 40, 40);
        lblUpgPhone.setPadding(40, 40, 40, 40);
        lblSalesBucket.setPadding(40, 40, 40, 40);
        lblExpectedCheck.setPadding(40, 40, 40, 40);
        txtNewPhones.setPadding(40, 40, 40, 40);
        txtUpgPhones.setPadding(40, 40, 40, 40);
        txtSalesBucket.setPadding(40, 40, 40, 40);
        txtExpectedCheck.setPadding(40, 40, 40, 40);

        lblNewPhone.setTextColor(Color.DKGRAY);
        lblUpgPhone.setTextColor(Color.DKGRAY);
        lblSalesBucket.setTextColor(Color.DKGRAY);
        lblExpectedCheck.setTextColor(Color.DKGRAY);

        LinearLayout dialogLayout = new LinearLayout(getContext());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(lblNewPhone);
        dialogLayout.addView(txtNewPhones);
        dialogLayout.addView(lblUpgPhone);
        dialogLayout.addView(txtUpgPhones);
        dialogLayout.addView(lblSalesBucket);
        dialogLayout.addView(txtSalesBucket);
        dialogLayout.addView(lblExpectedCheck);
        dialogLayout.addView(txtExpectedCheck);
        dialogLayout.setPadding(40, 40, 40, 40);

        dialog.setView(dialogLayout);

        dialog.setTitle("New quota for " + selectedMonthString + ", " + selectedYear);

        dialog.setPositiveButton("Save Quota", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newPhoneQuota = 0, upgPhoneQuota = 0;
                double bucketQuota = 0, expectedCheck = 0;
                boolean badNum = false;

                //Checks for empty fields
                if (txtNewPhones.length() > 0 && txtUpgPhones.length() > 0
                        && txtSalesBucket.length() > 0 && txtExpectedCheck.length() > 0) {
                    try {
                        newPhoneQuota = Integer.parseInt(txtNewPhones.getText().toString());
                        upgPhoneQuota = Integer.parseInt(txtUpgPhones.getText().toString());
                        bucketQuota = Double.parseDouble(txtSalesBucket.getText().toString());
                        expectedCheck = Double.parseDouble(txtExpectedCheck.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        badNum = true;
                    }

                    if (!badNum) {
                        //Adds data
                        if (databaseHelper.addQuotaData(selectedMonth, selectedYear,
                                newPhoneQuota, upgPhoneQuota, bucketQuota, expectedCheck)) {
                            Toast.makeText(getContext(), "Quota added successfully",
                                    Toast.LENGTH_SHORT).show();

                            //Updates fragment if save was successful
                            populateData();
                            calculatePaycheck();
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

    private void calculatePaycheck() {
        DecimalFormat format = new DecimalFormat("##.00");
        int netPhoneQuota = newPhoneQuotaGlobal + upgradeQuotaGlobal;
        double phoneMultiplier, salesMultiplier, expectedPaycheck;
        double currentPhoneQuota = (double) totalPhonesGlobal / (double) netPhoneQuota;

        //Determines phone multiplier based on currently achieved percentage
        if (currentPhoneQuota < .60) {
            phoneMultiplier = 0;
        } else if (currentPhoneQuota < .80) {
            phoneMultiplier = .75;
        } else if (currentPhoneQuota < 1) {
            phoneMultiplier = .9;
        } else if (currentPhoneQuota < 1.1) {
            phoneMultiplier = 1;
        } else if (currentPhoneQuota < 1.2) {
            phoneMultiplier = 1.1;
        } else if (currentPhoneQuota < 1.3) {
            phoneMultiplier = 1.2;
        } else if (currentPhoneQuota < 1.4) {
            phoneMultiplier = 1.3;
        } else if (currentPhoneQuota < 1.5) {
            phoneMultiplier = 1.4;
        } else if (currentPhoneQuota < 1.6) {
            phoneMultiplier = 1.5;
        } else if (currentPhoneQuota < 1.7) {
            phoneMultiplier = 1.6;
        } else if (currentPhoneQuota < 1.8) {
            phoneMultiplier = 1.7;
        } else if (currentPhoneQuota < 1.9) {
            phoneMultiplier = 1.8;
        } else if (currentPhoneQuota < 2) {
            phoneMultiplier = 1.9;
        } else if (netPhoneQuota == 0) {
            phoneMultiplier = 0;
        } else {
            phoneMultiplier = 2;
        }

        if (salesDollarQuotaGlobal == 0) {
            salesMultiplier = 0;
        } else {
            salesMultiplier = totalSalesDollarsGlobal / salesDollarQuotaGlobal;
        }
        expectedPaycheck = expectedCheckGlobal * phoneMultiplier * (Math.round(salesMultiplier * 100.0) / 100.0);

        //Checks for windfall
        if (expectedPaycheck > (expectedCheckGlobal * 3.0)) {
            double over300Adjustment = (expectedPaycheck - (expectedCheckGlobal * 3.0)) * .50;
            expectedPaycheck = expectedPaycheck - over300Adjustment;
        }
        lblPhoneMultiplier.setText(format.format(phoneMultiplier));
        lblSalesMultiplier.setText(format.format(salesMultiplier));
        lblExpectedPaycheck.setText(formatCurrency(expectedPaycheck));
    }

    private void hidePaycheck() {
        lblTargetPaycheck.setVisibility(View.INVISIBLE);
        lblTargetPaycheckTitle.setVisibility(View.INVISIBLE);
        lblTargetPaySubTitle.setVisibility(View.INVISIBLE);
        lblPhoneMultiplier.setVisibility(View.INVISIBLE);
        lblPhoneMultTitle.setVisibility(View.INVISIBLE);
        lblSalesMultiplier.setVisibility(View.INVISIBLE);
        lblSalesMultTitle.setVisibility(View.INVISIBLE);
        lblExpectedPaycheck.setVisibility(View.INVISIBLE);
        lblExpectedPayTitle.setVisibility(View.INVISIBLE);

        lblShowHide.setVisibility(View.VISIBLE);
    }

    private void showPaycheck() {
        lblTargetPaycheck.setVisibility(View.VISIBLE);
        lblTargetPaycheckTitle.setVisibility(View.VISIBLE);
        lblTargetPaySubTitle.setVisibility(View.VISIBLE);
        lblPhoneMultiplier.setVisibility(View.VISIBLE);
        lblPhoneMultTitle.setVisibility(View.VISIBLE);
        lblSalesMultiplier.setVisibility(View.VISIBLE);
        lblSalesMultTitle.setVisibility(View.VISIBLE);
        lblExpectedPaycheck.setVisibility(View.VISIBLE);
        lblExpectedPayTitle.setVisibility(View.VISIBLE);

        lblShowHide.setVisibility(View.INVISIBLE);

    }

    private void hideMetrics() {
        lblMonthlyTablets.setVisibility(View.INVISIBLE);
        lblMonthlyHum.setVisibility(View.INVISIBLE);
        lblMonthlyTMP.setVisibility(View.INVISIBLE);
        lblMonthlyRev.setVisibility(View.INVISIBLE);
        lblMonthlyCD.setVisibility(View.INVISIBLE);
        lblMonthlyMultiTMP.setVisibility(View.INVISIBLE);

        lblMetricsTablets.setVisibility(View.INVISIBLE);
        lblMetricsHum.setVisibility(View.INVISIBLE);
        lblMetricsNewTMP.setVisibility(View.INVISIBLE);
        lblMetricsRev.setVisibility(View.INVISIBLE);
        lblMetricsCD.setVisibility(View.INVISIBLE);
        lblMetricsMultiTMP.setVisibility(View.INVISIBLE);

        lblShowHide.setVisibility(View.VISIBLE);
    }

    private void showMetrics() {
        lblMonthlyTablets.setVisibility(View.VISIBLE);
        lblMonthlyHum.setVisibility(View.VISIBLE);
        lblMonthlyTMP.setVisibility(View.VISIBLE);
        lblMonthlyRev.setVisibility(View.VISIBLE);
        lblMonthlyCD.setVisibility(View.VISIBLE);
        lblMonthlyMultiTMP.setVisibility(View.VISIBLE);

        lblMetricsTablets.setVisibility(View.VISIBLE);
        lblMetricsHum.setVisibility(View.VISIBLE);
        lblMetricsNewTMP.setVisibility(View.VISIBLE);
        lblMetricsRev.setVisibility(View.VISIBLE);
        lblMetricsCD.setVisibility(View.VISIBLE);
        lblMetricsMultiTMP.setVisibility(View.VISIBLE);

        lblShowHide.setVisibility(View.INVISIBLE);
    }
}
