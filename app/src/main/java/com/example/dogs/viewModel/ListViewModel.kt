package com.example.dogs.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogs.model.DogBreed
import com.example.dogs.model.DogDao
import com.example.dogs.model.DogDatabase
import com.example.dogs.model.DogsApiService
import com.example.dogs.util.NotificationsHelper
import com.example.dogs.util.SharePreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application) :BaseViewModel(application) {
    private var prefHelper = SharePreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L
    private val dogsServices = DogsApiService()
    private val disposable = CompositeDisposable()


    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }



    fun refresh(){
        checkCacheDuration()
        val updateTime  = prefHelper.getUpdateTime()
        if (updateTime!=null && updateTime != 0L && System.nanoTime()-updateTime < refreshTime){
            fetchFromDatabase()
        } else{
            fetchFromRemote()
        }
    }

    fun refreshByPassCache(){
        fetchFromRemote()
    }



    private fun checkCacheDuration(){
        val cachePreferenes = prefHelper.getCacheDuration()

        try {
            val cachePreferenceInt = cachePreferenes?.toInt()?:5*60
            refreshTime = cachePreferenceInt.times( 1000 * 1000 * 1000L)
        } catch (e:NumberFormatException){
            e.printStackTrace()
        }
    }

    private  fun fetchFromDatabase(){
        loading.value = true;
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(),"Dogs retrived from database ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromRemote(){
        loading.value = true;
        disposable.add(
            dogsServices.getDogs().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object:DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication(),"Dogs retrived from endpoint ", Toast.LENGTH_SHORT).show()

                        NotificationsHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                       dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    private fun dogsRetrieved(dogList:List<DogBreed>){
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list:List<DogBreed>){
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result  = dao.insertAll(*list.toTypedArray())
            var i=0
            while (i<list.size){
                list[i].uuid = result[i].toInt()
                i++;
            }
            dogsRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }
}