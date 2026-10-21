package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.Castle
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.Factory
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.Museum
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.Sailing
import androidx.compose.material.icons.rounded.TempleHindu
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.Tram
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.ui.graphics.vector.ImageVector

/** A picture for each city or company (its landmark or what it's known for), by board tile. */
fun cityArtIcon(tile: Int): ImageVector = when (tile) {
    1 -> Icons.Rounded.Apartment // Mumbai skyline
    2 -> Icons.Rounded.WaterDrop // Water Works
    3 -> Icons.Rounded.Train // Railways
    4 -> Icons.Rounded.Factory // Ahmedabad textile mills
    6 -> Icons.Rounded.LocalDining // Indore street food
    8 -> Icons.Rounded.Castle // Jaipur forts
    10 -> Icons.Rounded.AccountBalance // Delhi, India Gate / Parliament
    11 -> Icons.Rounded.Park // Chandigarh Rock Garden
    12 -> Icons.Rounded.ElectricBolt // Electric Company
    13 -> Icons.Rounded.DirectionsBus // B.E.S.T. buses
    14 -> Icons.Rounded.Terrain // Shimla hills
    15 -> Icons.Rounded.TempleHindu // Amritsar Golden Temple
    17 -> Icons.Rounded.Sailing // Srinagar houseboats
    19 -> Icons.Rounded.Mosque // Agra, Taj Mahal
    21 -> Icons.Rounded.Factory // Kanpur mills
    22 -> Icons.Rounded.Museum // Patna heritage
    23 -> Icons.Rounded.LocalCafe // Darjeeling tea
    24 -> Icons.Rounded.Flight // Air India
    25 -> Icons.Rounded.Tram // Kolkata trams
    26 -> Icons.Rounded.Diamond // Hyderabad, Golconda diamonds
    28 -> Icons.Rounded.Waves // Chennai, Marina beach
    30 -> Icons.Rounded.Computer // Bengaluru tech
    32 -> Icons.Rounded.Forest // Ooty forests
    33 -> Icons.Rounded.Anchor // Kochi port
    34 -> Icons.Rounded.DirectionsBoat // Motorboat
    35 -> Icons.Rounded.BeachAccess // Goa beaches
    else -> Icons.Rounded.LocationCity
}
