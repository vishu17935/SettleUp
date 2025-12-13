package com.vishal.settleup.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.vishal.settleup.data.firebase.FirebaseRefs
import com.vishal.settleup.data.models.Group
import kotlin.random.Random

class GroupRepository {

    fun createGroup(
        groupName: String,
        userId: String,
        userName: String,
        onSuccess: (groupId: String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val groupId = FirebaseRefs.groupsRef.push().key ?: run {
            onError(IllegalStateException("Failed to generate groupId"))
            return
        }

        val joinCode = generateJoinCode()

        val group = Group(
            id = groupId,
            name = groupName,
            joinCode = joinCode,
            members = mapOf(userId to userName),
            createdAt = System.currentTimeMillis()
        )

        FirebaseRefs.groupsRef.child(groupId)
            .setValue(group)
            .addOnSuccessListener { onSuccess(groupId) }
            .addOnFailureListener { onError(it) }
    }

    fun joinGroupByCode(
        code: String,
        userId: String,
        userName: String,
        onSuccess: (groupId: String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        // Search all groups and find matching code (fine for 5-6 users)
        FirebaseRefs.groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val match = snapshot.children.firstOrNull { child ->
                    child.child("joinCode").getValue(String::class.java) == code
                }

                val groupId = match?.key
                if (groupId == null) {
                    onError(IllegalArgumentException("Invalid group code"))
                    return
                }

                // Add member
                FirebaseRefs.groupsRef.child(groupId).child("members").child(userId)
                    .setValue(userName)
                    .addOnSuccessListener { onSuccess(groupId) }
                    .addOnFailureListener { onError(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    fun observeGroup(
        groupId: String,
        onSuccess: (Group) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        FirebaseRefs.groupsRef.child(groupId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val group = snapshot.getValue(Group::class.java)
                    if (group != null) onSuccess(group)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    private fun generateJoinCode(): String {
        // 6-digit numeric code with leading zeros possible
        val n = Random.nextInt(0, 1_000_000)
        return n.toString().padStart(6, '0')
    }
}
