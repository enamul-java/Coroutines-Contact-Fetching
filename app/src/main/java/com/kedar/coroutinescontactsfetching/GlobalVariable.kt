package com.kedar.coroutinescontactsfetching

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class GlobalVariable : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    var name: String? = ""


}