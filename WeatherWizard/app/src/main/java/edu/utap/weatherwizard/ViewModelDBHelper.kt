package edu.utap.weatherwizard

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.weatherwizard.model.CityMeta

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allCityMeta"

    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    private fun limitAndGet(query: Query,
                            resultListener: (List<CityMeta>)->Unit) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(CityMeta::class.java)
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
    fun fetchCityMeta(
        resultListener: (List<CityMeta>) -> Unit
    ) {
        // XXX Write me and use limitAndGet

        val query = db.collection(rootCollection).orderBy("city", Query.Direction.ASCENDING)
        limitAndGet(query, resultListener)

    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createCityMeta(
        cityMeta: CityMeta,
        resultListener: (List<CityMeta>)->Unit
    ) {
        // XXX Write me: add photoMeta
        db.collection(rootCollection)
            .add(cityMeta)
            .addOnSuccessListener {
                fetchCityMeta(resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note create FAILED ")
                Log.w(javaClass.simpleName, "Error", e)
            }

    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removeCityMeta(
        cityMeta: CityMeta,
        resultListener: (List<CityMeta>)->Unit
    ) {
        // XXX Write me.  Make sure you delete the correct entry.  What uniquely identifies a photoMeta?
        db.collection(rootCollection)
            .document(cityMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Note delete  id: ${cityMeta.firestoreID}"
                )
                fetchCityMeta(resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note deleting FAILED ")
                Log.w(javaClass.simpleName, "Error adding document", e)
            }

    }
}