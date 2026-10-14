package com.example.padelconnect.data.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class UserPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("padel_connect_prefs", Context.MODE_PRIVATE)

    fun getUserId(): String {
        var id = prefs.getString("key_user_id", null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            prefs.edit { putString("key_user_id", id) }
        }
        return id
    }

    fun getCreatorName(): String? {
        return prefs.getString("key_creator_name", null)
    }

    fun saveCreatorName(name: String) {
        prefs.edit { putString("key_creator_name", name) }
    }
}