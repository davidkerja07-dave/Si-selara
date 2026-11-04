package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for BookingEntity operations.
 */
@Dao
interface BookingDao {

    @Query("SELECT * FROM bookings ORDER BY tanggalEpochMs ASC, id ASC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE tanggalEpochMs >= :startEpochMs AND tanggalEpochMs <= :endEpochMs ORDER BY tanggalEpochMs ASC, id ASC")
    fun getBookingsBetweenDates(startEpochMs: Long, endEpochMs: Long): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE pilihanAula = :aula ORDER BY tanggalEpochMs ASC")
    fun getBookingsByAula(aula: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<BookingEntity>)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Long)

    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun getCount(): Int

    @Query("SELECT * FROM bookings WHERE namaPeminjam LIKE '%' || :query || '%' OR catatan LIKE '%' || :query || '%' OR noWhatsapp LIKE '%' || :query || '%' ORDER BY tanggalEpochMs DESC")
    fun searchBookings(query: String): Flow<List<BookingEntity>>
}
