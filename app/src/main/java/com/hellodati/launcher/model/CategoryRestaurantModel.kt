package com.hellodati.launcher.model

data class CategoryRestaurantModel(
    val title: String,
    val dishRestaurantModels: List<DishRestaurantModel>
)