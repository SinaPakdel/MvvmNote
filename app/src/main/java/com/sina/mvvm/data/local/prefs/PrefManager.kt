package com.sina.mvvm.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sina.mvvm.data.local.model.helper.SortBy
import com.sina.mvvm.data.local.prefs.PrefManager.PreferencesKey.favorite
import com.sina.mvvm.data.local.prefs.PrefManager.PreferencesKey.sort
import com.sina.mvvm.utils.Constants.FAVORITE
import com.sina.mvvm.utils.Constants.PREFERENCES_FAVORITE_KEY
import com.sina.mvvm.utils.Constants.PREFERENCES_SORT_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrefManager @Inject constructor(private val appDataStore: DataStore<Preferences>) {

    private object PreferencesKey {
        val sort = stringPreferencesKey(PREFERENCES_SORT_KEY)
        val favorite = booleanPreferencesKey(PREFERENCES_FAVORITE_KEY)
    }

    suspend fun saveSortOrder(sortBy: SortBy) = appDataStore.edit { preferences ->
        preferences[sort] = sortBy.name
    }

    suspend fun saveFavorite(favoriteState: Boolean) = appDataStore.edit { preferences ->
        preferences[favorite] = favoriteState
    }

    val readFavorite: Flow<Boolean> = appDataStore.data.catch { exception ->
        if (exception is java.io.IOException) emit(emptyPreferences()) else throw exception
    }.map { preferences -> preferences[favorite] ?: FAVORITE }

    val readSearchNote = appDataStore.data.catch { exception ->
        if (exception is IOException) emit(emptyPreferences()) else throw exception
    }.map { preferences ->
        FilterPrefs(
            SortBy.valueOf(preferences[sort] ?: SortBy.NAME.name), preferences[favorite] ?: FAVORITE
        )
    }
}

data class FilterPrefs(val sortBy: SortBy, val isFavorite: Boolean)