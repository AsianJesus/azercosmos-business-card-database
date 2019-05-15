package com.ohmycthulhu.businesscarddatabase.utils.recognizer

// import com.googlecode.tesseract.android
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI

class Recognizer (private val LANG_PATH: String = "${Environment.getExternalStorageDirectory()}/BCD/assets") {
    private var baseApi: TessBaseAPI = TessBaseAPI()
    private var LANG = "eng"
    private var results: Map<RecognizePatterns, String> = mutableMapOf()

    init {
        initialize()
    }

    fun setLanguage (lang: String) {
        if (LANG != lang) {
            LANG = lang

        }
    }

    private fun initialize () {
        baseApi.setDebug(true)
        baseApi.init(LANG_PATH, LANG)
    }

    fun recognize(image: Bitmap): Map<RecognizePatterns, String> {
        baseApi.setImage(image)
        val text = baseApi.utF8Text
        val matches: MutableMap<RecognizePatterns, String> = mutableMapOf()
        Log.d("recognized", text)
        var hasMatched = false
        for (line in text.split('\n')) {
            hasMatched = false
            for (pattern in RecognizePatterns.values()) {
                if (!(matches.containsKey(pattern) || hasMatched)) {
                    val match = pattern.pattern.matcher(line)
                    if (match.matches()) {
                        hasMatched = true
                        matches[pattern] = line
                    }
                }
            }
        }
        results = matches
        Log.d("match_results", "We have following matches: ")
        for (key in matches.keys) {
            Log.d("match_result", "$key - ${matches[key]}")
        }
        return matches
    }

    fun getResults (): Map<RecognizePatterns, String> {
        return results
    }

}