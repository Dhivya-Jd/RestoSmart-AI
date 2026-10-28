package com.example.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.IngredientEntity
import com.example.data.local.entity.MenuItemEntity
import com.example.model.MenuItem

data class MenuItemWithIngredients(
    @Embedded val menuItem: MenuItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "menuItemId"
    )
    val ingredients: List<IngredientEntity> = emptyList()
) {
    fun toDomain(): MenuItem {
        return menuItem.toDomain(ingredients.map { it.toDomain() })
    }
}
