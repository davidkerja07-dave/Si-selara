package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AulaConstants
import com.example.data.BookingEntity
import com.example.ui.WeekRange
import com.example.ui.WeeklyStats
import com.example.ui.theme.AulaKecilBg
import com.example.ui.theme.AulaKecilColor
import com.example.ui.theme.AulaKemenagBg
import com.example.ui.theme.AulaKemenagColor
import com.example.ui.theme.AulaPlhutBg
import com.example.ui.theme.AulaPlhutColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklySummaryScreen(
    weekRange: WeekRange,
    weeklyStats: WeeklyStats,
    bookings: List<BookingEntity>,
    selectedAulaFilter: String,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCurrentWeekReset: () -> Unit,
    onAulaFilterSelected: (String) -> Unit,
    onEditBooking: (BookingEntity) -> Unit,
    onDeleteBooking: (BookingEntity) -> Unit,
    onWhatsAppClick: (rawNumber: String, nama: String, aula: String, tanggal: String) -> Unit,
    onShareReport: () -> Unit,
    onAddNewBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("weekly_summary_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Week Navigation Bar
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onPreviousWeek,
                            modifier = Modifier.testTag("prev_week_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Minggu Sebelumnya",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Si-Selara • Rekap Mingguan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = weekRange.label,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = onNextWeek,
                            modifier = Modifier.testTag("next_week_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Minggu Berikutnya",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Reset to current week & Share button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (weekRange.offset != 0) {
                            TextButton(
                                onClick = onCurrentWeekReset,
                                modifier = Modifier.testTag("today_reset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kembali ke Minggu Ini", fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                text = "Periode Aktif",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        FilledTonalButton(
                            onClick = onShareReport,
                            modifier = Modifier.testTag("share_report_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bagikan Rekap", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 2. Summary Stats Cards (Total, Aula PLHUT, Aula Kemenag, Aula Kecil)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Statistik Peminjaman Minggu Ini",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total Bookings Card
                    StatItemCard(
                        title = "Total Agenda",
                        count = weeklyStats.totalSemua,
                        icon = Icons.Default.CalendarMonth,
                        bgColor = MaterialTheme.colorScheme.primaryContainer,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )

                    // Aula PLHUT Stat Card
                    StatItemCard(
                        title = "Aula PLHUT",
                        count = weeklyStats.totalPlhut,
                        icon = Icons.Default.Mosque,
                        bgColor = AulaPlhutBg,
                        textColor = AulaPlhutColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Aula Kemenag Stat Card
                    StatItemCard(
                        title = "Aula Kemenag",
                        count = weeklyStats.totalKemenag,
                        icon = Icons.Default.Apartment,
                        bgColor = AulaKemenagBg,
                        textColor = AulaKemenagColor,
                        modifier = Modifier.weight(1f)
                    )

                    // Aula Kecil Stat Card
                    StatItemCard(
                        title = "Aula Kecil",
                        count = weeklyStats.totalKecil,
                        icon = Icons.Default.MeetingRoom,
                        bgColor = AulaKecilBg,
                        textColor = AulaKecilColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Filter by Aula Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Filter Berdasarkan Aula:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterOptions = listOf("Semua") + AulaConstants.AULA_OPTIONS
                    items(filterOptions) { filter ->
                        FilterChip(
                            selected = selectedAulaFilter == filter,
                            onClick = { onAulaFilterSelected(filter) },
                            label = { Text(filter) },
                            leadingIcon = if (selectedAulaFilter == filter) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("filter_chip_$filter")
                        )
                    }
                }
            }
        }

        // 4. Bookings Header & Count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Jadwal Peminjaman (${bookings.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = onAddNewBooking,
                    modifier = Modifier.testTag("add_from_rekap_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pinjam Aula", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5. Bookings List or Empty State
        if (bookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak Ada Peminjaman Aula",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedAulaFilter != "Semua")
                                "Tidak ada kegiatan di $selectedAulaFilter untuk periode ini."
                            else
                                "Belum ada jadwal peminjaman gedung aula pada periode minggu ini.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddNewBooking,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("empty_add_booking_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buat Peminjaman Sekarang")
                        }
                    }
                }
            }
        } else {
            items(items = bookings, key = { it.id }) { booking ->
                BookingCard(
                    booking = booking,
                    onEditClick = onEditBooking,
                    onDeleteClick = onDeleteBooking,
                    onWhatsAppClick = onWhatsAppClick
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatItemCard(
    title: String,
    count: Int,
    icon: ImageVector,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.9f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "kegiatan",
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}
