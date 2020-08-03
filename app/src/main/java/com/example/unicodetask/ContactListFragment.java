package com.example.unicodetask;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactListFragment extends Fragment
{
    private int CONTACTS_PERMISSION_REQUEST_CODE = 0;
    static String BUNDLE_CONTACT_ID = "_ID";
    static String BUNDLE_CONTACT_NAME = "DISPLAY_NAME";
    static String BUNDLE_CONTACT_NUMBER = "NUM";

    private Context fragmentActivity; //The activity with which the fragment is associated
    private View fragmentView; //The view associated with the fragment

    private boolean contactPermissionGranted = false; //Whether the contact permission has been granted

    private AdapterView.OnItemClickListener contactClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {

            TextView contactCard = (TextView)view.findViewById(R.id.contact_card_contact_name);

            //Bundling the contact details
            Bundle contactDetails = new Bundle();
            contactDetails.putString(BUNDLE_CONTACT_ID, contactCard.getTag().toString());
            contactDetails.putString(BUNDLE_CONTACT_NAME, contactCard.getText().toString());

            //Creating a contact display fragment for displaying the selected contact
            ContactDisplayFragment contactDisplayFragment = new ContactDisplayFragment();
            contactDisplayFragment.setContactDetails(contactDetails);

            //Displaying the fragment
            ((HomeActivity)fragmentActivity).displayFragment(contactDisplayFragment, true);

        }
    }; //Listens for clicks in the contacts list

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fragmentActivity = getActivity(); //Getting the associated activity

        //Getting the required permissions
        contactPermissionGranted = getContactPermissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_contact_list, container, false);

        //Displaying the contact list
        if(contactPermissionGranted) getAndDisplayContactList();

        return fragmentView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[])
    {
        //Checking if permission was granted
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //Displaying the contact list
            getAndDisplayContactList();

            contactPermissionGranted = true;
        }
        else contactPermissionGranted = false;
    }

    private boolean getContactPermissions()
    {
        /*Checks and returns whether the app has permission to read contacts. If not, gets the permissions required for viewing contact details*/

        //Checking if the app has permission to read contacts
        if(ContextCompat.checkSelfPermission(fragmentActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
            {
                //Creating the dialog used for showing the message
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(fragmentActivity);
                dialogBuilder.setTitle(R.string.permission_rationale_title)
                        .setMessage(R.string.contacts_permission_rationale_msg)
                        .setPositiveButton(R.string.permission_rationale_accept_btn, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                //Requesting permission to access contacts
                                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_REQUEST_CODE);
                            }
                        });
                dialogBuilder.show();
            }
            else requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_REQUEST_CODE); //Requesting permission to access contacts

            return false;
        }

        return true;
    }

    private void getAndDisplayContactList()
    {
        /*Retrieves and displays the contact list*/

        Cursor contactsCursor = fragmentActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"); //Gets a cursor with the contacts list

        //Initializing the adapter
        ContactsAdapter contactsAdapter = new ContactsAdapter(fragmentActivity, contactsCursor);

        //Attaching the adapter to the list view
        ListView contactsList = (ListView)fragmentView.findViewById(R.id.contacts_list);
        contactsList.setAdapter(contactsAdapter);
        contactsList.setOnItemClickListener(contactClickListener);
    }
}

class ContactsAdapter extends CursorAdapter
{
    ContactsAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.contact_card, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        //Getting the contact name
        String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        //Setting the contact name
        TextView contactCard = (TextView)view.findViewById(R.id.contact_card_contact_name);
        contactCard.setText(contactName);

        //Storing the contact id in the view's tag
        contactCard.setTag(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
    }
}