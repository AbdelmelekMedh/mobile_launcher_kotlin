package com.hellodati.launcher.model

data class CategoryModel(
    val title: String,
    val subItemModels: List<DishItemModel>
)