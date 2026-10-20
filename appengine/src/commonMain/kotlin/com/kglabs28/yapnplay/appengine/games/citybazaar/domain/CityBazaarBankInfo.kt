package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

/** The bank's money now, and all it has received from and paid to players. */
data class CityBazaarBankInfo(val cash: Long, val received: Long, val paid: Long)
