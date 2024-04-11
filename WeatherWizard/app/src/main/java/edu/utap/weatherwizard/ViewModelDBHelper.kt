package edu.utap.weatherwizard

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.weatherwizard.model.PhotoMeta

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allPhotos"

    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    private fun limitAndGet(query: Query,
                            resultListener: (List<PhotoMeta>)->Unit) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(PhotoMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
                resultListener(listOf())
            }
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    fun fetchPhotoMeta(
        sortInfo: SortInfo,
        resultListener: (List<PhotoMeta>) -> Unit
    ) {
        // XXX Write me and use limitAndGet
        val direction = if(sortInfo.ascending) Query.Direction.ASCENDING else Query.Direction.DESCENDING
        val column = if(sortInfo.sortColumn.name=="TITLE") "pictureTitle" else "byteSize"
        val query = db.collection(rootCollection).orderBy(column, direction)
        limitAndGet(query, resultListener)

    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createPhotoMeta(
        sortInfo: SortInfo,
        photoMeta: PhotoMeta,
        resultListener: (List<PhotoMeta>)->Unit
    ) {
        // XXX Write me: add photoMeta
        db.collection(rootCollection)
            .add(photoMeta)
            .addOnSuccessListener {
                fetchPhotoMeta(sortInfo, resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note create FAILED ")
                Log.w(javaClass.simpleName, "Error", e)
            }

    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removePhotoMeta(
        sortInfo: SortInfo,
        photoMeta: PhotoMeta,
        resultListener: (List<PhotoMeta>)->Unit
    ) {
        // XXX Write me.  Make sure you delete the correct entry.  What uniquely identifies a photoMeta?
        db.collection(rootCollection)
            .document(photoMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Note delete  id: ${photoMeta.firestoreID}"
                )
                fetchPhotoMeta(sortInfo, resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note deleting FAILED ")
                Log.w(javaClass.simpleName, "Error adding document", e)
            }

    }
}