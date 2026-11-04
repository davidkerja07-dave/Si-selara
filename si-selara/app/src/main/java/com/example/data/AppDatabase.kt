package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Main Room Database for the Peminjaman Aula application.
 */
@Database(entities = [BookingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "peminjaman_aula.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed initial sample data for demonstration of the weekly summary
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.bookingDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: BookingDao) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()

            // Monday of current week
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val mondayMs = cal.timeInMillis
            val mondayStr = sdf.format(cal.time)

            // Tuesday
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val tuesdayMs = cal.timeInMillis
            val tuesdayStr = sdf.format(cal.time)

            // Wednesday
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val wednesdayMs = cal.timeInMillis
            val wednesdayStr = sdf.format(cal.time)

            // Thursday
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val thursdayMs = cal.timeInMillis
            val thursdayStr = sdf.format(cal.time)

            // Friday
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val fridayMs = cal.timeInMillis
            val fridayStr = sdf.format(cal.time)

            val sampleBookings = listOf(
                BookingEntity(
                    namaPeminjam = "H. Ahmad Fauzi (KBIHU Al-Mabrur)",
                    tanggalPeminjaman = mondayStr,
                    tanggalEpochMs = mondayMs,
                    durasiPinjam = "4 Jam",
                    noWhatsapp = "081234567890",
                    pilihanAula = AulaConstants.AULA_PLHUT,
                    catatan = "Bimbingan Manasik Haji Kloter 1 dan pembagian perlengkapan jamaah"
                ),
                BookingEntity(
                    namaPeminjam = "Drs. H. Mulyadi (Pokjaluh Kemenag)",
                    tanggalPeminjaman = tuesdayStr,
                    tanggalEpochMs = tuesdayMs,
                    durasiPinjam = "1 Hari",
                    noWhatsapp = "082198765432",
                    pilihanAula = AulaConstants.AULA_KEMENAG,
                    catatan = "Rapat Koordinasi Evaluasi Penyuluh Agama Islam se-Kabupaten"
                ),
                BookingEntity(
                    namaPeminjam = "Siti Rahmawati, S.Pd (MGMP PAI)",
                    tanggalPeminjaman = wednesdayStr,
                    tanggalEpochMs = wednesdayMs,
                    durasiPinjam = "3 Jam",
                    noWhatsapp = "085712345678",
                    pilihanAula = AulaConstants.AULA_KECIL,
                    catatan = "Penyusunan Modul Ajar dan Kisi-kisi Asesmen Madrasah"
                ),
                BookingEntity(
                    namaPeminjam = "Dr. Ridwan Kamil (Dharma Wanita)",
                    tanggalPeminjaman = thursdayStr,
                    tanggalEpochMs = thursdayMs,
                    durasiPinjam = "5 Jam",
                    noWhatsapp = "081399887766",
                    pilihanAula = AulaConstants.AULA_PLHUT,
                    catatan = "Sosialisasi Kesehatan Jamaah Lansia dan Pemeriksaan Tekanan Darah"
                ),
                BookingEntity(
                    namaPeminjam = "Ust. Abdullah Sani (Baznas Daerah)",
                    tanggalPeminjaman = fridayStr,
                    tanggalEpochMs = fridayMs,
                    durasiPinjam = "2 Jam",
                    noWhatsapp = "087811223344",
                    pilihanAula = AulaConstants.AULA_KEMENAG,
                    catatan = "Penyaluran Bantuan Beasiswa Santri Berprestasi"
                )
            )

            dao.insertAll(sampleBookings)
        }
    }
}
