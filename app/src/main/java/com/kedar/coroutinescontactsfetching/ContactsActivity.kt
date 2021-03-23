package com.kedar.coroutinescontactsfetching

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_contacts.*
import java.util.*
import kotlin.collections.ArrayList


class ContactsActivity : AppCompatActivity() {
    private val contactsViewModel by viewModels<ContactsViewModel>()
    private val CONTACTS_READ_REQ_CODE = 100
    private var timer: Timer? = null
    private lateinit var tvAllContact : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        tvAllContact = findViewById(R.id.tvAllContact)

        init()

        search.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {


                        val str = search.text.toString()
                        gettingContactNumber(str)


                    }
                }, 600)



            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timer?.cancel()
                tvAllContact.text = "Searching....."

            }

        })
    }

    private fun init() {
        tvDefault.text = "Fetching contacts!!!"

        val adapter = ContactsAdapter(this)
        rvContacts.adapter = adapter
        contactsViewModel.contactsLiveData.observe(this, Observer {
            tvDefault.visibility = View.GONE
            adapter.contacts = it

            var count = rvContacts.adapter!!.itemCount
            //tvAllContact.text = "Found $count Matching  Contact"
            val str = search.text.toString()
            if (str.isEmpty()){
                tvAllContact.text = "All Contact"
            }else{
                tvAllContact.text = "Found $count Matching  Contact"
            }

        })
        /*
        if (hasPermission(Manifest.permission.READ_CONTACTS)) {
            contactsViewModel.fetchContacts("")
        } else {
            requestPermissionWithRationale(
                Manifest.permission.READ_CONTACTS,
                CONTACTS_READ_REQ_CODE,
                getString(R.string.contact_permission_rationale)
            )
        }*/

        gettingContactNumber("")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_READ_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gettingContactNumber("")
        }

    }

    fun gettingContactNumber(searchString: String){
        if (hasPermission(Manifest.permission.READ_CONTACTS)) {
            contactsViewModel.fetchContacts(searchString)
        } else {
            requestPermissionWithRationale(
                    Manifest.permission.READ_CONTACTS,
                    CONTACTS_READ_REQ_CODE,
                    getString(R.string.contact_permission_rationale)
            )
        }
    }


    fun getContactDisplayNameByNumber(number: String?): ArrayList<Contact>? {
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        var namelist = ArrayList<Contact>()
        val contentResolver = contentResolver
        val contactLookup: Cursor? = contentResolver.query(
            uri, arrayOf(
                BaseColumns._ID,
                ContactsContract.PhoneLookup.LOOKUP_KEY
            ), null, null, null
        )
        try {
            while (contactLookup != null && contactLookup.getCount() > 0) {
                // if (contactLookup != null && contactLookup.getCount() > 0) {
                //contactLookup?.moveToNext()
                var id =
                    contactLookup?.getString(contactLookup.getColumnIndex(ContactsContract.Data.CONTACT_ID))
                var name =
                    contactLookup?.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                //}
                var contact = Contact(id, name)
                namelist.add(contact)
                Log.e("Valuset---", namelist.toString())

            }

        } finally {
            if (contactLookup != null) {
                contactLookup.close()
            }
        }
        return namelist
    }
}
