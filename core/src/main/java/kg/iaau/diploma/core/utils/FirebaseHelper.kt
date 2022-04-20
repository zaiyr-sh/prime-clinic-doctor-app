package kg.iaau.diploma.core.utils

import android.net.Uri
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

object FirebaseHelper {

    fun addCallListener(userId: String, listener: ((uid: String) -> Unit)? = null) {
        val ref = FirebaseFirestore.getInstance().collection("doctors").document(userId)
            .collection("call").document("calling")
        ref.addSnapshotListener { value, _ ->
            if (value != null && value.exists()) {
                value.getString("uid")?.let {
                    val uid = value.getString("uid")
                    val accepted = value.getBoolean("accepted")
                    if (!uid.isNullOrEmpty() && accepted == false) listener?.invoke(uid)
                }
            }
        }
    }

    fun setUserOnline(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val map = mutableMapOf<String, Any>()
        map["isOnline"] = true
        db.collection("users").document(userId).set(map, SetOptions.merge())
    }

    fun addAdminChatListener(
        userId: String,
        onSuccess: ((doc: DocumentSnapshot) -> Unit)? = null,
        onFail: ((ex: Exception) -> Unit)? = null
    ) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chatAdmin").document(userId)
        ref.get().addOnSuccessListener { doc ->
            onSuccess?.invoke(doc)
        }.addOnFailureListener {
            onFail?.invoke(it)
        }
    }

    inline fun <reified T> createChannels(userId: String): FirestoreRecyclerOptions<T> {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("PrimeDocChat").whereEqualTo("adminId", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<T>().setQuery(query, T::class.java).build()
    }

    fun setupUserType(
        docRef: DocumentReference?,
        listener: ((adminId: String?) -> Unit)? = null
    ) {
        docRef?.get()?.addOnSuccessListener {
            val adminId = it.getString("clientId")
            listener?.invoke(adminId)
        }
    }

    fun setupPatientData(
        userId: String?,
        doctorChatListener: ((doc: DocumentSnapshot) -> Unit)?
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId!!).get().addOnSuccessListener {
            doctorChatListener?.invoke(it)
        }
    }

    inline fun <reified T> setupChat(
        docRef: DocumentReference,
        crossinline onSuccess: ((options: FirestoreRecyclerOptions<T>) -> Unit)
    ) {
        val query = docRef.collection("messages").orderBy("time", Query.Direction.ASCENDING)
        val options: FirestoreRecyclerOptions<T> =
            FirestoreRecyclerOptions.Builder<T>().setQuery(query, T::class.java)
                .build()
        docRef.collection("messages").addSnapshotListener { _, _ ->
            onSuccess(options)
        }
    }

    fun uploadPhoto(imgUri: Uri, onSuccess: ((url: String) -> Unit)? = null, onDefault: (() -> Unit)? = null) {
        val ref = FirebaseStorage.getInstance().getReference("images/" + Date().time + ".jpg")
        ref.putFile(imgUri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                val url = it.toString()
                if (url.isNotEmpty()) {
                    onSuccess?.invoke(url)
                }
                onDefault?.invoke()
            }
        }
    }

    fun getCallData(
        uid: String,
        receiverId: String,
        accepted: Boolean,
        declined: Boolean
    ): MutableMap<String, Any> {
        val callData = mutableMapOf<String, Any>()
        callData["uid"] = uid
        callData["receiverId"] = receiverId
        callData["accepted"] = accepted
        callData["declined"] = declined
        return callData
    }

    fun makeCall(
        userId: String,
        onSuccess: ((ref: DocumentReference) -> Unit)? = null,
        onFail: ((ref: DocumentReference) -> Unit)? = null
    ) {
        val db = FirebaseFirestore.getInstance()
        val ref =
            db.collection("users").document(userId).collection("call").document("calling")
        ref.get().addOnSuccessListener {
            when (it.exists() && !it.getString("uid").isNullOrEmpty()) {
                true -> onFail?.invoke(ref)
                else -> onSuccess?.invoke(ref)
            }
        }
    }

    fun addCallAcceptanceListener(
        ref: DocumentReference,
        onSuccess: (() -> Unit)? = null,
        onFail: (() -> Unit)? = null
    ): ListenerRegistration {
        return ref.addSnapshotListener { value, _ ->
            if (value != null && value.exists()) {
                val accepted = value.getBoolean("accepted")
                val declined = value.getBoolean("declined")
                if (accepted == true)
                    onSuccess?.invoke()
                if (declined != null && declined == true)
                    onFail?.invoke()
            }
        }
    }

}