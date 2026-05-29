package com.deoony.app.ui.util

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Face

fun getIconByName(name: String): ImageVector? {
    return when (name) {
        "person" -> Icons.Default.Person
        "work" -> Icons.Default.Work
        "home" -> Icons.Default.Home
        "star" -> Icons.Default.Star
        "favorite" -> Icons.Default.Favorite
        "shopping_cart" -> Icons.Default.ShoppingCart
        "school" -> Icons.Default.School
        "face" -> Icons.Default.Face
        else -> null
    }
}
