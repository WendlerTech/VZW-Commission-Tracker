package tech.wendler.commission_tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Objects;

public class EditTransaction extends Fragment {

    private final static double REVENUE_ASSUMED_VALUE = SalesDollarValues.getRevenueAssumedValue();
    private final static int CONNECTED_ASSUMED_VALUE = SalesDollarValues.getConnectedAssumedValue();
    private final static int SINGLE_TMP_ASSUMED_VALUE = SalesDollarValues.getSingleTmpAssumedValue();
    private final static int HUM_ASSUMED_VALUE = SalesDollarValues.getHumAssumedValue();
    private final static int MULTI_TMP_ASSUMED_VALUE = SalesDollarValues.getMultiTmpAssumedValue();
    private final static int TABLET_ASSUMED_VALUE = SalesDollarValues.getTabletAssumedValue();

    private EditText txtNewPhone, txtUpgPhone, txtTablet, txtConnected, txtTMP, txtRev, txtHum;
    private TextView lblBucketTotal;
    private CheckBox chkMultiTMP;
    private Button btnAddDetails;
    private double totalRev = 0, totalBucketAchieved = 0;
    private double tabletBucketAmt = 0, connectedBucketAmt = 0, humBucketAmt = 0,
            singleTMPBucketAmt = 0, multiTMPBucketAmt = 0, revBucketAmt = 0;
    private int totalTablets = 0, totalConnected = 0,
            totalHum = 0, totalTMP = 0, totalNewPhones = 0, totalUpgPhones = 0;
    private boolean newMultiTMP;

    private Transaction transaction = null;
    private TransactionInfo transactionInfo = null;
    private Calendar selectedDate = Calendar.getInstance();
    private Fragment dailyTotalsFragment = null;

    NumberFormat format = NumberFormat.getCurrencyInstance();
    private DatabaseHelper databaseHelper;
    private FirebaseAnalytics mFirebaseAnalytics;

    public EditTransaction() {

    }

    public static EditTransaction newInstance() {
        return new EditTransaction();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getContext()));
        mFirebaseAnalytics.setCurrentScreen(Objects.requireNonNull(getActivity()), "Edit_Transaction", "EditTransaction");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_transaction, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btnSubmit, btnCancel;

        databaseHelper = new DatabaseHelper(getActivity());
        txtNewPhone = getView().findViewById(R.id.txtEditNewPhones);
        txtUpgPhone = getView().findViewById(R.id.txtEditUpgPhones);
        txtTablet = getView().findViewById(R.id.txtEditTablets);
        txtHum = getView().findViewById(R.id.txtEditHum);
        txtConnected = getView().findViewById(R.id.txtConnectedDev);
        txtTMP = getView().findViewById(R.id.txtEditTMP);
        txtRev = getView().findViewById(R.id.txtRevenue);
        chkMultiTMP = getView().findViewById(R.id.chkMultiTMP);
        lblBucketTotal = getView().findViewById(R.id.lblTotalBucket);
        btnSubmit = getView().findViewById(R.id.btnSubmit);
        btnCancel = getView().findViewById(R.id.btnCancel);
        btnAddDetails = getView().findViewById(R.id.btnEditTransAddDetails);

        //Gets the Transaction object passed by user selecting a transaction in recycler view
        final Bundle selectedTransaction = this.getArguments();
        if (selectedTransaction != null) {
            this.transaction = (Transaction) selectedTransaction
                    .getSerializable("selectedTransaction");
            this.selectedDate.setTimeInMillis(selectedTransaction.getLong("selectedDate"));
            this.transactionInfo = (TransactionInfo) selectedTransaction.getSerializable("inProgressTransactionInfo");

            if (transactionInfo == null) {
                queryTransactionInfo();
            } else {
                //Changes button text to "View Details" if any exist
                btnAddDetails.setText(R.string.view_details);
            }
        }

        populateSelectedTransData();

        //TextWatcher listeners update the bucket total label in real time
        txtNewPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Highlights the edit text box with a pink border upon changes being detected
                txtNewPhone.setBackgroundResource(R.drawable.edittext_modified_border);
                if (txtNewPhone.getText().toString().length() > 0) {
                    if (txtNewPhone.getText().toString().length() < 8) {
                        totalNewPhones = Integer.parseInt(txtNewPhone.getText().toString());
                    } else {
                        txtNewPhone.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Allows user to simply delete entry, rather than entering a 0
                if (txtNewPhone.getText().toString().length() == 0) {
                    totalNewPhones = 0;
                }
            }
        });

        txtUpgPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Highlights the edit text box with a pink border upon changes being detected
                txtUpgPhone.setBackgroundResource(R.drawable.edittext_modified_border);
                if (txtUpgPhone.getText().toString().length() > 0) {
                    if (txtUpgPhone.getText().toString().length() < 8) {
                        totalUpgPhones = Integer.parseInt(txtUpgPhone.getText().toString());
                    } else {
                        txtUpgPhone.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Allows user to simply delete entry, rather than entering a 0
                if (txtUpgPhone.getText().toString().length() == 0) {
                    totalUpgPhones = 0;
                }
            }
        });

        txtTablet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Highlights the edit text box with a pink border upon changes being detected
                txtTablet.setBackgroundResource(R.drawable.edittext_modified_border);
                if (txtTablet.getText().toString().length() > 0) {
                    if (txtTablet.getText().toString().length() < 8) {
                        totalTablets = Integer.parseInt(txtTablet.getText().toString());
                        tabletBucketAmt = totalTablets * TABLET_ASSUMED_VALUE;
                        updateBucketTotalLabel();
                    } else {
                        txtTablet.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtTablet.getText().toString().length() == 0) {
                    tabletBucketAmt = 0;
                    totalTablets = 0;
                    updateBucketTotalLabel();
                }
            }
        });

        txtHum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Highlights the edit text box with a pink border upon changes being detected
                txtHum.setBackgroundResource(R.drawable.edittext_modified_border);
                if (txtHum.getText().toString().length() > 0) {
                    if (txtHum.getText().toString().length() < 8) {
                        totalHum = Integer.parseInt(txtHum.getText().toString());
                        humBucketAmt = (totalHum * HUM_ASSUMED_VALUE);
                        updateBucketTotalLabel();
                    } else {
                        txtHum.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtHum.getText().toString().length() == 0) {
                    humBucketAmt = 0;
                    totalHum = 0;
                    updateBucketTotalLabel();
                }
            }
        });

        txtConnected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Highlights the edit text box with a pink border upon changes being detected
                txtConnected.setBackgroundResource(R.drawable.edittext_modified_border);
                if (txtConnected.getText().toString().length() > 0) {
                    if (txtConnected.getText().toString().length() < 8) {
                        totalConnected = Integer.parseInt(txtConnected.getText().toString());
                        connectedBucketAmt = (totalConnected * CONNECTED_ASSUMED_VALUE);
                        updateBucketTotalLabel();
                    } else {
                        txtConnected.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtConnected.getText().toString().length() == 0) {
                    connectedBucketAmt = 0;
                    totalConnected = 0;
                    updateBucketTotalLabel();
                }
            }
        });

        txtTMP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Highlights the edit text box with a pink border upon changes being detected
                txtTMP.setBackgroundResource(R.drawable.edittext_modified_border);
                if (txtTMP.getText().toString().length() > 0) {
                    if (txtTMP.getText().toString().length() < 8) {
                        totalTMP = Integer.parseInt(txtTMP.getText().toString());
                        singleTMPBucketAmt = (totalTMP * SINGLE_TMP_ASSUMED_VALUE);
                        updateBucketTotalLabel();
                    } else {
                        txtTMP.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtTMP.getText().toString().length() == 0) {
                    singleTMPBucketAmt = 0;
                    totalTMP = 0;
                    updateBucketTotalLabel();
                }
            }
        });

        txtRev.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Highlights the edit text box with a pink border upon changes being detected
                txtRev.setBackgroundResource(R.drawable.edittext_modified_border);
                if (txtRev.getText().toString().length() > 0) {
                    if (txtRev.getText().toString().length() < 8) {
                        if (txtRev.getText().toString().equals(".")) {
                            txtRev.setText("0.");
                            txtRev.setSelection(txtRev.getText().length()); //Moves cursor to end
                        }
                        try {
                            totalRev = Double.parseDouble(txtRev.getText().toString());
                        } catch (NumberFormatException ignored) {

                        }
                        revBucketAmt = (totalRev * REVENUE_ASSUMED_VALUE);
                        updateBucketTotalLabel();
                    } else {
                        txtRev.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtRev.getText().toString().length() == 0) {
                    revBucketAmt = 0;
                    totalRev = 0;
                    updateBucketTotalLabel();
                }
            }
        });

        chkMultiTMP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Highlights the checkbox with a pink border upon changes being detected
                chkMultiTMP.setBackgroundResource(R.drawable.edittext_modified_border);
                if (isChecked) {
                    //Un-highlights the TMP edit text box upon selection of multi TMP
                    txtTMP.setBackgroundResource(0);
                    txtTMP.setText("");
                    txtTMP.setEnabled(false);
                    singleTMPBucketAmt = 0;
                    totalTMP = 0;
                    newMultiTMP = true;
                    multiTMPBucketAmt = MULTI_TMP_ASSUMED_VALUE;
                    updateBucketTotalLabel();
                } else {
                    //Also highlights TMP edit text if multi is de-selected
                    txtTMP.setBackgroundResource(R.drawable.edittext_modified_border);
                    txtTMP.setEnabled(true);
                    multiTMPBucketAmt = 0;
                    newMultiTMP = false;
                    updateBucketTotalLabel();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Edit Transaction");
                dialog.setMessage("Are you sure you want to edit this transaction?" +
                        "\n\nHighlighted boxes reflect changes.");
                dialog.setPositiveButton("Submit Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Confirmation from user
                        newMultiTMP = chkMultiTMP.isChecked();
                        //Creates new Transaction object from newly edited values
                        Transaction editedTransaction = new Transaction(transaction.getTransactionID(),
                                totalNewPhones, totalUpgPhones, totalTablets, totalConnected,
                                totalHum, totalTMP, totalRev, newMultiTMP);
                        editedTransaction.setTotalSalesDollars(editedTransaction.calculateTotalSalesDollars());

                        //Sends new object to be updated in DB
                        databaseHelper.updateTransaction(editedTransaction, transactionInfo);

                        Toast.makeText(getContext(), "Transaction updated", Toast.LENGTH_SHORT).show();

                        mFirebaseAnalytics.logEvent("Transaction_Edited", null);

                        //Opens daily totals fragment to selected date upon successful database edit
                        Bundle bundle = new Bundle();
                        bundle.putLong("selectedDate", selectedDate.getTimeInMillis());
                        dailyTotalsFragment = DailyTotals.newInstance();
                        dailyTotalsFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction;
                        if (getFragmentManager() != null) {
                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, dailyTotalsFragment);
                            fragmentTransaction.commit();
                        }
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //User cancelled edits, nothing happens
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        btnAddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.setTotalNewPhones(totalNewPhones);
                transaction.setTotalUpgPhones(totalUpgPhones);
                transaction.setTotalTablets(totalTablets);
                transaction.setTotalHum(totalHum);
                transaction.setTotalConnected(totalConnected);
                transaction.setTotalTMP(totalTMP);
                transaction.setTotalRev(totalRev);
                transaction.setNewMultiTMP(newMultiTMP);
                transaction.setTotalSalesDollars(totalBucketAchieved);

                Bundle transactionBundle = new Bundle();
                transactionBundle.putSerializable("currentTransaction", transaction);
                transactionBundle.putSerializable("currentTransactionInfo", transactionInfo);
                transactionBundle.putLong("selectedDate", selectedDate.getTimeInMillis());

                Fragment moreDetailsFragment = MoreInfo.newInstance();
                moreDetailsFragment.setArguments(transactionBundle);
                FragmentTransaction fragmentTransaction;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, moreDetailsFragment, "EditTransaction");
                    fragmentTransaction.addToBackStack("EditTransaction");
                    fragmentTransaction.commit();
                }
            }
        });

        //Exits fragment without saving changes; opens daily totals fragment on selected date
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong("selectedDate", selectedDate.getTimeInMillis());
                dailyTotalsFragment = DailyTotals.newInstance();
                dailyTotalsFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, dailyTotalsFragment);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    //Populates fields with current data from selected transaction
    private void populateSelectedTransData() {
        totalNewPhones = transaction.getTotalNewPhones();
        totalUpgPhones = transaction.getTotalUpgPhones();
        totalTablets = transaction.getTotalTablets();
        totalHum = transaction.getTotalHum();
        totalConnected = transaction.getTotalConnected();
        totalTMP = transaction.getTotalTMP();
        newMultiTMP = transaction.isNewMultiTMP();
        totalRev = transaction.getTotalRev();
        totalBucketAchieved = transaction.getTotalSalesDollars();

        tabletBucketAmt = totalTablets * TABLET_ASSUMED_VALUE;
        connectedBucketAmt = totalConnected * CONNECTED_ASSUMED_VALUE;
        humBucketAmt = totalHum * HUM_ASSUMED_VALUE;
        singleTMPBucketAmt = totalTMP * SINGLE_TMP_ASSUMED_VALUE;
        revBucketAmt = totalRev * REVENUE_ASSUMED_VALUE;

        if (totalNewPhones > 0) {
            txtNewPhone.setText(String.valueOf(totalNewPhones));
        }
        if (totalUpgPhones > 0) {
            txtUpgPhone.setText(String.valueOf(totalUpgPhones));
        }
        if (totalConnected > 0) {
            txtConnected.setText(String.valueOf(totalConnected));
        }
        if (totalHum > 0) {
            txtHum.setText(String.valueOf(totalHum));
        }
        if (totalRev > 0) {
            txtRev.setText(String.valueOf(totalRev));
        }
        if (totalTablets > 0) {
            txtTablet.setText(String.valueOf(totalTablets));
        }
        if (totalTMP > 0) {
            txtTMP.setText(String.valueOf(totalTMP));
        }

        if (newMultiTMP) {
            chkMultiTMP.setChecked(true);
            multiTMPBucketAmt = MULTI_TMP_ASSUMED_VALUE;
            txtTMP.setEnabled(false);
        } else {
            chkMultiTMP.setChecked(false);
            multiTMPBucketAmt = 0;
            txtTMP.setEnabled(true);
        }

        lblBucketTotal.setText(format.format(totalBucketAchieved));
    }

    private void queryTransactionInfo() {
        String name, phoneNum, orderNum;
        int salesForceLeads;
        double extraSalesDollars;
        boolean repAssisted, dFill, ispu, preOrder;

        String queryString = "SELECT customer_name, phone_number, order_number, sales_force_leads, " +
                "rep_assisted_order, direct_fulfillment_order, in_store_pickup_order, pre_order, " +
                "extra_sales_dollars FROM Transactions WHERE transID LIKE '" +
                transaction.getTransactionID() + "';";

        try (Cursor cursor = databaseHelper.getData(queryString)) {
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("customer_name"));
                phoneNum = cursor.getString(cursor.getColumnIndex("phone_number"));
                orderNum = cursor.getString(cursor.getColumnIndex("order_number"));
                salesForceLeads = cursor.getInt(cursor.getColumnIndex("sales_force_leads"));
                extraSalesDollars = cursor.getFloat(cursor.getColumnIndex("extra_sales_dollars"));
                repAssisted = cursor.getInt(cursor.getColumnIndex("rep_assisted_order")) == 1;
                dFill = cursor.getInt(cursor.getColumnIndex("direct_fulfillment_order")) == 1;
                ispu = cursor.getInt(cursor.getColumnIndex("in_store_pickup_order")) == 1;
                preOrder = cursor.getInt(cursor.getColumnIndex("pre_order")) == 1;

                transactionInfo = new TransactionInfo(name, phoneNum, orderNum, salesForceLeads,
                        extraSalesDollars, repAssisted, dFill, ispu, preOrder);

                //Changes button text to "View Details" if any exist
                if (name != null || phoneNum != null || orderNum != null || salesForceLeads > 0
                        || extraSalesDollars > 0 || repAssisted || dFill || ispu || preOrder) {
                    btnAddDetails.setText(R.string.view_details);
                }
            }
        }
    }

    //Changes label to reflect updated bucket dollars earned
    private void updateBucketTotalLabel() {
        totalBucketAchieved = tabletBucketAmt + connectedBucketAmt + humBucketAmt
                + singleTMPBucketAmt + multiTMPBucketAmt + revBucketAmt;

        lblBucketTotal.setText(format.format(totalBucketAchieved));
    }
}
