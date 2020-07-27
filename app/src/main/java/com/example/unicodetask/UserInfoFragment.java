package com.example.unicodetask;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class UserInfoFragment extends Fragment
{

    private Context fragmentActivity; //The activity with which the fragment is associated
    private View fragmentView; //The View object associate with the fragment layout

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Getting the fragment activity
        fragmentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_user_info, container, false);

        //Toolbar setting
        setHasOptionsMenu(true);

        return fragmentView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //Restoring entered user info
        restoreUserInfo();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        /*Inflates the toolbar menu*/

        inflater.inflate(R.menu.user_info_menu, menu); //Inflating the fragment menu
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*Handles menu item clicks*/

        //Determining which item has been clicked
        switch(item.getItemId())
        {
            case R.id.menuitem_save :
                saveUserInfo(); break;
            case R.id.menuitem_clear : clearUserInfo() ; //Clearing the info fields
                 break;
        }

        return true;
    }

    private void restoreUserInfo()
    {
        /*Restores the user info from the nav header*/

        if(((HomeActivity)fragmentActivity).userInfoFound)
        {
            //Getting the values stored in the nav header
            String userInfo[] = ((HomeActivity)fragmentActivity).getNavHeaderFieldValues();

            ((EditText)fragmentView.findViewById(R.id.user_name)).setText(userInfo[0]); //Setting the user name field
            ((EditText)fragmentView.findViewById(R.id.phonenum)).setText(userInfo[1]); //Setting the user phone field

            //Getting the date components
            String date[] = userInfo[2].split("-");
            ((DatePicker)fragmentView.findViewById(R.id.birth_date)).updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
        }
    }

    private void clearUserInfo()
    {
        /*Clears the info entered by the user*/

        ((EditText)fragmentView.findViewById(R.id.user_name)).setText(null); //Clearing the user name field
        ((EditText)fragmentView.findViewById(R.id.phonenum)).setText(null); //Clearing the phone number field

        //Updating the birth date picker
        Calendar calendar = Calendar.getInstance();
        ((DatePicker)fragmentView.findViewById(R.id.birth_date)).updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK)); //Resetting to current date

        //Deleting the user info file
        File userInfoFile = new File(fragmentActivity.getFilesDir(), HomeActivity.USER_INFO_FILE_NAME);
        try
        {
            userInfoFile.delete();
        }
        catch(SecurityException e)
        {
            Toast.makeText(fragmentActivity, "Unable to clear saved userinfo from memory", Toast.LENGTH_SHORT).show();
        }

        //Updating the nav header
        ((HomeActivity)fragmentActivity).updateNavHeader(new String[]{getResources().getString(R.string.nav_header_username_label),
                getResources().getString(R.string.nav_header_userphone_label), getResources().getString(R.string.nav_header_userbirth_label)});

    }

    private void saveUserInfo()
    {
        /*Saves the entered user info to the file*/

        //Reading the user info
        StringBuilder userInfo = new StringBuilder();
        userInfo.append(((EditText)fragmentView.findViewById(R.id.user_name)).getText().toString() + ",");
        userInfo.append(((EditText)fragmentView.findViewById(R.id.phonenum)).getText().toString() + ",");
        //Getting the entered birthdate
        DatePicker birthdatePicker = ((DatePicker)fragmentView.findViewById(R.id.birth_date));
        int birthday = birthdatePicker.getDayOfMonth();
        int birthmonth = birthdatePicker.getMonth() + 1;
        int birthYear = birthdatePicker.getYear();
        userInfo.append(String.format("%d-%d-%d", birthday, birthmonth, birthYear));

        try
        {
            FileOutputStream writer = new FileOutputStream(new File(fragmentActivity.getFilesDir(), HomeActivity.USER_INFO_FILE_NAME)); //Opening a writer to write the user info

            //Writing the user info to file
            writer.write(userInfo.toString().getBytes());

            //Closing the writer after use
            writer.close();
        }
        catch(FileNotFoundException e)
        {
            Toast.makeText(fragmentActivity, "Unable to save user info", Toast.LENGTH_SHORT).show();
        }
        catch(SecurityException e)
        {
            Toast.makeText(fragmentActivity, "Unable to access file", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
        }

        //Updating the navigation drawer header
        ((HomeActivity)fragmentActivity).updateNavHeader(userInfo.toString().split(","));
    }

}