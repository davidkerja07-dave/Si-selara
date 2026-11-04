package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a hall booking record.
 */
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val namaPeminjam: String,
    val tanggalPeminjaman: String, // ISO date string: YYYY-MM-DD
    val tanggalEpochMs: Long,      // UTC/Local midnight timestamp for week/date filtering
    val durasiPinjam: String,      // Display duration, e.g., "4 Jam" or "1 Hari"
    val noWhatsapp: String,        // WhatsApp contact number, e.g., "081234567890"
    val pilihanAula: String,       // Strictly "Aula PLHUT", "Aula Kemenag", or "Aula Kecil"
    val catatan: String = "",      // Additional notes or purpose of event
    val createdAt: Long = System.currentTimeMillis()
)

object AulaConstants {
    const val AULA_PLHUT = "Aula PLHUT"
    const val AULA_KEMENAG = "Aula Kemenag"
    const val AULA_KECIL = "Aula Kecil"

    val AULA_OPTIONS = listOf(AULA_PLHUT, AULA_KEMENAG, AULA_KECIL)
}
