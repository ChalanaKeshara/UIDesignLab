package com.example.UIDesignLab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {

    private TextView txtDetail;

    public DetailFragment() {
        // A Fragment needs an empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        txtDetail = view.findViewById(R.id.txtDetail);
        return view;
    }

    public void updateTextView(String text) {
        if (txtDetail != null) {
            txtDetail.setText(text);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        txtDetail = null;
    }
}