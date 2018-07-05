package tech.wendler.commission_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewTransaction extends Fragment {

    final static double REVENUE_ASSUMED_VALUE = .35;
    final static int CONNECTED_ASSUMED_VALUE = 50;
    final static int SINGLE_TMP_ASSUMED_VALUE = 70;
    final static int MULTI_TMP_ASSUMED_VALUE = 200;
    final static int TABLET_ASSUMED_VALUE = 200;

    private EditText txtNewPhone, txtUpgPhone, txtTablet, txtConnected, txtTMP, txtRev;
    private TextView lblBucketTotal;
    private CheckBox chkMultiTMP;
    private Button btnSubmit;
    private double totalBucketAchieved = 0;
    private double tabletBucketAmt = 0, connectedBucketAmt = 0,
            singleTMPBucketAmt = 0, multiTMPBucketAmt = 0, revBucketAmt = 0;
    private int totalNewPhones = 0, totalUpgPhones = 0, totalTablets = 0, totalConnected = 0,
            totalTMP = 0;
    private boolean newMultiTMP = false;
    private double totalRev = 0;

    NumberFormat format = NumberFormat.getCurrencyInstance();
    private DatabaseHelper databaseHelper;

    public NewTransaction() {
        // Required empty public constructor
    }

    public static NewTransaction newInstance() {
        NewTransaction fragment = new NewTransaction();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_transaction, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
        txtNewPhone =  getView().findViewById(R.id.txtNewPhones);
        txtUpgPhone =  getView().findViewById(R.id.txtUpgPhones);
        txtTablet =  getView().findViewById(R.id.txtTablets);
        txtConnected =  getView().findViewById(R.id.txtConnectedDev);
        txtTMP =  getView().findViewById(R.id.txtTMP);
        txtRev = getView().findViewById(R.id.txtRevenue);
        chkMultiTMP =  getView().findViewById(R.id.chkMultiTMP);
        lblBucketTotal =  getView().findViewById(R.id.lblTotalBucket);
        btnSubmit =  getView().findViewById(R.id.btnSubmit);

    //TextWatcher listeners update the bucket total label in real time
        txtTablet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtTablet.getText().toString().length() > 0) {
                    totalTablets = Integer.parseInt(txtTablet.getText().toString());
                    tabletBucketAmt = totalTablets * TABLET_ASSUMED_VALUE;
                    updateBucketTotalLabel();
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

        txtConnected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtConnected.getText().toString().length() > 0) {
                    totalConnected = Integer.parseInt(txtConnected.getText().toString());
                    connectedBucketAmt = (totalConnected * CONNECTED_ASSUMED_VALUE);
                    updateBucketTotalLabel();
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
                    totalTMP = Integer.parseInt(txtTMP.getText().toString());
                    singleTMPBucketAmt = (totalTMP * SINGLE_TMP_ASSUMED_VALUE);
                    updateBucketTotalLabel();
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
                    totalRev = Double.parseDouble(txtRev.getText().toString());
                    revBucketAmt = (totalRev * REVENUE_ASSUMED_VALUE);
                    updateBucketTotalLabel();
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
                totalNewPhones = Integer.parseInt(txtNewPhone.getText().toString());
                totalUpgPhones = Integer.parseInt(txtUpgPhone.getText().toString());
                databaseHelper.addData(currentTime(), totalNewPhones, totalUpgPhones, totalTablets,
                        totalConnected, totalTMP, newMultiTMP, totalRev, totalBucketAchieved);
            }
        });
    }
    //Changes label to reflect updated bucket dollars earned
    public void updateBucketTotalLabel() {
        totalBucketAchieved = tabletBucketAmt + connectedBucketAmt
                + singleTMPBucketAmt + multiTMPBucketAmt + revBucketAmt;

        lblBucketTotal.setText(format.format(totalBucketAchieved));
    }

    public String currentTime() {
        //Gets current date & formats it as such: Nov 23, 2015, 3:55 PM
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a");
        String date = simpleDateFormat.format(calendar.getTime());
        return date;
    }
}
