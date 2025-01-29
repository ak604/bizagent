import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Base64

class WebClient {

    private val client = OkHttpClient()

    fun executePostRequest(file :File) {
        val encodedFile = encodeFileToBase64(file)
        encodedFile?.let { sendPostRequest(it) }
    }

    // Method to encode file to Base64
    private fun encodeFileToBase64(file: File): String? {
        return try {
            val fileInputStream = FileInputStream(file)
            val fileBytes = ByteArray(file.length().toInt())
            fileInputStream.read(fileBytes)
            Base64.getEncoder().encodeToString(fileBytes)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Method to send POST request with the Base64 encoded file
    private fun sendPostRequest(encodedFile: String) {

        val requestBody = encodedFile.toRequestBody("text/plain".toMediaType())

        // Create the POST request
        val request: Request = Request.Builder()
            .url("https://4mwwu4teqk.execute-api.ap-south-1.amazonaws.com/dev/uploadAudio")  // Replace with your server URL
            .post(requestBody)
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("Response: ${response.body?.string()}")
                } else {
                    println("Request failed with code: ${response.code}")
                }
            }
        })
    }
}
