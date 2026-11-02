package com.runii.vegs

import android.app.Application
import com.runii.vegs.data.AppDatabase
import com.runii.vegs.data.SettingsRepository

class RuniiApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        settingsRepository = SettingsRepository(this)
    }
}
