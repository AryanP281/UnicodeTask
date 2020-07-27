package com.example.unicodetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout; //The drawer layout for the navigation drawer
    private Toolbar toolbar; //The app toolbar
    private ActionBarDrawerToggle drawerToggle; //The drawer toggle for managing the navigation drawer
    private NavigationView navView; //The navigation drawer

    static final String USER_INFO_FILE_NAME = "user.txt"; //The name of the txt file in which the user info is stored

    boolean userInfoFound = false; //Tells whether the user info was found and read from the file

    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch(item.getItemId())
            {
                case R.id.nav_userinfo :
                    displayFragment(new UserInfoFragment(), false); //Displaying the fragment
                    toolbar.setTitle(R.string.frag_userinfo_title); //Updating the title
                    break;
                case R.id.nav_contacts :
                    displayFragment(new ContactListFragment(), false); //Displaying the contact list task fragment
                    toolbar.setTitle(R.string.frag_contactlist_title); //Updating the title
                    break;
                case R.id.nav_weather :
                    displayFragment(new WeatherFragment(), false); //Displaying the weather data fragment
                    toolbar.setTitle(R.string.frag_weatherinfo_title); //Updating the title
                    break;
            }

            //Closing the drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        }
    }; //Listens for navigation view item clicks

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Setting the toolbar as the action bar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the drawer toggle
        initializeDrawerToggle(toolbar);

        //Initializing the navigation view
        navView = ((NavigationView)findViewById(R.id.nav_view));
        navView.setNavigationItemSelectedListener(navigationItemSelectedListener); //For responding to clicks in the navigation drawer

        //Reading the user info and setting the user details in the navigation header
        initializeNavHeader();

        //Restoring saved instance state
        if(savedInstanceState != null) getSupportActionBar().setTitle(savedInstanceState.getString("toolbar_title")); //Restoring the toolbar title
        else
        {
            displayFragment(new UserInfoFragment(), false); //Displaying the default fragment
            getSupportActionBar().setTitle(R.string.frag_userinfo_title); //Setting the toolbar title
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Saving the toolbar title
        outState.putString("toolbar_title", toolbar.getTitle().toString());
    }

    void displayFragment(Fragment fragment, boolean addToBackstack)
    {
        /*Displays the given fragment in the content frame*/

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction(); //Starting the fragment transaction
        fragmentTransaction.replace(R.id.content_frame, fragment); //Adding the fragment to the frame
        if(addToBackstack)
            fragmentTransaction.addToBackStack(null); //Adding the transaction to the backstack
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE); //Setting the transition style
        fragmentTransaction.commit(); //Committing the transaction
    }

    private void initializeDrawerToggle(Toolbar toolbar)
    {
        /*Initializes the drawer toggle*/

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer);
        drawerLayout.addDrawerListener(drawerToggle); //Setting the drawer toggle as the drawer listener
        drawerToggle.syncState();
    }

    private void initializeNavHeader()
    {
        /*Sets the user info in the navigation header*/

        try
        {
            //Creating a reader to read the user info file
            BufferedReader reader = new BufferedReader(new FileReader(new File(getFilesDir(), USER_INFO_FILE_NAME)));

            //Reading the data
            String fileLine = reader.readLine();
            String userInfo[] = fileLine.split(","); //Separating the read line into the userinfo as per CSV format

            //Setting the nav header component
            ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_username)).setText(userInfo[0]);
            ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_userphone)).setText(userInfo[1]);
            ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_birthdate)).setText(userInfo[2]);

            //Closing the reader after use
            reader.close();

            //Stating that the user info was read
            userInfoFound = true;
        }
        catch(FileNotFoundException e) {}
        catch(IOException e) {}
    }

    void updateNavHeader(String userInfo[])
    {
        /*Updates the nav header with the given values*/

        //Setting the nav header component
        ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_username)).setText(userInfo[0]);
        ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_userphone)).setText(userInfo[1]);
        ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_birthdate)).setText(userInfo[2]);
    }

    String[] getNavHeaderFieldValues()
    {
        /*Returns the values for the nav header textviews*/

        String values[] = new String[3];
        values[0] = ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_username)).getText().toString();
        values[1] = ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_userphone)).getText().toString();
        values[2] = ((TextView)navView.getHeaderView(0).findViewById(R.id.nav_header_birthdate)).getText().toString();

        return values;
    }

}