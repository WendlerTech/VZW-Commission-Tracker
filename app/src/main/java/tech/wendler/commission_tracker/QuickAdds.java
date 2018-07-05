package tech.wendler.commission_tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class QuickAdds extends Fragment {

    private Button btnPlusNewPhone, btnMinusNewPhone, btnMinusUpg, btnPlusUpg, btnMinusBundle,
            btnPlusBundle, btnMinusCD, btnPlusCD, btnMinusTablet, btnPlusTablet, btnSubmit;

    public QuickAdds() {
        // Required empty public constructor
    }

    public static QuickAdds newInstance() {
        QuickAdds fragment = new QuickAdds();
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
        return inflater.inflate(R.layout.fragment_quick_adds, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
