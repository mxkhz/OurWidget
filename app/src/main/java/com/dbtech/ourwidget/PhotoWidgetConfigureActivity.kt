package com.dbtech.ourwidget

import android.appwidget.AppWidgetManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.ActivityPhotoWidgetConfigureBinding
import com.dbtech.ourwidget.databinding.PhotoWidgetBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.prefs.Preferences
import java.util.prefs.PreferencesFactory

//class  : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_photo_widget_configure)
//    }
//}

class PhotoWidgetConfigureActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    var groups = mutableSetOf<Group>()
    val adapter = GroupItemWidetAdapter()
    var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    val _this = this
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        val binding = ActivityPhotoWidgetConfigureBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.groupsList.adapter = adapter
//        title = getString(R.string.select_list_for_widget)

//        binding.groceryListContainer.setOnClickListener {
//            onWidgetContainerClicked(R.layout.widget_grocery_list)
//        }
//        binding.todoListContainer.setOnClickListener {
//            onWidgetContainerClicked(R.layout.widget_todo_list)
//        }
        val currentUser = Firebase.auth.currentUser
        if(currentUser == null){
            binding.root.rootView?.findNavController()?.navigate(R.id.action_myGroupsFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
        lateinit var userId : String
        currentUser?.let {
            // Name, email address, and profile photo Url
            userId = currentUser.uid
        }

        database = Firebase.database.reference
        val databaseRef = database.child("userData").child(userId).child("groups")

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
                val groupId = dataSnapshot.key
                val groupName = dataSnapshot.getValue()
                database.child("groups").child(groupId.toString()).child("groupName").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    if(it.value.toString() != "null" || it.value != null) {
                        groups.add(
                            Group(
                                groupId.toString(),
                                it.value.toString(),
                                ::onWidgetContainerClicked
                            )
                        )
                        adapter.data = groups.toList()
                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }

                Log.i("groupid", groupId.toString())
            }
            // ...
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newComment = dataSnapshot.getValue()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(ContentValues.TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(binding.root.context, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        databaseRef.addChildEventListener(childEventListener)

        // Find the widget id from the intent.
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    fun onWidgetContainerClicked(groupId: String) {
//        ListSharedPrefsUtil.saveWidgetLayoutIdPref(this, appWidgetId, widgetLayoutResId)
        // It is the responsibility of the configuration activity to update the app widget
        val sharedPref = this.getSharedPreferences("GROUP_ID", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(appWidgetId.toString(), groupId)
            apply()
        }
        val appWidgetManager = AppWidgetManager.getInstance(this)
        updateAppWidget(this, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}