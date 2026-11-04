package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AulaConstants
import com.example.data.BookingEntity
import com.example.data.BookingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class FormState(
    val editingId: Long? = null,
    val namaPeminjam: String = "",
    val namaError: String? = null,
    val tanggalEpochMs: Long = System.currentTimeMillis(),
    val tanggalFormatted: String = "",
    val tanggalDisplay: String = "",
    val tanggalError: String? = null,
    val durasiNilai: String = "4",
    val durasiSatuan: String = "Jam",
    val durasiError: String? = null,
    val noWhatsapp: String = "",
    val whatsappError: String? = null,
    val pilihanAula: String = AulaConstants.AULA_PLHUT,
    val catatan: String = "",
    val isSaving: Boolean = false
)

data class WeeklyStats(
    val totalSemua: Int = 0,
    val totalPlhut: Int = 0,
    val totalKemenag: Int = 0,
    val totalKecil: Int = 0
)

data class WeekRange(
    val startEpochMs: Long,
    val endEpochMs: Long,
    val label: String,
    val offset: Int
)

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookingRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = BookingRepository(database.bookingDao())
    }

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Form State
    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    // Week offset state (0 = current week, -1 = last week, +1 = next week)
    private val _weekOffset = MutableStateFlow(0)
    val weekOffset: StateFlow<Int> = _weekOffset.asStateFlow()

    // Filter by Aula on weekly screen ("Semua", "Aula PLHUT", "Aula Kemenag", "Aula Kecil")
    private val _selectedAulaFilter = MutableStateFlow("Semua")
    val selectedAulaFilter: StateFlow<String> = _selectedAulaFilter.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active bottom navigation tab (0 = Form Peminjaman, 1 = Rekap Mingguan, 2 = Riwayat Lengkap)
    private val _currentTab = MutableStateFlow(1) // Default to Rekap Mingguan to showcase stats immediately
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // UI Events (Snackbar messages)
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val indonesianDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    private val shortRangeFormat = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))

    init {
        // Initialize form with today's date
        val today = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val cleanMidnight = calendar.timeInMillis
        _formState.update {
            it.copy(
                tanggalEpochMs = cleanMidnight,
                tanggalFormatted = isoDateFormat.format(Date(cleanMidnight)),
                tanggalDisplay = indonesianDateFormat.format(Date(cleanMidnight))
            )
        }
    }

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    // --- FORM ACTIONS ---

    fun onNamaChanged(nama: String) {
        _formState.update {
            it.copy(
                namaPeminjam = nama,
                namaError = if (nama.trim().length < 3) "Nama peminjam minimal 3 karakter" else null
            )
        }
    }

    fun onTanggalSelected(epochMs: Long) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = epochMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val cleanMidnight = calendar.timeInMillis
        _formState.update {
            it.copy(
                tanggalEpochMs = cleanMidnight,
                tanggalFormatted = isoDateFormat.format(Date(cleanMidnight)),
                tanggalDisplay = indonesianDateFormat.format(Date(cleanMidnight)),
                tanggalError = null
            )
        }
    }

    fun onDurasiNilaiChanged(nilai: String) {
        val filtered = nilai.filter { it.isDigit() }
        _formState.update {
            it.copy(
                durasiNilai = filtered,
                durasiError = if (filtered.isBlank() || filtered.toIntOrNull() == 0) "Durasi pinjam harus diisi angka valid" else null
            )
        }
    }

    fun onDurasiSatuanChanged(satuan: String) {
        _formState.update { it.copy(durasiSatuan = satuan) }
    }

    fun onWhatsappChanged(wa: String) {
        val filtered = wa.filter { it.isDigit() || it == '+' }
        val isValid = filtered.length >= 9
        _formState.update {
            it.copy(
                noWhatsapp = filtered,
                whatsappError = if (!isValid) "Nomor WhatsApp minimal 9 digit angka" else null
            )
        }
    }

    fun onAulaSelected(aula: String) {
        if (AulaConstants.AULA_OPTIONS.contains(aula)) {
            _formState.update { it.copy(pilihanAula = aula) }
        }
    }

    fun onCatatanChanged(catatan: String) {
        _formState.update { it.copy(catatan = catatan) }
    }

    fun startEditBooking(booking: BookingEntity) {
        // Parse duration like "4 Jam" or "1 Hari"
        val parts = booking.durasiPinjam.split(" ")
        val nilai = parts.firstOrNull()?.filter { it.isDigit() } ?: "1"
        val satuan = if (parts.size > 1) parts[1] else "Jam"

        _formState.value = FormState(
            editingId = booking.id,
            namaPeminjam = booking.namaPeminjam,
            tanggalEpochMs = booking.tanggalEpochMs,
            tanggalFormatted = booking.tanggalPeminjaman,
            tanggalDisplay = indonesianDateFormat.format(Date(booking.tanggalEpochMs)),
            durasiNilai = nilai,
            durasiSatuan = satuan,
            noWhatsapp = booking.noWhatsapp,
            pilihanAula = booking.pilihanAula,
            catatan = booking.catatan
        )
        _currentTab.value = 0 // Switch to form tab
    }

    fun resetForm() {
        val today = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val cleanMidnight = calendar.timeInMillis
        _formState.value = FormState(
            editingId = null,
            namaPeminjam = "",
            tanggalEpochMs = cleanMidnight,
            tanggalFormatted = isoDateFormat.format(Date(cleanMidnight)),
            tanggalDisplay = indonesianDateFormat.format(Date(cleanMidnight)),
            durasiNilai = "4",
            durasiSatuan = "Jam",
            noWhatsapp = "",
            pilihanAula = AulaConstants.AULA_PLHUT,
            catatan = ""
        )
    }

    fun saveBooking() {
        val state = _formState.value

        // Validate all fields
        var hasError = false
        var updatedNamaError: String? = null
        var updatedDurasiError: String? = null
        var updatedWhatsappError: String? = null

        if (state.namaPeminjam.trim().length < 3) {
            updatedNamaError = "Nama peminjam minimal 3 karakter"
            hasError = true
        }

        val durasiNum = state.durasiNilai.toIntOrNull()
        if (durasiNum == null || durasiNum <= 0) {
            updatedDurasiError = "Durasi pinjam harus berupa angka positif"
            hasError = true
        }

        val waDigits = state.noWhatsapp.filter { it.isDigit() }
        if (waDigits.length < 9) {
            updatedWhatsappError = "Nomor WhatsApp minimal 9 digit angka"
            hasError = true
        }

        if (hasError) {
            _formState.update {
                it.copy(
                    namaError = updatedNamaError,
                    durasiError = updatedDurasiError,
                    whatsappError = updatedWhatsappError
                )
            }
            viewModelScope.launch {
                _uiEvent.emit("Harap perbaiki data yang belum lengkap.")
            }
            return
        }

        val durasiString = "${state.durasiNilai} ${state.durasiSatuan}"

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            try {
                if (state.editingId != null) {
                    val updatedBooking = BookingEntity(
                        id = state.editingId,
                        namaPeminjam = state.namaPeminjam.trim(),
                        tanggalPeminjaman = state.tanggalFormatted,
                        tanggalEpochMs = state.tanggalEpochMs,
                        durasiPinjam = durasiString,
                        noWhatsapp = state.noWhatsapp.trim(),
                        pilihanAula = state.pilihanAula,
                        catatan = state.catatan.trim()
                    )
                    repository.updateBooking(updatedBooking)
                    _uiEvent.emit("Peminjaman aula berhasil diperbarui!")
                } else {
                    val newBooking = BookingEntity(
                        namaPeminjam = state.namaPeminjam.trim(),
                        tanggalPeminjaman = state.tanggalFormatted,
                        tanggalEpochMs = state.tanggalEpochMs,
                        durasiPinjam = durasiString,
                        noWhatsapp = state.noWhatsapp.trim(),
                        pilihanAula = state.pilihanAula,
                        catatan = state.catatan.trim()
                    )
                    repository.insertBooking(newBooking)
                    _uiEvent.emit("Peminjaman aula berhasil disimpan!")
                }
                resetForm()
                _currentTab.value = 1 // Switch back to Rekap Mingguan
            } catch (e: Exception) {
                _uiEvent.emit("Gagal menyimpan: ${e.localizedMessage}")
            } finally {
                _formState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun deleteBooking(booking: BookingEntity) {
        viewModelScope.launch {
            try {
                repository.deleteBooking(booking)
                _uiEvent.emit("Data peminjaman telah dihapus.")
            } catch (e: Exception) {
                _uiEvent.emit("Gagal menghapus: ${e.localizedMessage}")
            }
        }
    }

    // --- REKAP MINGGUAN & STATISTIK ---

    fun nextWeek() {
        _weekOffset.update { it + 1 }
    }

    fun previousWeek() {
        _weekOffset.update { it - 1 }
    }

    fun resetToCurrentWeek() {
        _weekOffset.value = 0
    }

    fun onAulaFilterChanged(filter: String) {
        _selectedAulaFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * Calculates the start (Monday 00:00:00) and end (Sunday 23:59:59) for a given week offset.
     */
    fun getWeekRange(offset: Int): WeekRange {
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            add(Calendar.WEEK_OF_YEAR, offset)
        }
        val startMs = cal.timeInMillis

        val calEnd = Calendar.getInstance().apply {
            timeInMillis = startMs
            add(Calendar.DAY_OF_MONTH, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endMs = calEnd.timeInMillis

        val startLabel = shortRangeFormat.format(Date(startMs))
        val endLabel = shortRangeFormat.format(Date(endMs))

        val label = when (offset) {
            0 -> "Minggu Ini ($startLabel - $endLabel)"
            -1 -> "Minggu Lalu ($startLabel - $endLabel)"
            1 -> "Minggu Depan ($startLabel - $endLabel)"
            else -> "$startLabel - $endLabel"
        }

        return WeekRange(startMs, endMs, label, offset)
    }

    /**
     * Reactively computes weekly bookings based on the selected week offset and venue filter.
     */
    val currentWeekRange: StateFlow<WeekRange> = _weekOffset
        .combine(MutableStateFlow(Unit)) { offset, _ -> getWeekRange(offset) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = getWeekRange(0)
        )

    val weeklyBookings: StateFlow<List<BookingEntity>> = combine(
        allBookings,
        currentWeekRange,
        _selectedAulaFilter
    ) { bookings, weekRange, filter ->
        bookings.filter { item ->
            val inWeek = item.tanggalEpochMs in weekRange.startEpochMs..weekRange.endEpochMs
            val matchesAula = if (filter == "Semua") true else item.pilihanAula == filter
            inWeek && matchesAula
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Statistics for the currently selected week (independent of the venue filter dropdown).
     */
    val weeklyStats: StateFlow<WeeklyStats> = combine(
        allBookings,
        currentWeekRange
    ) { bookings, weekRange ->
        val weekItems = bookings.filter { it.tanggalEpochMs in weekRange.startEpochMs..weekRange.endEpochMs }
        val plhut = weekItems.count { it.pilihanAula == AulaConstants.AULA_PLHUT }
        val kemenag = weekItems.count { it.pilihanAula == AulaConstants.AULA_KEMENAG }
        val kecil = weekItems.count { it.pilihanAula == AulaConstants.AULA_KECIL }

        WeeklyStats(
            totalSemua = weekItems.size,
            totalPlhut = plhut,
            totalKemenag = kemenag,
            totalKecil = kecil
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeeklyStats()
    )

    // --- ACTIONS: WHATSAPP & SHARE ---

    fun openWhatsApp(context: Context, rawNumber: String, namaPeminjam: String, aula: String, tanggal: String) {
        var cleanNumber = rawNumber.filter { it.isDigit() }
        if (cleanNumber.startsWith("0")) {
            cleanNumber = "62" + cleanNumber.substring(1)
        }

        val message = "Halo Bapak/Ibu $namaPeminjam, konfirmasi peminjaman/sewa $aula Kemenag Kota Mojokerto untuk tanggal $tanggal melalui sistem Si-Selara. Ada hal atau fasilitas yang perlu dikoordinasikan?"
        val encodedMessage = Uri.encode(message)
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage")

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvent.emit("Tidak dapat membuka WhatsApp: Pastikan aplikasi WhatsApp terpasang.")
            }
        }
    }

    fun shareWeeklyReport(context: Context) {
        val weekRange = currentWeekRange.value
        val stats = weeklyStats.value
        val bookings = weeklyBookings.value

        val sb = StringBuilder()
        sb.append("📋 *SI-SELARA - REKAPITULASI SEWA & PEMINJAMAN AULA*\n")
        sb.append("🏛 *Kemenag Kota Mojokerto*\n")
        sb.append("🗓 Periode: ${weekRange.label}\n\n")
        sb.append("📊 *RINGKASAN STATISTIK:*\n")
        sb.append("• Total Peminjaman: ${stats.totalSemua} kegiatan\n")
        sb.append("• Aula PLHUT: ${stats.totalPlhut} kegiatan\n")
        sb.append("• Aula Kemenag: ${stats.totalKemenag} kegiatan\n")
        sb.append("• Aula Kecil: ${stats.totalKecil} kegiatan\n\n")
        sb.append("📝 *DAFTAR JADWAL KEGIATAN:*\n")

        if (bookings.isEmpty()) {
            sb.append("Belum ada agenda peminjaman untuk periode ini.\n")
        } else {
            bookings.forEachIndexed { index, item ->
                sb.append("${index + 1}. *${item.pilihanAula}*\n")
                sb.append("   • Peminjam: ${item.namaPeminjam}\n")
                sb.append("   • Tanggal: ${item.tanggalPeminjaman}\n")
                sb.append("   • Durasi: ${item.durasiPinjam}\n")
                sb.append("   • No. WA: ${item.noWhatsapp}\n")
                if (item.catatan.isNotBlank()) {
                    sb.append("   • Catatan: ${item.catatan}\n")
                }
                sb.append("\n")
            }
        }
        sb.append("Dicatat melalui Si-Selara (Aplikasi Sewa dan Rekap Aula) Kemenag Kota Mojokerto.")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Rekap Mingguan").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    }
}
