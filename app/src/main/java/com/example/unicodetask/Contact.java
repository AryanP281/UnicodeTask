package com.example.unicodetask;

public class Contact
{
    String contactId; //The id of the contact in the database
    String contactName; //The name of the contact
    String contactNumber; //The phone number of the contact

    Contact()
    {
        contactId = null;
        contactName = null;
        contactNumber = null;
    }

    Contact(String name, String number, String id)
    {
        this.contactNumber = number;
        this.contactName = name;
        this.contactId = id;
    }
}
