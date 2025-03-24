package com.example.calorie_app_prototype.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

data class NutritionResponse(val items: List<NutritionItem>)
data class NutritionItem(val name: String, val calories: Double)

interface CalorieApiService {
    @Headers("X-Api-Key: fZConAlzmfdu8kI9iDQv9g==6bp2Xj8xsQurv4VX")  // Replace YOUR_API_KEY with your actual API key
    @GET("v1/nutrition")
    fun getNutrition(@Query("query") foodName: String): Call<NutritionResponse>
}
