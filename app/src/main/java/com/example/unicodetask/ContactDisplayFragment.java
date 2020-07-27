package com.example.unicodetask;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ContactDisplayFragment extends Fragment
{
    private String contactId; //The id of the contact to be displayed
    private String contactDisplayName; //The display name of the contact

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_contact_display, container, false);

        //Displaying the contact details
        displayContactDetails(fragmentView);

        return fragmentView;
    }

    void setContactDetails(Bundle contactDetails)
    {
       /*Gets the details(id and display name) of the contact to be displayed*/

        contactId = contactDetails.getString(ContactListFragment.BUNDLE_CONTACT_ID);
        contactDisplayName = contactDetails.getString(ContactListFragment.BUNDLE_CONTACT_NAME);
    }

    void displayContactDetails(View fragmentView)
    {
        /*Displays the contact details*/

        //Getting the contact display name
        ((TextView)fragmentView.findViewById(R.id.contact_displayname)).setText(contactDisplayName); //Displaying the contact display name

        //Getting the contact
        Cursor contactDetails = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{this.contactId}, null);
        contactDetails.moveToFirst(); //Pointing to the 1st row

        //Getting the contact phone number
        String contactNum = contactDetails.getString(contactDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        ((TextView)fragmentView.findViewById(R.id.contact_number)).setText(contactNum); //Displaying the contact Number
    }
}