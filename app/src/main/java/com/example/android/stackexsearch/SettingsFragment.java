package com.example.android.stackexsearch;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends DialogFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        final SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.preference_root_key), Context.MODE_PRIVATE);

        final Spinner sortSpinner = rootView.findViewById(R.id.sort_spinner);
        final Spinner orderSpinner = rootView.findViewById(R.id.order_spinner);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sort_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.order_array, android.R.layout.simple_spinner_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderAdapter);

        sortSpinner.setSelection(preferences.getInt(getString(R.string.sort_preference), 0));
        orderSpinner.setSelection(preferences.getInt(getString(R.string.order_preference), 0));

        final EditText favEditText = rootView.findViewById(R.id.fav_et);
        favEditText.setText(preferences.getString(getString(R.string.category_pref), "Android"));

        Button saveSettingsButton = rootView.findViewById(R.id.save_btn);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(favEditText.getText().toString().trim())) {
                    SharedPreferences.Editor preferenceEditor = preferences.edit()
                            .putInt(getString(R.string.sort_preference), sortSpinner.getSelectedItemPosition())
                            .putInt(getString(R.string.order_preference), orderSpinner.getSelectedItemPosition())
                            .putString(getString(R.string.category_pref), favEditText.getText().toString().trim());
                    preferenceEditor.apply();
                    SettingsFragment.this.dismiss();
                }
            }
        });


        return rootView;
    }

}
