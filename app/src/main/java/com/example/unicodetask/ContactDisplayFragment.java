package com.example.unicodetask;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ContactDisplayFragment extends Fragment
{
    private Contact contact; //The displayed contact

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            contact = new Contact(savedInstanceState.getString(ContactListFragment.BUNDLE_CONTACT_NAME),
                    savedInstanceState.getString(ContactListFragment.BUNDLE_CONTACT_NUMBER), savedInstanceState.getString(ContactListFragment.BUNDLE_CONTACT_ID)); //Getting the contact
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_contact_display, container, false);

        //Displaying the contact details
        displayContactDetails(fragmentView);

        //Enabling toolbar menu
        setHasOptionsMenu(true);

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        /*Inflating the toolbar menu*/

        inflater.inflate(R.menu.contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*Handles menu item clicks*/

        //Determining which item has been clicked
        switch (item.getItemId())
        {
            case R.id.menuitem_dial : dialContact(); break;
            case R.id.menuitem_share_contact : shareContact(); break;
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        //Saving the contact info
        outState.putString(ContactListFragment.BUNDLE_CONTACT_NAME, contact.contactName);
        outState.putString(ContactListFragment.BUNDLE_CONTACT_ID, contact.contactId);
        outState.putString(ContactListFragment.BUNDLE_CONTACT_NUMBER, contact.contactNumber);
    }

    void setContactDetails(Bundle contactDetails)
    {
       /*Gets the details(id and display name) of the contact to be displayed*/

        this.contact = new Contact(contactDetails.getString(ContactListFragment.BUNDLE_CONTACT_NAME), null,
                contactDetails.getString(ContactListFragment.BUNDLE_CONTACT_ID));
    }

    void displayContactDetails(View fragmentView)
    {
        /*Displays the contact details*/

        //Getting the contact display name
        ((TextView)fragmentView.findViewById(R.id.contact_displayname)).setText(contact.contactName); //Displaying the contact display name

        //Getting the contact
        Cursor contactDetails = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact.contactId}, null);
        contactDetails.moveToFirst(); //Pointing to the 1st row

        //Getting the contact phone number
        contact.contactNumber = contactDetails.getString(contactDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        ((TextView)fragmentView.findViewById(R.id.contact_number)).setText(contact.contactNumber); //Displaying the contact Number
    }

    private void dialContact()
    {
        /*Dials the currently displayed contact*/

        Intent dialIntent = new Intent(Intent.ACTION_DIAL); //The intent to dial the contact
        dialIntent.setData(Uri.parse(String.format("tel:%s",contact.contactNumber)));  //Setting the contact number
        if(dialIntent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(dialIntent);
    }

    private void shareContact()
    {
        /*Shares the currently displayed contact*/

        Intent intent = new Intent(Intent.ACTION_SEND); //The intent to send the contact info
        intent.putExtra(Intent.EXTRA_TEXT, String.format("Name: %s\nPhone: %s", contact.contactName, contact.contactNumber));
        intent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(intent,null);
        startActivity(shareIntent);
    }
}