package com.example.unicodetask;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;


public class UserInfoFragment extends Fragment
{
    private final int CAMERA_PERMISSION_CODE = 201;
    private final int USE_GALLERY_REQUEST_CODE = 1;
    private final int USE_CAMERA_REQUEST_CODE = 11;

    private Context fragmentActivity; //The activity with which the fragment is associated
    private View fragmentView; //The View object associate with the fragment layout
    private Uri profilePicUri = null; //The content uri of the users saved profile pic

    private View.OnClickListener setProfilePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            final CharSequence sources[] = {"Camera", "Gallery"}; //The picture sources

            //Displaying a dialog box for user choice
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.profilepic_source_dialog_msg)
                    .setItems(sources, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(which == 1)
                                useGallery();
                            else useCamera();
                        }
                    });
            dialog.show();
        }
    }; //The set the user's profile pic

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

        //Setting the set profile pic button listener
        ((Button)fragmentView.findViewById(R.id.user_pic)).setOnClickListener(setProfilePicListener);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[])
    {
        if(requestCode == HomeActivity.EXTERNAL_STORAGE_PERMISSION_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                useGallery();
        }
        else
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                useCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Checking if the activity was successful
        if(resultCode != AppCompatActivity.RESULT_CANCELED)
        {
            if(requestCode == USE_GALLERY_REQUEST_CODE)
            {
                //Checking if the URI was received
                if(resultCode == AppCompatActivity.RESULT_OK && data != null)
                {
                    profilePicUri = data.getData(); //Getting the uri of the selected image

                    ((HomeActivity)getActivity()).displayUserProfilePic(profilePicUri);
                }
            }
            else
            {
                //Checking if the image was received
                if(resultCode == AppCompatActivity.RESULT_OK)
                    ((HomeActivity)getActivity()).displayUserProfilePic(profilePicUri);
            }
        }
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
        userInfo.append(String.format("%d-%d-%d,", birthday, birthmonth, birthYear));

        //Saving the profile pic uri
        userInfo.append(profilePicUri != null ? profilePicUri.toString() : "");

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

    private void useGallery()
    {
        /*Gets the user profile pic from the gallery*/

        //Checking if app has permission to access gallery
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            getExternalStoragePermission(); //Getting permission to access the gallery
        else {
            //Creating the intent to use gallery
            Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            galleryIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Profile Picture"), USE_GALLERY_REQUEST_CODE);
        }
    }

    private void getExternalStoragePermission()
    {
        /*Gets permission to access external storage*/

        //Checking if the user should be shown permission rationale explaining the need for internet access
        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.permission_rationale_title)
                    .setMessage(R.string.gallery_permission_rationale_msg)
                    .setPositiveButton(R.string.permission_rationale_accept_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //Requesting permission
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, HomeActivity.EXTERNAL_STORAGE_PERMISSION_CODE);
                        }
                    });
            dialog.show();
        }
        else requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, HomeActivity.EXTERNAL_STORAGE_PERMISSION_CODE);
    }

    private void useCamera()
    {
        /*Captures the user's profile pic from the camera*/

        //Checking if the app has permission to access the camera
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            getCameraPermission();
        else
        {
            //Deleting the old captured image
            File picturesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File files[] = picturesDir.listFiles();
            for(File file : files)
            {
                file.delete();
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File imageFile = createImageFile(); //The file for saving the captured image

            if(imageFile != null)
            {
                profilePicUri = FileProvider.getUriForFile(getActivity(), "com.example.unicodetask.fileprovider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, profilePicUri);
                startActivityForResult(cameraIntent, USE_CAMERA_REQUEST_CODE);
            }
        }

    }

    private void getCameraPermission()
    {
        /*Gets permission to use camera*/

        //Checking if the user should be shown permission rationale explaining the need to use camera
        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.permission_rationale_title)
                    .setMessage(R.string.camera_permission_rationale_msg)
                    .setPositiveButton(R.string.permission_rationale_accept_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //Requesting permission
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    });
            dialog.show();
        }
        else requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    private File createImageFile()
    {
        /*Creates a file for storing the captured image*/

        File imageFile = null; //The file for storing the image

        try
        {
            imageFile = File.createTempFile("profile_pic", ".jpg", getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)); //Creating the file
        }
        catch(IOException e){}

        return imageFile;
    }

}