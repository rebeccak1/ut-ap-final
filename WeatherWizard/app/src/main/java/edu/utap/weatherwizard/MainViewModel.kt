package edu.utap.weatherwizard

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.utap.weatherwizard.glide.Glide
import edu.utap.weatherwizard.model.CityMeta

class MainViewModel : ViewModel() {
    // It is a real bummer that we need to put this here, but we do because
    // it is computed elsewhere, then we launch the camera activity
    // At that point our fragment can be destroyed, which means this has to be
    // remembered and restored.  Instead, we put it in the viewModel where we
    // know it will persist (and we can persist it)
    private var cityUUID = ""
    // Only call this from TakePictureWrapper
    fun takePictureUUID(uuid: String) {
        pictureUUID = uuid
    }
    // LiveData for entire note list, all images
    private var cityMetaList = MutableLiveData<List<CityMeta>>()

    // Track current authenticated user
    private var currentAuthUser = invalidUser
    // Firestore state
    private val storage = Storage()
    // Database access
    private val dbHelp = ViewModelDBHelper()


    /////////////////////////////////////////////////////////////
    // Notes, memory cache and database interaction
    fun fetchCityMeta(resultListener:()->Unit) {
        dbHelp.fetchCityMeta() {
            cityMetaList.postValue(it)
            resultListener.invoke()
        }
    }
    fun observeCityMeta(): LiveData<List<CityMeta>> {
        return cityMetaList
    }


    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }
    fun removePhotoAt(position: Int) {
        // XXX Deletion requires two different operations.  What are they?
        val photoMeta = getPhotoMeta(position)
        val sort = sortInfo.value!!
        storage.deleteImage(photoMeta.uuid)
        dbHelp.removePhotoMeta(sort, photoMeta){
            photoMetaList.postValue(it)
        }
    }

    // Get a note from the memory cache
    fun getCityMeta(position: Int) : CityMeta {
        val note = cityMetaList.value?.get(position)
        return note!!
    }

    private fun createCityMeta(city: String, uuid : String) {
        val currentUser = currentAuthUser
        val cityMeta = CityMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            uuid = uuid,
            city = city,
        )
        dbHelp.createCityMeta(cityMeta) {
            cityMetaList.postValue(it)
        }
    }

    /////////////////////////////////////////////////////////////
    // We can't just schedule the file upload and return.
    // The problem is that our previous picture uploads can still be pending.
    // So a note can have a pictureFileName that does not refer to an existing file.
    // That violates referential integrity, which we really like in our db (and programming
    // model).
    // So we do not add the pictureFileName to the note until the picture finishes uploading.
    // That means a user won't see their picture updates immediately, they have to
    // wait for some interaction with the server.
    // You could imagine dealing with this somehow using local files while waiting for
    // a server interaction, but that seems error prone.
    // Freezing the app during an upload also seems bad.
    fun pictureSuccess() {
        val photoFile = TakePictureWrapper.fileNameToFile(pictureUUID)
        // XXX Write me while preserving referential integrity
        storage.uploadImage(photoFile, pictureUUID) {
            createCityMeta(pictureNameByUser, pictureUUID, it)
            cityUUID = ""
        }
    }
    fun pictureFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        cityUUID = ""
    }

    fun glideFetch(uuid: String, imageView: ImageView) {
        Glide.fetch(storage.uuid2StorageReference(uuid),
            imageView)
    }
}
