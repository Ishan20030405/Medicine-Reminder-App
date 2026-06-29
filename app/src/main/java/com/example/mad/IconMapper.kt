package com.example.mad

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    fun getIconForName(name: String): ImageVector {
        return when (name) {
            "Tablet" -> Icons.Default.Circle
            "Capsule" -> Icons.Default.Circle
            "Spray" -> Icons.Default.Cyclone
            "Gel" -> Icons.Default.Opacity
            "Cream" -> Icons.Default.Opacity
            "Injection" -> Icons.Default.Edit
            "Powder" -> Icons.Default.Grain
            "Inhaler" -> Icons.Default.Air
            "Gummy" -> Icons.Default.Favorite
            "Herb" -> Icons.Default.Spa
            "Ampoule" -> Icons.Default.Info
            "Softgel" -> Icons.Default.Lens
            "Chewy bite" -> Icons.Default.Restaurant
            "Drops" -> Icons.Default.WaterDrop
            "Lotion" -> Icons.Default.CleanHands
            "Liquid" -> Icons.Default.LocalPharmacy
            else -> Icons.Default.MedicalServices
        }
    }
}
