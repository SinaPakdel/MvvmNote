package com.sina.mvvm.utils

import androidx.appcompat.widget.SearchView
import com.google.android.material.button.MaterialButton
import com.sina.mvvm.R
import com.sina.mvvm.R.drawable.ic_favorite_empty
import com.sina.mvvm.R.drawable.ic_favorite_fill

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener.invoke(newText.orEmpty())
            return true
        }
    })
}

fun MaterialButton.toggleFavoriteIcon(
    initialValue: Boolean = false,
    onFavoriteClick: (Boolean) -> Unit
): Boolean {

    var isFavorite = initialValue
    tag = isFavorite

    setIconResource(if (isFavorite) ic_favorite_fill else ic_favorite_empty)

    setOnClickListener {
        isFavorite = !isFavorite
        tag = isFavorite
        setIconResource(if (isFavorite) ic_favorite_fill else ic_favorite_empty)
        onFavoriteClick.invoke(isFavorite)
    }
    return isFavorite
}