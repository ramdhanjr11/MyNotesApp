package com.muramsyah.mynotesapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.core.content.contentValuesOf
import com.muramsyah.mynotesapp.db.DatabaseContract.AUTHORITY
import com.muramsyah.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.muramsyah.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import com.muramsyah.mynotesapp.db.NoteHelper

class NoteProvider : ContentProvider() {

    companion object {
        private const val NOTE = 1
        private const val NOTE_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var noteHelper: NoteHelper

        init {
            // content://com.muramsyah.mynotesapp/note
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE)

            // content://com.muramsyah.mynotesapp/note/id
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", NOTE_ID)
        }
    }

    override fun onCreate(): Boolean {
        noteHelper = NoteHelper.getInstance(context as Context)
        noteHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (sUriMatcher.match(uri)) {
            NOTE -> noteHelper.queryAll()
            NOTE_ID -> noteHelper.queryById(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (NOTE) {
            sUriMatcher.match(uri) -> noteHelper.insert(values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return Uri.parse("$CONTENT_URI/$added")

    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val update: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.update(uri.lastPathSegment.toString(), values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return update
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return deleted

    }
}