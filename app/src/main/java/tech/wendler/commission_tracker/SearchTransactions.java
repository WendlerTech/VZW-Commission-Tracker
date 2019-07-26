package tech.wendler.commission_tracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SearchTransactions extends Fragment {

    private EditText txtCustName, txtPhoneNum, txtOrderNum;
    private CheckBox chkRepAssisted, chkDfill, chkIspu, chkPreOrder;
    private TextView lblStartDate, lblEndDate;
    private Button btnStartDate, btnEndDate;
    private RadioGroup radioGroup;

    private Date startDate, endDate;
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener startDateSetListener, endDateSetListener;

    private String custName, phoneNum, orderNum;
    private boolean repAssisted, dFill, ispu, preOrder, searchByDate;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static SearchTransactions newInstance() {
        return new SearchTransactions();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getContext()));
        mFirebaseAnalytics.setCurrentScreen(Objects.requireNonNull(getActivity()), "Search_Transactions", "SearchTransactions");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btnSearch, btnClear;

        txtCustName = getView().findViewById(R.id.txtSearchCustName);
        txtPhoneNum = getView().findViewById(R.id.txtSearchPhoneNum);
        txtOrderNum = getView().findViewById(R.id.txtSearchOrderNum);
        chkRepAssisted = getView().findViewById(R.id.chkSearchRepAssisted);
        chkDfill = getView().findViewById(R.id.chkSearchDfill);
        chkIspu = getView().findViewById(R.id.chkSearchIspu);
        chkPreOrder = getView().findViewById(R.id.chkSearchPreOrder);
        lblStartDate = getView().findViewById(R.id.lblSearchStartDate);
        lblEndDate = getView().findViewById(R.id.lblSearchEndDate);
        btnStartDate = getView().findViewById(R.id.btnSearchStartDate);
        btnEndDate = getView().findViewById(R.id.btnSearchEndDate);
        btnSearch = getView().findViewById(R.id.btnSearchTransactions);
        btnClear = getView().findViewById(R.id.btnSearchClear);
        radioGroup = getView().findViewById(R.id.radGrpSearch);

        txtPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radSearchLifetime) {
                    lblStartDate.setVisibility(View.INVISIBLE);
                    lblEndDate.setVisibility(View.INVISIBLE);
                    btnStartDate.setEnabled(false);
                    btnEndDate.setEnabled(false);
                    searchByDate = false;
                } else {
                    lblStartDate.setVisibility(View.VISIBLE);
                    lblEndDate.setVisibility(View.VISIBLE);
                    btnStartDate.setEnabled(true);
                    btnEndDate.setEnabled(true);
                    searchByDate = true;
                }
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);

                int year = startCal.get(Calendar.YEAR);
                int month = startCal.get(Calendar.MONTH);
                int day = startCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog startDialog = new DatePickerDialog(getActivity(),
                        R.style.DialogTheme, startDateSetListener, year, month, day);

                startDialog.show();
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);

                int year = endCal.get(Calendar.YEAR);
                int month = endCal.get(Calendar.MONTH);
                int day = endCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog endDialog = new DatePickerDialog(getActivity(),
                        R.style.DialogTheme, endDateSetListener, year, month, day);

                endDialog.show();
            }
        });

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar selectedStartDate = Calendar.getInstance();
                selectedStartDate.set(year, month, dayOfMonth);

                lblStartDate.setText(formatDateForLabel(selectedStartDate));
                startDate = selectedStartDate.getTime();
            }
        };

        endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar selectedEndDate = Calendar.getInstance();
                selectedEndDate.set(year, month, dayOfMonth);

                lblEndDate.setText(formatDateForLabel(selectedEndDate));
                endDate = selectedEndDate.getTime();
            }
        };

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDateValidity(startDate, endDate)) {
                    Bundle queryStringBundle = new Bundle();

                    boolean searchByCustName = false;
                    boolean searchByPhoneNum = false;
                    boolean searchByOrderNum = false;

                    if (txtCustName.getText().length() > 0) {
                        custName = txtCustName.getText().toString();
                        queryStringBundle.putString("searchTerm", custName);
                        searchByCustName = true;
                        searchByPhoneNum = false;
                        searchByOrderNum = false;
                    }
                    if (txtPhoneNum.getText().length() > 0) {
                        phoneNum = txtPhoneNum.getText().toString();
                        queryStringBundle.putString("searchTerm", phoneNum);
                        searchByPhoneNum = true;
                        searchByCustName = false;
                        searchByOrderNum = false;
                    }
                    if (txtOrderNum.getText().length() > 0) {
                        orderNum = txtOrderNum.getText().toString();
                        queryStringBundle.putString("searchTerm", orderNum);
                        searchByOrderNum = true;
                        searchByCustName = false;
                        searchByPhoneNum = false;
                    }
                    repAssisted = chkRepAssisted.isChecked();
                    dFill = chkDfill.isChecked();
                    ispu = chkIspu.isChecked();
                    preOrder = chkPreOrder.isChecked();

                    if (searchByCustName || searchByPhoneNum || searchByOrderNum || repAssisted ||
                            dFill || ispu || preOrder || searchByDate) {
                        //Something was entered by user
                        String queryString = "SELECT * FROM Transactions WHERE ";
                        boolean searchByTwoCriteria;

                        if (repAssisted || dFill || ispu || preOrder) {
                            //A checkbox was selected
                            if (searchByCustName) {
                                queryString += "customer_name LIKE ?";
                                searchByTwoCriteria = true;
                            } else if (searchByPhoneNum) {
                                queryString += "phone_number LIKE ?";
                                searchByTwoCriteria = true;
                            } else if (searchByOrderNum) {
                                queryString += "order_number LIKE ?";
                                searchByTwoCriteria = true;
                            } else {
                                //Only a checkbox was selected
                                searchByTwoCriteria = false;
                                if (repAssisted) {
                                    queryString += "rep_assisted_order = 1";
                                } else if (dFill) {
                                    queryString += "direct_fulfillment_order = 1";
                                } else if (ispu) {
                                    queryString += "in_store_pickup_order = 1";
                                } else {
                                    queryString += "pre_order = 1";
                                }
                            }
                            //Both a checkbox & a string were added by user
                            if (searchByTwoCriteria) {
                                if (repAssisted) {
                                    queryString += " AND rep_assisted_order = 1";
                                } else if (dFill) {
                                    queryString += " AND direct_fulfillment_order = 1";
                                } else if (ispu) {
                                    queryString += " AND in_store_pickup_order = 1";
                                } else {
                                    queryString += " AND pre_order = 1";
                                }
                            }
                        } else if (searchByCustName) { //No checkboxes were selected
                            queryString += "customer_name LIKE ?";
                        } else if (searchByPhoneNum) {
                            queryString += "phone_number LIKE ?";
                        } else if (searchByOrderNum){
                            queryString += "order_number LIKE ?";
                        } else {
                            //Allows user to search by date range alone
                            queryString = "SELECT * FROM Transactions WHERE 1 = 1";
                        }

                        //Adds date range
                        if (searchByDate) {
                            queryString += " AND date >= '" + formatDateForDatabase(startDate) +
                                    "' AND date < '" + formatDateForDatabase(endDate) + "23:59'";
                        }

                        queryString += " ORDER BY date ASC;";

                        Fragment searchedFragment = SearchedTransactions.newInstance();
                        queryStringBundle.putString("queryStringToSearch", queryString);
                        searchedFragment.setArguments(queryStringBundle);
                        mFirebaseAnalytics.logEvent("Searched_For_Transaction", null);
                        if (getFragmentManager() != null) {
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, searchedFragment, "SearchTransactions");
                            fragmentTransaction.addToBackStack("SearchTransactions");
                            fragmentTransaction.commit();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please enter at least one search criteria.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Start date must be before end date.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        initialLoad();

        txtCustName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txtCustName.getText().length() > 0) {
                    txtPhoneNum.setEnabled(false);
                    txtOrderNum.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txtCustName.getText().length() == 0) {
                    txtPhoneNum.setEnabled(true);
                    txtOrderNum.setEnabled(true);
                }
            }
        });

        txtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txtPhoneNum.getText().length() > 0) {
                    txtCustName.setEnabled(false);
                    txtOrderNum.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txtPhoneNum.getText().length() == 0) {
                    txtCustName.setEnabled(true);
                    txtOrderNum.setEnabled(true);
                }
            }
        });

        txtOrderNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txtOrderNum.getText().length() > 0) {
                    txtCustName.setEnabled(false);
                    txtPhoneNum.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txtOrderNum.getText().length() == 0) {
                    txtPhoneNum.setEnabled(true);
                    txtCustName.setEnabled(true);
                }
            }
        });

        chkRepAssisted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkDfill.setChecked(false);
                    chkIspu.setChecked(false);
                    chkPreOrder.setChecked(false);
                }
            }
        });

        chkDfill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkRepAssisted.setChecked(false);
                    chkIspu.setChecked(false);
                    chkPreOrder.setChecked(false);
                }
            }
        });

        chkIspu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkRepAssisted.setChecked(false);
                    chkDfill.setChecked(false);
                    chkPreOrder.setChecked(false);
                }
            }
        });

        chkPreOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkRepAssisted.setChecked(false);
                    chkDfill.setChecked(false);
                    chkIspu.setChecked(false);
                }
            }
        });
    }

    private void clearFields() {
        txtCustName.setText("");
        txtPhoneNum.setText("");
        txtOrderNum.setText("");
        chkRepAssisted.setChecked(false);
        chkDfill.setChecked(false);
        chkIspu.setChecked(false);
        chkPreOrder.setChecked(false);
        radioGroup.check(R.id.radSearchLifetime);
        initialLoad();
    }

    private void initialLoad() {
        //Hides date range labels
        lblStartDate.setVisibility(View.INVISIBLE);
        lblEndDate.setVisibility(View.INVISIBLE);
        btnStartDate.setEnabled(false);
        btnEndDate.setEnabled(false);

        Calendar startCal = Calendar.getInstance();

        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        //Sets start date to first day of current month
        startCal.set(Calendar.MONTH, currentMonth);
        startCal.set(Calendar.YEAR, currentYear);
        startCal.set(Calendar.DAY_OF_MONTH, 1);

        //Sets end date to today
        lblStartDate.setText(formatDateForLabel(startCal));
        lblEndDate.setText(formatDateForLabel(calendar));

        //Assigns default date values
        startDate = startCal.getTime();
        endDate = calendar.getTime();

    }

    private String formatDateForLabel(Calendar dateToFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        return dateFormat.format(dateToFormat.getTime());
    }

    private String formatDateForDatabase(Date dateToFormat) {
        //Gets current date & returns it as a string to be inserted into the database
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToFormat);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }

    //Returns true if end date is greater than start date or if same day is selected
    private boolean checkDateValidity(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(startDate);
        endCal.setTime(endDate);

        if (startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR) &&
                startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)) {
            return true;
        } else {
            return startDate.compareTo(endDate) < 0;
        }
    }
}
