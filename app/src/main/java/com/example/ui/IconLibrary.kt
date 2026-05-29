package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class AppIconItem(
    val name: String,
    val icon: ImageVector,
    val labelAr: String
)

object IconLibrary {
    val iconsList = listOf(
        AppIconItem("folder", Icons.Default.Folder, "عام"),
        AppIconItem("person", Icons.Default.Person, "شخصي"),
        AppIconItem("work", Icons.Default.Work, "عمل"),
        AppIconItem("home", Icons.Default.Home, "منزل / سكن"),
        AppIconItem("wallet", Icons.Default.AccountBalanceWallet, "محفظة"),
        AppIconItem("bank", Icons.Default.AccountBalance, "بنك"),
        AppIconItem("shopping_cart", Icons.Default.ShoppingCart, "مقاضي"),
        AppIconItem("directions_car", Icons.Default.DirectionsCar, "سيارة"),
        AppIconItem("restaurant", Icons.Default.Restaurant, "طعام"),
        AppIconItem("school", Icons.Default.School, "دراسة"),
        AppIconItem("call", Icons.Default.Call, "إتصالات"),
        AppIconItem("email", Icons.Default.Email, "فواتير"),
        AppIconItem("star", Icons.Default.Star, "مهم"),
        AppIconItem("favorite", Icons.Default.Favorite, "عائلة"),
        AppIconItem("settings", Icons.Default.Settings, "إعدادات"),
        AppIconItem("warning", Icons.Default.Warning, "طوارئ"),
        AppIconItem("info", Icons.Default.Info, "بيان"),
        AppIconItem("search", Icons.Default.Search, "بحث"),
        AppIconItem("location", Icons.Default.LocationOn, "سفر"),
        AppIconItem("notifications", Icons.Default.Notifications, "إشعار"),
        AppIconItem("lock", Icons.Default.Lock, "ضمان"),
        AppIconItem("build", Icons.Default.Build, "صيانة"),
        AppIconItem("gift", Icons.Default.CardGiftcard, "هدية"),
        AppIconItem("face", Icons.Default.Face, "تسلية"),
        AppIconItem("tech", Icons.Default.Keyboard, "أجهزة"),
        AppIconItem("list", Icons.Default.List, "سجل"),
        AppIconItem("gas", Icons.Default.LocalGasStation, "وقود"),
        AppIconItem("shop", Icons.Default.LocalMall, "ملابس"),
        AppIconItem("thumb", Icons.Default.ThumbUp, "تمت"),
        AppIconItem("alarm", Icons.Default.Alarm, "سداد")
    )

    fun getIconByName(name: String, fallback: ImageVector = Icons.Default.Folder): ImageVector {
        return iconsList.find { it.name.lowercase() == name.lowercase() }?.icon ?: fallback
    }
}
