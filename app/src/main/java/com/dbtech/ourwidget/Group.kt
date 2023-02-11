package com.dbtech.ourwidget

import android.content.Context

data class Group(
    var id: String,
    var name: String,
    var onWidgetContainerClicked : (input: String) -> Unit
    )