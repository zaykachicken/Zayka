package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Classic Food Delivery Theme (Premium Zomato Red & Sunset Orange)
val Primary = Color(0xFFE23744) // Classic Food Crimson Red
val PrimaryDark = Color(0xFFCB202D)
val PrimaryLight = Color(0xFFFFEBEE)
val PrimaryContainer = Color(0xFFFFF5F5)
val OnPrimaryContainer = Color(0xFF900C1B)

val Secondary = Color(0xFFFC8019) // Swiggy Sunset Orange
val SecondaryDark = Color(0xFFE06805)
val SecondaryLight = Color(0xFFFFF3E0)
val SecondaryContainer = Color(0xFFFFE0B2)
val OnSecondaryContainer = Color(0xFF6D2800)

val Tertiary = Color(0xFF25A75C) // Fresh Veg Green
val TertiaryDark = Color(0xFF1B8044)
val TertiaryLight = Color(0xFFE8F5E9)
val TertiaryContainer = Color(0xFFC8E6C9)
val OnTertiaryContainer = Color(0xFF0D4722)

val Neutral = Color(0xFF1C1C1C) // Deep Charcoal Text
val NeutralDark = Color(0xFF111111)
val NeutralGray = Color(0xFF686B78) // Muted Slate Text
val NeutralMuted = Color(0xFF93959F)
val NeutralBorder = Color(0xFFE8E8E8) // Clean Card Border
val NeutralBackground = Color(0xFFF4F5F7) // Light Clean Surface Background
val NeutralSurface = Color(0xFFFFFFFF) // Pure White Cards

// Core Brand Aliases
val ZaykaRed = Primary // #E23744
val ZaykaRedDark = PrimaryDark // #CB202D
val ZaykaOrange = Secondary // #FC8019
val ZaykaAmber = Color(0xFFFFB500) // Golden Amber
val ZaykaYellow = Color(0xFFFFC107)

// Food Category Badges & Indicators
val VegGreen = Tertiary // #25A75C
val NonVegRed = Primary // #E23744

// Surface and Backgrounds
val ZaykaBackgroundLight = NeutralBackground // #F4F5F7
val ZaykaSurfaceLight = NeutralSurface // #FFFFFF
val ZaykaSurfaceVariant = Color(0xFFF0F1F4)

val TextPrimaryLight = Neutral // #1C1C1C
val TextSecondaryLight = NeutralGray // #686B78
val TextMutedLight = NeutralMuted // #93959F
val BorderLight = NeutralBorder // #E8E8E8

// Dark Theme Colors
val ZaykaBackgroundDark = Color(0xFF121212)
val ZaykaSurfaceDark = Color(0xFF1E1E1E)
val ZaykaSurfaceVariantDark = Color(0xFF2C2C2C)
val TextPrimaryDark = Color(0xFFF5F5F5)
val TextSecondaryDark = Color(0xFFA0A0A0)
val BorderDark = Color(0xFF333333)

// Status colors
val StatusPreparing = Secondary // #FC8019
val StatusOutForDelivery = Color(0xFF0284C7)
val StatusDelivered = Tertiary // #25A75C
val StatusCancelled = Primary // #E23744
