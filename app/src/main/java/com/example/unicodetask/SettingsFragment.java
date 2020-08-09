package com.example.unicodetask;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);

        //Populating the themes spinner
        Spinner spinner = (Spinner)fragmentView.findViewById(R.id.themes_spinner); //The themes spinner
        ArrayAdapter<CharSequence> themesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.themes_list, R.layout.theme_spinner); //Adapter containing the themes list
        themesAdapter.setDropDownViewResource(R.layout.theme_spinner);
        spinner.setAdapter(themesAdapter); //Setting the spinner adapter

        //Enabling toolbar options menu
        setHasOptionsMenu(true);

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        /*Inflates the toolbar menu*/

        inflater.inflate(R.menu.settings_menu, menu); //Inflating the fragment menu
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*Handles menu item clicks*/

        //Determining which item has been clicked
        switch(item.getItemId())
        {
            case R.id.menuitem_apply_settings : applySettings(); break;
        }

        return true;
    }

    private void applySettings()
    {
        //Getting the theme to be applied

        //Applying the theme
        int themesList[] = {R.style.AppThemeDark, R.style.AppThemeLight}; //The list of themes
        Spinner themesSpinner = (Spinner)getView().findViewById(R.id.themes_spinner);
        getActivity().setTheme(themesList[themesSpinner.getSelectedItemPosition()]);
        //Reloading the activity in order to apply the theme changes
        getActivity().fileList();
        startActivity(getActivity().getIntent());

    }

}