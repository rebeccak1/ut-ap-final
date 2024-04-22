package edu.utap.weatherwizard

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.weatherwizard.model.CityMeta
import edu.utap.weatherwizard.model.UnitsMeta

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allCityMeta"
    private val unitsCollection = "allUnits"

    private fun limitAndGet(query: Query,
                            resultListener: (List<CityMeta>)->Unit) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d("XXX", "allNotes fetch ${result!!.documents.size}")
                resultListener(result.documents.mapNotNull {
                    it.toObject(CityMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d("XXX", "allNotes fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    private fun limitAndGetUnits(query: Query,
                                 resultListener: (List<UnitsMeta>)->Unit) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d("XXX", "allNotes fetch ${result!!.documents.size}")
                resultListener(result.documents.mapNotNull {
                    it.toObject(UnitsMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d("XXX", "allNotes fetch FAILED ", it)
                resultListener(listOf())
            }
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    fun fetchCityMeta(user: String,
        resultListener: (List<CityMeta>) -> Unit
    ) {
        Log.d("XXX", "fetching in view model")
        val query = db.collection(rootCollection).orderBy("city", Query.Direction.ASCENDING).whereEqualTo("ownerUid", user)
        limitAndGet(query, resultListener)

    }

    fun fetchUnitsMeta(user: String,
                      resultListener: (List<UnitsMeta>) -> Unit
    ) {
        Log.d("XXX", "fetching in view model")
        val query = db.collection(unitsCollection).orderBy("units", Query.Direction.ASCENDING).whereEqualTo("ownerUid", user)
        limitAndGetUnits(query, resultListener)

    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createCityMeta(user: String,
        cityMeta: CityMeta,
        resultListener: (List<CityMeta>)->Unit
    ) {
        db.collection(rootCollection)
            .add(cityMeta)
            .addOnSuccessListener {
                fetchCityMeta(user, resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note create FAILED ")
                Log.w(javaClass.simpleName, "Error", e)
            }

    }

    fun updateCityMeta(user: String,
                       cityMeta: CityMeta, newHome: Boolean, newFavorite: Boolean,
                       resultListener: (List<CityMeta>)->Unit
    ){
        db.collection(rootCollection)
            .document(cityMeta.firestoreID)
            .update("home", newHome, "favorite", newFavorite)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Note update id: ${cityMeta.firestoreID}"
                )
                fetchCityMeta(user, resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note updating FAILED ")
                Log.w(javaClass.simpleName, "Error adding document", e)
            }
    }

    fun updateUnitsMeta(user: String,
                       unitsMeta: UnitsMeta, newUnits:String,
                       resultListener: (List<UnitsMeta>)->Unit
    ){
        db.collection(unitsCollection)
            .document(unitsMeta.firestoreID)
            .update("units", newUnits)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Note update id: ${unitsMeta.firestoreID}"
                )
                fetchUnitsMeta(user, resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note updating FAILED ")
                Log.w(javaClass.simpleName, "Error adding document", e)
            }
    }


    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removeCityMeta(user: String,
        cityMeta: CityMeta,
        resultListener: (List<CityMeta>)->Unit
    ) {
        db.collection(rootCollection)
            .document(cityMeta.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Note delete  id: ${cityMeta.firestoreID}"
                )
                fetchCityMeta(user, resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note deleting FAILED ")
                Log.w(javaClass.simpleName, "Error adding document", e)
            }

    }

    fun createUnitsMeta(user: String,
                       unitsMeta: UnitsMeta,
                       resultListener: (List<UnitsMeta>)->Unit
    ) {
        db.collection(unitsCollection)
            .add(unitsMeta)
            .addOnSuccessListener {
                fetchUnitsMeta(user, resultListener)
            }
            .addOnFailureListener { e->
                Log.d(javaClass.simpleName, "Note create FAILED ")
                Log.w(javaClass.simpleName, "Error", e)
            }

    }
}