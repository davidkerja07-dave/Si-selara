package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository providing a clean API for data access to the ViewModel.
 */
class BookingRepository(private val bookingDao: BookingDao) {

    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    fun getBookingsForDateRange(startEpochMs: Long, endEpochMs: Long): Flow<List<BookingEntity>> {
        return bookingDao.getBookingsBetweenDates(startEpochMs, endEpochMs)
    }

    fun getBookingsByAula(aula: String): Flow<List<BookingEntity>> {
        return bookingDao.getBookingsByAula(aula)
    }

    fun searchBookings(query: String): Flow<List<BookingEntity>> {
        return bookingDao.searchBookings(query)
    }

    fun getBookingById(id: Long): Flow<BookingEntity?> {
        return bookingDao.getBookingById(id)
    }

    suspend fun insertBooking(booking: BookingEntity): Long {
        return bookingDao.insertBooking(booking)
    }

    suspend fun updateBooking(booking: BookingEntity) {
        bookingDao.updateBooking(booking)
    }

    suspend fun deleteBooking(booking: BookingEntity) {
        bookingDao.deleteBooking(booking)
    }

    suspend fun deleteBookingById(id: Long) {
        bookingDao.deleteBookingById(id)
    }
}
