package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Moshi-compatible API Data Classes ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?
)

// --- Retrofit API Service ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

// --- Retrofit Client Singleton ---

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    /**
     * Call the Gemini API to get a real response.
     */
    suspend fun getChatResponse(prompt: String, chatHistory: List<com.example.data.ChatMessage>): String {
        val key = BuildConfig.GEMINI_API_KEY
        if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
            return "Hello! I am Yebuna Creative Assistant. It seems the Gemini API key is not configured in the Secrets Panel. To activate real AI responses, please configure GEMINI_API_KEY in AI Studio. In the meantime, how can I assist you with our creative coffee-fueled design collective?"
        }

        // Build structured dialog turns
        val turns = mutableListOf<Content>()
        
        // Add chat history context
        chatHistory.takeLast(10).forEach { msg ->
            turns.add(
                Content(
                    parts = listOf(Part(text = msg.text))
                )
            )
        }
        
        // Add current prompt
        turns.add(Content(parts = listOf(Part(text = prompt))))

        val systemPrompt = """
            You are "Yebuna Creative Assistant", the intelligent AI support widget for Yebuna.com (a premium creative freelance platform/agency powered by coffee-fueled creativity).
            Yebuna represents top-tier Ethiopian and East African freelancers specialized in Brand Identity, UI/UX Design, Web Development, 3D Illustration, and Motion Graphics.
            Our freelancers include:
            - Almaz Kebede (Brand Identity Expert)
            - Yonas Alemu (UI/UX Designer)
            - Selam Tekle (3D Artist & Illustrator)
            - Desta Mulu (Full-Stack Web Developer)
            - Ephraim Tsegaye (Motion Graphics Animator)

            Respond to users in a professional, warm, encouraging, and coffee-inspired tone. Include subtle references to Ethiopian coffee hospitality ("Buna") when appropriate, but keep it elegant and concise.
            Keep responses helpful, informative, under 3 paragraphs, and focused on helping the user navigate Yebuna, hire our freelancers, book a call, or answer creative project questions.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = turns,
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = apiService.generateContent(key, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I apologize, but I could not formulate a response at the moment. Can I help you book an appointment instead?"
        } catch (e: Exception) {
            "I encountered a slight connection error while brewing my response: ${e.localizedMessage}. Please try sending again, or contact our support team!"
        }
    }
}
