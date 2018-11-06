package tech.wendler.commission_tracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPriorTransaction extends Fragment {

    final static double REVENUE_ASSUMED_VALUE = .35;
    final static int CONNECTED_ASSUMED_VALUE = 50;
    final static int SINGLE_TMP_ASSUMED_VALUE = 70;
    final static int HUM_ASSUMED_VALUE = 100;
    final static int MULTI_TMP_ASSUMED_VALUE = 200;
    final static int TABLET_ASSUMED_VALUE = 200;

    private EditText txtNewPhone, txtUpgPhone, txtTablet, txtConnected, txtTMP, txtRev, txtHum;
    private TextView lblBucketTotal, lblHeaderTitle;
    private CheckBox chkMultiTMP;
    private double totalBucketAchieved = 0;
    private double tabletBucketAmt = 0, connectedBucketAmt = 0, humBucketAmt = 0,
            singleTMPBucketAmt = 0, multiTMPBucketAmt = 0, revBucketAmt = 0;
    private int totalNewPhones = 0, totalUpgPhones = 0, totalTablets = 0, totalConnected = 0,
            totalHum = 0, totalTMP = 0;
    private boolean newMultiTMP = false;
    private double totalRev = 0;

    private Calendar selectedDate = Calendar.getInstance();
    NumberFormat format = NumberFormat.getCurrencyInstance();
    private DatabaseHelper databaseHelper;
    private Fragment dailyTotalsFragment = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    public AddPriorTransaction() {
        // Required empty public constructor
    }

    public static AddPriorTransaction newInstance() {
        return new AddPriorTransaction();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_prior_transaction, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btnSubmit, btnCancel;

        final Bundle selectedDateBundle = this.getArguments();
        if (selectedDateBundle != null) {
            selectedDate.setTimeInMillis(selectedDateBundle.getLong("selectedDate"));
        }

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
        lblHeaderTitle = getView().findViewById(R.id.lblTitle);
        btnSubmit = getView().findViewById(R.id.btnSubmit);
        btnCancel = getView().findViewById(R.id.btnCancelAddPrior);

        updateTitleLabel(selectedDate);

        //TextWatcher listeners update the bucket total label in real time
        txtTablet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                if (txtHum.getText().toString().length() > 0) {
                    if (txtHum.getText().toString().length() < 9) {
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
                if (txtConnected.getText().toString().length() > 0) {
                    if (txtConnected.getText().toString().length() < 9) {
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
                if (txtRev.getText().toString().length() > 0) {
                    if (txtRev.getText().toString().length() < 9) {
                        totalRev = Double.parseDouble(txtRev.getText().toString());
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

        txtNewPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtNewPhone.getText().toString().length() > 0) {
                    if (txtNewPhone.getText().toString().length() < 9) {
                        totalNewPhones = Integer.parseInt(txtNewPhone.getText().toString());
                    } else {
                        txtNewPhone.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtUpgPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtUpgPhone.getText().toString().length() > 0) {
                    if (txtUpgPhone.getText().toString().length() < 9) {
                        totalUpgPhones = Integer.parseInt(txtUpgPhone.getText().toString());
                    } else {
                        txtUpgPhone.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chkMultiTMP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtTMP.setText("");
                    txtTMP.setEnabled(false);
                    singleTMPBucketAmt = 0;
                    totalTMP = 0;
                    multiTMPBucketAmt = MULTI_TMP_ASSUMED_VALUE;
                    newMultiTMP = true;
                    updateBucketTotalLabel();
                } else {
                    txtTMP.setEnabled(true);
                    multiTMPBucketAmt = 0;
                    newMultiTMP = false;
                    updateBucketTotalLabel();
                }
            }
        });

        //Submit button handles database transaction
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.addTransactionData(formatDateForQueryString(selectedDate), totalNewPhones, totalUpgPhones,
                        totalTablets, totalHum, totalConnected, totalTMP, newMultiTMP,
                        totalRev, totalBucketAchieved)) {
                    Toast.makeText(getContext(), "Transaction successfully added.",
                            Toast.LENGTH_SHORT).show();
                    //Opens daily totals fragment to selected date upon successful database save
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
                } else {
                    Toast.makeText(getContext(), "Error while writing to the database.",
                            Toast.LENGTH_SHORT).show();
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

    //Changes label to reflect updated bucket dollars earned
    public void updateBucketTotalLabel() {
        totalBucketAchieved = tabletBucketAmt + connectedBucketAmt + humBucketAmt
                + singleTMPBucketAmt + multiTMPBucketAmt + revBucketAmt;

        lblBucketTotal.setText(format.format(totalBucketAchieved));
    }

    public void updateTitleLabel(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy");
        lblHeaderTitle.setText("New Transaction For " + format.format(calendar.getTime()));
    }

    private String formatDateForQueryString(Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date.getTime());
    }
}