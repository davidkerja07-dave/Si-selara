package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BookingViewModel
import com.example.ui.components.AllBookingsScreen
import com.example.ui.components.BookingFormScreen
import com.example.ui.components.WeeklySummaryScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: BookingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }

                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val formState by viewModel.formState.collectAsStateWithLifecycle()
                val weekRange by viewModel.currentWeekRange.collectAsStateWithLifecycle()
                val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()
                val weeklyBookings by viewModel.weeklyBookings.collectAsStateWithLifecycle()
                val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
                val selectedAulaFilter by viewModel.selectedAulaFilter.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

                // Handle Back Press when not on main Rekap tab
                BackHandler(enabled = currentTab != 1) {
                    if (currentTab == 0 && formState.editingId != null) {
                        viewModel.resetForm()
                    }
                    viewModel.selectTab(1)
                }

                // Collect Snackbar UI Events
                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collectLatest { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                androidx.compose.foundation.layout.Column {
                                    Text(
                                        text = "Si-Selara",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp
                                    )
                                    Text(
                                        text = "Sewa & Rekap Aula • Kemenag Kota Mojokerto",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                    )
                                }
                            },
                            actions = {
                                if (currentTab == 1) {
                                    IconButton(
                                        onClick = { viewModel.shareWeeklyReport(context) },
                                        modifier = Modifier.testTag("topbar_share_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Bagikan Rekap Mingguan"
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_navigation_bar"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { viewModel.selectTab(1) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Rekap Mingguan"
                                    )
                                },
                                label = { Text("Rekap Mingguan", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_rekap")
                            )

                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { viewModel.selectTab(0) },
                                icon = {
                                    Icon(
                                        imageVector = if (formState.editingId != null) Icons.Default.EditNote else Icons.Default.AddCircle,
                                        contentDescription = "Form Peminjaman"
                                    )
                                },
                                label = {
                                    Text(
                                        if (formState.editingId != null) "Edit Data" else "Pinjam Aula",
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_form")
                            )

                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { viewModel.selectTab(2) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "Semua Arsip"
                                    )
                                },
                                label = { Text("Semua Arsip", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_history")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 700.dp)
                        ) {
                            when (currentTab) {
                                0 -> BookingFormScreen(
                                    formState = formState,
                                    onNamaChanged = viewModel::onNamaChanged,
                                    onTanggalSelected = viewModel::onTanggalSelected,
                                    onDurasiNilaiChanged = viewModel::onDurasiNilaiChanged,
                                    onDurasiSatuanChanged = viewModel::onDurasiSatuanChanged,
                                    onWhatsappChanged = viewModel::onWhatsappChanged,
                                    onAulaSelected = viewModel::onAulaSelected,
                                    onCatatanChanged = viewModel::onCatatanChanged,
                                    onSaveClick = viewModel::saveBooking,
                                    onCancelEditClick = {
                                        viewModel.resetForm()
                                        viewModel.selectTab(1)
                                    }
                                )
                                1 -> WeeklySummaryScreen(
                                    weekRange = weekRange,
                                    weeklyStats = weeklyStats,
                                    bookings = weeklyBookings,
                                    selectedAulaFilter = selectedAulaFilter,
                                    onPreviousWeek = viewModel::previousWeek,
                                    onNextWeek = viewModel::nextWeek,
                                    onCurrentWeekReset = viewModel::resetToCurrentWeek,
                                    onAulaFilterSelected = viewModel::onAulaFilterChanged,
                                    onEditBooking = viewModel::startEditBooking,
                                    onDeleteBooking = viewModel::deleteBooking,
                                    onWhatsAppClick = { rawNumber, nama, aula, tanggal ->
                                        viewModel.openWhatsApp(context, rawNumber, nama, aula, tanggal)
                                    },
                                    onShareReport = { viewModel.shareWeeklyReport(context) },
                                    onAddNewBooking = {
                                        viewModel.resetForm()
                                        viewModel.selectTab(0)
                                    }
                                )
                                2 -> AllBookingsScreen(
                                    bookings = allBookings,
                                    searchQuery = searchQuery,
                                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                                    onEditBooking = viewModel::startEditBooking,
                                    onDeleteBooking = viewModel::deleteBooking,
                                    onWhatsAppClick = { rawNumber, nama, aula, tanggal ->
                                        viewModel.openWhatsApp(context, rawNumber, nama, aula, tanggal)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
