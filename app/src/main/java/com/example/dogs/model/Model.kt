package com.example.dogs.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class DogBreed (
    @ColumnInfo(name = "breed_id")
    @SerializedName("id")
    val breedId:String?,

    @ColumnInfo(name = "dog_name")
    @SerializedName("name")
    val dogBreed: String?,

    @ColumnInfo(name = "life_span")
    @SerializedName("life_span")
    val lifeSpan: String?,

    @SerializedName("breed_group")
    @ColumnInfo(name = "bred_group")
    val breedGroup: String?,

    @SerializedName("bred_for")
    @ColumnInfo(name = "bred_for")
    val breedFor: String?,

    @SerializedName("temperament")
    @ColumnInfo(name = "temperament")
    val temperament:String?,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    val imageUrl:String?
){
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}

data class DogPallete(var color:Int)

data class SmsInfo(var to:String, var text:String,var imageUrl:String?)