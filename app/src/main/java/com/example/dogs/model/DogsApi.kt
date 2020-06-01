package com.example.dogs.model

import retrofit2.http.GET
import io.reactivex.Single

interface DogsApi {
    @GET("DevTides/DogsApi/master/dogs.json")
    fun getDogs(): Single<List<DogBreed>>
}