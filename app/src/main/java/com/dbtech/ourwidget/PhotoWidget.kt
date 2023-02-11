package com.dbtech.ourwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 * Implementation of App Widget functionality.
 */


class PhotoWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    }


internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.photo_widget)
//    var widgetText = "hiiiii"
    // Construct the RemoteViews object

//    views.setTextViewText(R.id.appwidget_text, widgetText)

    // ###### Start Call to databse
        val database = Firebase.database.reference

    val sharedPref = context.getSharedPreferences(
        "GROUP_ID", Context.MODE_PRIVATE)
//    val sharedPref = context.getSharedPreferences(
//        R.string.group_id.toString(), Context.MODE_PRIVATE)
        Log.i(TAG, sharedPref.getString(appWidgetId.toString(), "NOTHING")!!)
//        appWidgetManager.updateAppWidget(appWidgetId, views)
            val databaseRef =
                database.child("groups").child(sharedPref.getString(appWidgetId.toString(), "").toString()).child("posts").orderByKey().limitToLast(1)
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(ContentValues.TAG, "onChildAdded:" + dataSnapshot.key!!)
//                    widgetText = dataSnapshot.key.toString()
                    val imageRef = dataSnapshot.child("imageUrl").getValue()
                    val storageRef = Firebase.storage.reference
                    val imageStorageRef = storageRef.child(imageRef.toString())
                    imageStorageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { byteArray ->
                        // Use the bytes to display the image
//                val byteArray: ByteArray = stream.toByteArray()
//                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.photo)
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                        views.setBitmap(R.id.photo, "setImageBitmap", bitmap)

                        views.setImageViewBitmap(R.id.photo, bitmap)
                        appWidgetManager.updateAppWidget(appWidgetId, views)

                    }.addOnFailureListener {
                        // Handle any errors
                        Log.i(TAG, "Filed to load image")
                    }
                }

                // ...
                override fun onChildChanged(
                    dataSnapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    Log.d(ContentValues.TAG, "onChildChanged: ${dataSnapshot.key}")
                    val imageRef = dataSnapshot.child("imageUrl").getValue()
                    val storageRef = Firebase.storage.reference
                    val imageStorageRef = storageRef.child(imageRef.toString())
                    imageStorageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { byteArray ->
                        // Use the bytes to display the image
//                val byteArray: ByteArray = stream.toByteArray()
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.photo)
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                        views.setBitmap(R.id.photo, "setImageBitmap", bitmap)

                        views.setImageViewBitmap(R.id.photo, bitmap)
                        appWidgetManager.updateAppWidget(appWidgetId, views)

                    }.addOnFailureListener {
                        // Handle any errors
                        Log.i(TAG, "Filed to load image")
                    }
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
                    Log.w(
                        ContentValues.TAG,
                        "postComments:onCancelled",
                        databaseError.toException()
                    )
                    Toast.makeText(
                        context, "Failed to load comments.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            databaseRef.addChildEventListener(childEventListener)

            // ########## End Call to database


}
