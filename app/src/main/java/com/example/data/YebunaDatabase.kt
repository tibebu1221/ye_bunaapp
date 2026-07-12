package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val freelancerName: String,
    val serviceName: String,
    val clientName: String,
    val clientEmail: String,
    val date: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "newsletter_signups")
data class NewsletterSignup(
    @PrimaryKey val email: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "contact_inquiries")
data class ContactInquiry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- DAOs ---

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)
}

@Dao
interface NewsletterDao {
    @Query("SELECT * FROM newsletter_signups ORDER BY timestamp DESC")
    fun getAllSignups(): Flow<List<NewsletterSignup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignup(signup: NewsletterSignup)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_inquiries ORDER BY timestamp DESC")
    fun getAllInquiries(): Flow<List<ContactInquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: ContactInquiry)
}

// --- Room Database ---

@Database(
    entities = [Booking::class, NewsletterSignup::class, ChatMessage::class, ContactInquiry::class],
    version = 1,
    exportSchema = false
)
abstract class YebunaDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun newsletterDao(): NewsletterDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: YebunaDatabase? = null

        fun getDatabase(context: Context): YebunaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YebunaDatabase::class.java,
                    "yebuna_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Repository Implementation ---

class YebunaRepository(private val db: YebunaDatabase) {
    val bookings: Flow<List<Booking>> = db.bookingDao().getAllBookings()
    val signups: Flow<List<NewsletterSignup>> = db.newsletterDao().getAllSignups()
    val chatMessages: Flow<List<ChatMessage>> = db.chatMessageDao().getAllMessages()
    val inquiries: Flow<List<ContactInquiry>> = db.contactDao().getAllInquiries()

    suspend fun addBooking(booking: Booking) {
        db.bookingDao().insertBooking(booking)
    }

    suspend fun addSignup(signup: NewsletterSignup) {
        db.newsletterDao().insertSignup(signup)
    }

    suspend fun addChatMessage(message: ChatMessage) {
        db.chatMessageDao().insertMessage(message)
    }

    suspend fun clearChat() {
        db.chatMessageDao().clearChat()
    }

    suspend fun addInquiry(inquiry: ContactInquiry) {
        db.contactDao().insertInquiry(inquiry)
    }
}
