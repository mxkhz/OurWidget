package com.dbtech.ourwidget

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.dbtech.ourwidget.databinding.FragmentGroupBinding
import com.dbtech.ourwidget.databinding.FragmentHomeScreenBinding
import com.dbtech.ourwidget.databinding.FragmentMyGroupsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
// Post item adapter is for Group Fragment and Group item adapter is for MyGroupsFragment
class GroupFragment : Fragment() {
    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!
    var posts = mutableSetOf<Post>()
    val adapter = PostItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.members.setOnClickListener {
            val action = GroupFragmentDirections.actionGroupFragmentToMembersFragment(GroupFragmentArgs.fromBundle(requireArguments()).groupId)
            view.findNavController().navigate(action)
        }
//        binding.groupId.text = groupId
        binding.addAPost.setOnClickListener {
            val action = GroupFragmentDirections.actionGroupFragmentToAddPostFragment(GroupFragmentArgs.fromBundle(requireArguments()).groupId)
            view.findNavController().navigate(action)
        }
//        posts.add(Post("https://firebasestorage.googleapis.com/v0/b/android-test-20601.appspot.com/o/images%2Fimage%3A17?alt=media&token=65977b3c-f418-42e6-9999-072ae080f848"))
//        adapter.data = posts.toList()
        binding.postsList.adapter = adapter

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//            Firebase.auth.signOut()
        val currentUser = Firebase.auth.currentUser
        if(currentUser == null){
            view?.findNavController()?.navigate(R.id.action_groupFragment_to_signUpFragment)
//                binding.eMailAddress.text = name.toString()
        }
        val groupId = GroupFragmentArgs.fromBundle(requireArguments()).groupId
        val database = Firebase.database.reference
        val currentUserId = currentUser?.uid
//        val postsRef = database.child("groups").child(groupId).child("posts")
//        database.child("groups").child(groupId).child("admin").get().addOnSuccessListener {
//            var removable = currentUser?.uid == it.value
//            val adminUid = it.value

        database.child("groups").child(groupId).child("admin").get().addOnSuccessListener {
            val databaseRef = database.child("groups").child(groupId).child("posts").orderByKey()
            val adminUid = it.value
//            var removable = currentUser?.uid == adminUid // If admin can remove all posts
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(ContentValues.TAG, "onChildAdded:" + dataSnapshot.key!!)

                    // A new comment has been added, add it to the displayed list
                    val imageRef = dataSnapshot.child("imageUrl").getValue()
                    val author = dataSnapshot.child("author").getValue()
                    var removable: Boolean = false
                    if (currentUser?.uid == adminUid) {
                        removable = true
                    } else if (currentUserId == author) {
                        removable = true
                    }
                    val postId = dataSnapshot.key.toString()
                    posts.add(Post(groupId, author.toString(), removable, postId, imageRef.toString()))
                    adapter.data = posts.toList().reversed()
//                Log.i("groupid", groupId.toString())
                }

                // ...
                override fun onChildChanged(
                    dataSnapshot: DataSnapshot,
                    previousChildName: String?
                ) {
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
        }
    }

}