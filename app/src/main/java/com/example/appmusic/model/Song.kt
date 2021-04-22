package com.example.appmusic.model

import java.io.Serializable

class Song(
    var title: String? = null,
    val subTitle: String? = null,
    val path: String? = null,
    val image: String?= null
) : Serializable