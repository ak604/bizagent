package com.example.bizagent

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.IOException

object FileUtils {
    fun getLastModifiedFile(directory: File): File? {
        // Ensure the directory exists and is a directory
        if (!directory.exists() || !directory.isDirectory) {
            Log.e("FileSelection", "The provided path is not a valid directory.")
            return null
        }

        // List all files in the directory
        val files = directory.listFiles()

        // If no files exist, return null
        if (files.isNullOrEmpty()) {
            Log.e("FileSelection", "No files found in the directory.")
            return null
        }

        // Sort files by last modified timestamp (descending order)
        val lastModifiedFile = files.maxByOrNull { it.lastModified() }

        // Return the file with the most recent modification time
        return lastModifiedFile
    }

    fun copyM4aFileUsingMediaStore(context: Context, sourceFile: File, destFileName: String) {
        val resolver = context.contentResolver

        // ✅ 1. Check if the source file exists
        if (!sourceFile.exists()) {
            Log.e("FileCopy", "Source file does not exist: ${sourceFile.absolutePath}")
            return
        }

        // ✅ 2. Prepare content values for MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, destFileName)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4") // Correct MIME type for .m4a files
            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC) // Save to Music folder
        }

        // ✅ 3. Insert into MediaStore
        val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri == null) {
            Log.e("FileCopy", "Failed to create destination file in MediaStore")
            return
        }

        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.i("FileCopy", "File copied successfully to Music directory: $destFileName")
        } catch (e: IOException) {
            Log.e("FileCopy", "Error copying file: ${e.message}")
        }
    }
}