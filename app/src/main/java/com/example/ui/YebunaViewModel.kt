package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Booking
import com.example.data.ChatMessage
import com.example.data.ContactInquiry
import com.example.data.NewsletterSignup
import com.example.data.YebunaDatabase
import com.example.data.YebunaRepository
import com.example.data.api.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- Project Model ---
data class Project(
    val id: Int,
    val title: String,
    val category: String,
    val freelancerName: String,
    val description: String,
    val longDescription: String,
    val rating: Double,
    val client: String,
    val year: String,
    val tools: List<String>
)

// --- Testimonial Model ---
data class Testimonial(
    val id: Int,
    val name: String,
    val role: String,
    val company: String,
    val text: String,
    val rating: Int
)

class YebunaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = YebunaDatabase.getDatabase(application)
    private val repository = YebunaRepository(db)

    // --- Preferences State ---
    private val _isDarkMode = MutableStateFlow(true) // Default to elegant dark theme
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // --- Search & Filter State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // --- Projects Static Data ---
    val allProjects = listOf(
        Project(
            id = 1,
            title = "Bunna Brew Branding",
            category = "Brand Identity",
            freelancerName = "Almaz Kebede",
            description = "Organic visual identity for a heritage coffee roaster, featuring hand-carved textures and modern typography.",
            longDescription = "An exhaustive rebrand tracing the organic, highland origin of heirloom Ethiopian coffee. We constructed custom woodblock-print motifs, typography inspired by Ge'ez script aesthetics, and zero-plastic earth packaging to secure Bunna Brew's global footprint.",
            rating = 4.9,
            client = "Bunna Brew Roasters Ltd.",
            year = "2026",
            tools = listOf("Adobe Illustrator", "Figma", "Packaging Mockups", "Hand-drawn Linocut")
        ),
        Project(
            id = 2,
            title = "Kolo Logistics App",
            category = "UI/UX Design",
            freelancerName = "Yonas Alemu",
            description = "Sleek dark-mode mobile application for roasted grain warehousing and supply-chain logistics.",
            longDescription = "A complete UX transformation for bulk agricultural traders. Our design prioritizes real-time offline synchronization for rural drivers, a high-contrast telemetry dashboard, and a frictionless single-tap freight billing process.",
            rating = 4.8,
            client = "Kolo Agriculture Group",
            year = "2025",
            tools = listOf("Figma", "Jetpack Compose Prototype", "After Effects", "User Testing")
        ),
        Project(
            id = 3,
            title = "Axum Monumental 3D",
            category = "3D Illustration",
            freelancerName = "Selam Tekle",
            description = "Stunning low-poly 3D models and interactive WebGL assets of historic monuments for museums.",
            longDescription = "An immersive archaeological preservation project. Selam constructed hyper-accurate, high-fidelity 3D assets of ancient obelisks and subterranean ruins, compatible with modern spatial computing and mobile AR engines.",
            rating = 5.0,
            client = "Heritage Preservation Association",
            year = "2026",
            tools = listOf("Blender", "ZBrush", "Substance Painter", "Unity 3D")
        ),
        Project(
            id = 4,
            title = "Yebuna Hub Platform",
            category = "Web Development",
            freelancerName = "Desta Mulu",
            description = "Next.js talent hub optimized with local caching and streaming response for near-zero load times.",
            longDescription = "Our flagship global marketplace portal. Desta engineered a highly custom SSR portfolio generator, yielding a perfect 100/100 Lighthouse performance score through server-side edge rendering and smart asset compression.",
            rating = 4.9,
            client = "Yebuna Collective",
            year = "2026",
            tools = listOf("React", "Next.js", "Tailwind CSS", "Vercel Edge", "SQLite")
        ),
        Project(
            id = 5,
            title = "Gojo Eco-Architects",
            category = "Web Development",
            freelancerName = "Desta Mulu",
            description = "Minimalist and sustainable architectural showcase built for zero-footprint rendering.",
            longDescription = "A masterclass in modern digital minimalism. We crafted a lightweight green-tech portfolio using pure static-site delivery, displaying intricate architectural blue-prints with minimal network payloads.",
            rating = 4.7,
            client = "Gojo Architects Partners",
            year = "2025",
            tools = listOf("HTML5/CSS3", "Vanilla JS", "Service Workers", "SVG Animations")
        ),
        Project(
            id = 6,
            title = "Buna Ritual Motion Reel",
            category = "Motion Graphics",
            freelancerName = "Ephraim Tsegaye",
            description = "Atmospheric motion graphics loop tracing the history of coffee roasting ceremonies.",
            longDescription = "A visual masterpiece exploring the cultural sacredness of the Buna ritual. Blending organic watercolor textures with dynamic vector physics, the animation evokes warmth, community, and caffeine-fueled genius.",
            rating = 4.9,
            client = "Ethiopian Coffee Authority",
            year = "2026",
            tools = listOf("Adobe After Effects", "Cinema 4D", "Premiere Pro", "Lottie")
        )
    )

    // --- Reactive Filtered Projects Flow ---
    val filteredProjects: StateFlow<List<Project>> = combine(
        _searchQuery, _selectedCategory
    ) { query, category ->
        allProjects.filter { project ->
            val matchesCategory = (category == "All") || (project.category == category)
            val matchesSearch = project.title.contains(query, ignoreCase = true) ||
                    project.description.contains(query, ignoreCase = true) ||
                    project.freelancerName.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = allProjects
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    // --- Testimonials ---
    val testimonials = listOf(
        Testimonial(
            id = 1,
            name = "Martha Tefera",
            role = "CEO",
            company = "Bunna Brew Roasters",
            text = "The rebranding of Bunna Brew was spectacular. Almaz captured our cultural soul while making it look globally competitive. Exceptional craftsmanship!",
            rating = 5
        ),
        Testimonial(
            id = 2,
            name = "Dawit Hailu",
            role = "Operations Director",
            company = "Kolo Logistics",
            text = "Yonas designed a flawless mobile logistics portal. Our driver coordination and grain deliveries improved by 140% in just one month.",
            rating = 5
        ),
        Testimonial(
            id = 3,
            name = "Prof. Girma Assefa",
            role = "Chief Conservator",
            company = "East African Heritage",
            text = "Selam's 3D historical models are breathtaking. The detail on the obelisks has allowed us to preserve these designs forever in spatial virtual reality.",
            rating = 5
        ),
        Testimonial(
            id = 4,
            name = "Sarah Jenkins",
            role = "VP of Design",
            company = "Global Talents Corp",
            text = "The website is lightning fast and beautifully responsive. Yebuna's engineering talent has raised our agency standards tenfold.",
            rating = 4
        )
    )

    // --- Room Database Backed Lists ---
    val bookings: StateFlow<List<Booking>> = repository.bookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val inquiries: StateFlow<List<ContactInquiry>> = repository.inquiries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val signups: StateFlow<List<NewsletterSignup>> = repository.signups.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Form UI States & Validations ---

    // 1. Booking Form State
    val _bookingFreelancer = MutableStateFlow("Almaz Kebede")
    val bookingFreelancer = _bookingFreelancer.asStateFlow()

    val _bookingService = MutableStateFlow("Brand Identity Design")
    val bookingService = _bookingService.asStateFlow()

    val _bookingClientName = MutableStateFlow("")
    val bookingClientName = _bookingClientName.asStateFlow()

    val _bookingClientEmail = MutableStateFlow("")
    val bookingClientEmail = _bookingClientEmail.asStateFlow()

    val _bookingDate = MutableStateFlow("")
    val bookingDate = _bookingDate.asStateFlow()

    val _bookingNotes = MutableStateFlow("")
    val bookingNotes = _bookingNotes.asStateFlow()

    // 2. Contact Form State
    val _contactName = MutableStateFlow("")
    val contactName = _contactName.asStateFlow()

    val _contactEmail = MutableStateFlow("")
    val contactEmail = _contactEmail.asStateFlow()

    val _contactSubject = MutableStateFlow("")
    val contactSubject = _contactSubject.asStateFlow()

    val _contactMessage = MutableStateFlow("")
    val contactMessage = _contactMessage.asStateFlow()

    // 3. Newsletter Form State
    val _newsletterEmail = MutableStateFlow("")
    val newsletterEmail = _newsletterEmail.asStateFlow()

    // --- Toast / Feedback alerts ---
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage = _feedbackMessage.asStateFlow()

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    // --- Booking submission ---
    fun setBookingField(field: String, value: String) {
        when (field) {
            "freelancer" -> _bookingFreelancer.value = value
            "service" -> _bookingService.value = value
            "name" -> _bookingClientName.value = value
            "email" -> _bookingClientEmail.value = value
            "date" -> _bookingDate.value = value
            "notes" -> _bookingNotes.value = value
        }
    }

    fun submitBooking(): Boolean {
        val name = _bookingClientName.value.trim()
        val email = _bookingClientEmail.value.trim()
        val date = _bookingDate.value.trim()
        val notes = _bookingNotes.value.trim()

        if (name.isEmpty() || email.isEmpty() || date.isEmpty() || notes.isEmpty()) {
            _feedbackMessage.value = "Please fill in all booking fields."
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _feedbackMessage.value = "Please enter a valid email address."
            return false
        }

        viewModelScope.launch {
            val booking = Booking(
                freelancerName = _bookingFreelancer.value,
                serviceName = _bookingService.value,
                clientName = name,
                clientEmail = email,
                date = date,
                notes = notes
            )
            repository.addBooking(booking)
            _feedbackMessage.value = "Success! Your booking request has been locked with ${_bookingFreelancer.value}."
            
            // Reset input fields
            _bookingClientName.value = ""
            _bookingClientEmail.value = ""
            _bookingDate.value = ""
            _bookingNotes.value = ""
        }
        return true
    }

    // --- Contact submission ---
    fun setContactField(field: String, value: String) {
        when (field) {
            "name" -> _contactName.value = value
            "email" -> _contactEmail.value = value
            "subject" -> _contactSubject.value = value
            "message" -> _contactMessage.value = value
        }
    }

    fun submitContactInquiry(): Boolean {
        val name = _contactName.value.trim()
        val email = _contactEmail.value.trim()
        val subject = _contactSubject.value.trim()
        val msg = _contactMessage.value.trim()

        if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || msg.isEmpty()) {
            _feedbackMessage.value = "All fields are required for general inquiry."
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _feedbackMessage.value = "Please enter a valid email address."
            return false
        }

        viewModelScope.launch {
            val inquiry = ContactInquiry(
                name = name,
                email = email,
                subject = subject,
                message = msg
            )
            repository.addInquiry(inquiry)
            _feedbackMessage.value = "Thank you! Your inquiry about '$subject' has been submitted."
            
            // Reset fields
            _contactName.value = ""
            _contactEmail.value = ""
            _contactSubject.value = ""
            _contactMessage.value = ""
        }
        return true
    }

    // --- Newsletter submission ---
    fun setNewsletterEmail(email: String) {
        _newsletterEmail.value = email
    }

    fun submitNewsletter(): Boolean {
        val email = _newsletterEmail.value.trim()
        if (email.isEmpty()) {
            _feedbackMessage.value = "Please enter an email address."
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _feedbackMessage.value = "Please enter a valid email address."
            return false
        }

        viewModelScope.launch {
            val signup = NewsletterSignup(email = email)
            repository.addSignup(signup)
            _feedbackMessage.value = "Incredible! You have subscribed to the Yebuna Newsletter."
            _newsletterEmail.value = ""
        }
        return true
    }

    // --- Live Chat State & Logic ---
    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    fun sendChatMessage() {
        val text = _chatInput.value.trim()
        if (text.isEmpty()) return

        _chatInput.value = ""
        _isChatLoading.value = true

        viewModelScope.launch {
            // Save User message
            val userMsg = ChatMessage(sender = "user", text = text)
            repository.addChatMessage(userMsg)

            // Fetch live chat messages for conversation history context
            val currentHistory = db.chatMessageDao().getAllMessages()
            // Wait, since we are inside a flow, we can query it directly or extract it from database once
            val historyList = db.chatMessageDao().getAllMessages() // Wait, DAO returns flow. Let's write a simple query or get history from Flow value.
            // Let's implement a direct list query if we need to, or just fetch the current list in flow:
            val history = chatMessages.value

            // Call Gemini
            val response = GeminiClient.getChatResponse(text, history)

            // Save AI message
            val aiMsg = ChatMessage(sender = "ai", text = response)
            repository.addChatMessage(aiMsg)

            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
            // Add a warm welcome message
            val welcome = ChatMessage(
                sender = "ai",
                text = "Welcome to Yebuna Creative Collective real-time support! I am your AI assistant, fueled by roasted beans and custom code. How can I help you explore portfolios or hire a designer today?"
            )
            repository.addChatMessage(welcome)
        }
    }

    init {
        // Seed chat message on launch if empty
        viewModelScope.launch {
            db.chatMessageDao().getAllMessages().collect { list ->
                if (list.isEmpty()) {
                    val welcome = ChatMessage(
                        sender = "ai",
                        text = "Welcome to Yebuna Creative Collective real-time support! I am your AI assistant, fueled by roasted beans and custom code. How can I help you explore portfolios or hire a designer today?"
                    )
                    repository.addChatMessage(welcome)
                }
            }
        }
    }
}
