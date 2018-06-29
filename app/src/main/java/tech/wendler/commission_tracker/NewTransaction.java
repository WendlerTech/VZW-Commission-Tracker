package tech.wendler.commission_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

public class NewTransaction extends Fragment {

    final static double REVENUE_ASSUMED_VALUE = .35;
    final static int CONNECTED_ASSUMED_VALUE = 50;
    final static int SINGLE_TMP_ASSUMED_VALUE = 70;
    final static int MULTI_TMP_ASSUMED_VALUE = 200;
    final static int TABLET_ASSUMED_VALUE = 200;

    private EditText txtNewPhone, txtUpgPhone, txtTablet, txtConnected, txtTMP, txtRev;
    private TextView lblBucketTotal;
    private CheckBox chkMultiTMP;
    private double totalBucketAchieved = 0;
    private double tabletBucketAmt = 0, connectedBucketAmt = 0,
            singleTMPBucketAmt = 0, multiTMPBucketAmt = 0, revBucketAmt = 0;

    NumberFormat format = NumberFormat.getCurrencyInstance();

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
        txtNewPhone = (EditText) getView().findViewById(R.id.txtNewPhones);
        txtUpgPhone = (EditText) getView().findViewById(R.id.txtUpgPhones);
        txtTablet = (EditText) getView().findViewById(R.id.txtTablets);
        txtConnected = (EditText) getView().findViewById(R.id.txtConnectedDev);
        txtTMP = (EditText) getView().findViewById(R.id.txtTMP);
        txtRev = (EditText) getView().findViewById(R.id.txtRevenue);
        chkMultiTMP = (CheckBox) getView().findViewById(R.id.chkMultiTMP);
        lblBucketTotal = (TextView) getView().findViewById(R.id.lblTotalBucket);

    //TextWatcher listeners update the bucket total label in real time
        txtTablet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtTablet.getText().toString().length() > 0) {
                    tabletBucketAmt = (Integer.parseInt(txtTablet.getText().toString()) * TABLET_ASSUMED_VALUE);
                    updateBucketTotalLabel();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtTablet.getText().toString().length() == 0) {
                    tabletBucketAmt = 0;
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
                    connectedBucketAmt = (Integer.parseInt(txtConnected.getText().toString()) * CONNECTED_ASSUMED_VALUE);
                    updateBucketTotalLabel();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtConnected.getText().toString().length() == 0) {
                    connectedBucketAmt = 0;
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
                    singleTMPBucketAmt = (Integer.parseInt(txtTMP.getText().toString()) * SINGLE_TMP_ASSUMED_VALUE);
                    updateBucketTotalLabel();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtTMP.getText().toString().length() == 0) {
                    singleTMPBucketAmt = 0;
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
                    revBucketAmt = (Integer.parseInt(txtRev.getText().toString()) * REVENUE_ASSUMED_VALUE);
                    updateBucketTotalLabel();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Updates label if user deletes their entry
                if (txtRev.getText().toString().length() == 0) {
                    revBucketAmt = 0;
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
                    multiTMPBucketAmt = MULTI_TMP_ASSUMED_VALUE;
                    updateBucketTotalLabel();
                } else {
                    txtTMP.setEnabled(true);
                    multiTMPBucketAmt = 0;
                    updateBucketTotalLabel();
                }

            }
        });
    }
    //Changes label to reflect updated bucket dollars earned
    public void updateBucketTotalLabel() {
        totalBucketAchieved = tabletBucketAmt + connectedBucketAmt
                + singleTMPBucketAmt + multiTMPBucketAmt + revBucketAmt;

        lblBucketTotal.setText(format.format(totalBucketAchieved));
    }
}
