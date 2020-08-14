package com.example.unicodetask;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        //Restarting the activity to apply the theme changes
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }
}