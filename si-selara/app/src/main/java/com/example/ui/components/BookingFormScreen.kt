package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AulaConstants
import com.example.ui.FormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    formState: FormState,
    onNamaChanged: (String) -> Unit,
    onTanggalSelected: (Long) -> Unit,
    onDurasiNilaiChanged: (String) -> Unit,
    onDurasiSatuanChanged: (String) -> Unit,
    onWhatsappChanged: (String) -> Unit,
    onAulaSelected: (String) -> Unit,
    onCatatanChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.tanggalEpochMs
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onTanggalSelected(it)
                        }
                        showDatePicker = false
                    },
                    modifier = Modifier.testTag("date_picker_confirm")
                ) {
                    Text("Pilih Tanggal", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (formState.editingId != null) Icons.Default.EditCalendar else Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (formState.editingId != null) "Edit Sewa & Peminjaman Aula" else "Form Sewa & Peminjaman Aula",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (formState.editingId != null)
                            "Perbarui rincian reservasi Si-Selara Kemenag Kota Mojokerto"
                        else
                            "Si-Selara • Kemenag Kota Mojokerto (PLHUT, Kemenag, Aula Kecil)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                if (formState.editingId != null) {
                    IconButton(
                        onClick = onCancelEditClick,
                        modifier = Modifier.testTag("cancel_edit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Batal Edit",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // 1. Nama Peminjam
        Column {
            OutlinedTextField(
                value = formState.namaPeminjam,
                onValueChange = onNamaChanged,
                label = { Text("Nama Peminjam / Instansi *") },
                placeholder = { Text("Contoh: H. Ahmad Fauzi / Kemenag Kota") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                isError = formState.namaError != null,
                supportingText = {
                    formState.namaError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    } ?: Text("Nama penanggung jawab atau nama organisasi")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_nama_peminjam"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 2. Tanggal Peminjaman (Interactive DatePicker)
        Column {
            Text(
                text = "Tanggal Peminjaman *",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showDatePicker = true }
                    .testTag("input_tanggal_peminjaman"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = formState.tanggalDisplay.ifBlank { "Pilih Tanggal" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Format: ${formState.tanggalFormatted}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TextButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.testTag("change_date_button")
                    ) {
                        Text("Ubah")
                    }
                }
            }
        }

        // 3. Durasi Pinjam (Numeric + Unit)
        Column {
            Text(
                text = "Durasi Peminjaman *",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = formState.durasiNilai,
                    onValueChange = onDurasiNilaiChanged,
                    label = { Text("Jumlah") },
                    placeholder = { Text("4") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = formState.durasiError != null,
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_durasi_nilai"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Satuan Selector: Jam vs Hari
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Jam", "Hari").forEach { unit ->
                        FilterChip(
                            selected = formState.durasiSatuan == unit,
                            onClick = { onDurasiSatuanChanged(unit) },
                            label = { Text(unit, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = if (formState.durasiSatuan == unit) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("chip_durasi_$unit")
                        )
                    }
                }
            }

            formState.durasiError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // 4. No. WhatsApp Peminjam
        Column {
            OutlinedTextField(
                value = formState.noWhatsapp,
                onValueChange = onWhatsappChanged,
                label = { Text("No. WhatsApp Peminjam *") },
                placeholder = { Text("081234567890") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = formState.whatsappError != null,
                supportingText = {
                    formState.whatsappError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    } ?: Text("Untuk konfirmasi dan koordinasi peminjaman")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_no_whatsapp"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 5. Pilihan Aula (Dropdown with exactly 3 options: "Aula PLHUT", "Aula Kemenag", "Aula Kecil")
        Column {
            Text(
                text = "Pilihan Gedung Aula *",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = formState.pilihanAula,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Salah Satu Aula") },
                    leadingIcon = {
                        val icon = when (formState.pilihanAula) {
                            AulaConstants.AULA_PLHUT -> Icons.Default.Mosque
                            AulaConstants.AULA_KEMENAG -> Icons.Default.Apartment
                            else -> Icons.Default.MeetingRoom
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .testTag("dropdown_pilihan_aula"),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    AulaConstants.AULA_OPTIONS.forEach { aulaOption ->
                        val (icon, desc) = when (aulaOption) {
                            AulaConstants.AULA_PLHUT -> Pair(
                                Icons.Default.Mosque,
                                "Pusat Layanan Haji & Umrah Terpadu (Kapasitas Besar)"
                            )
                            AulaConstants.AULA_KEMENAG -> Pair(
                                Icons.Default.Apartment,
                                "Aula Utama Kantor Kemenag (Kapasitas Sedang)"
                            )
                            else -> Pair(
                                Icons.Default.MeetingRoom,
                                "Aula Rapat / Pertemuan Kecil (Kapasitas Terbatas)"
                            )
                        }

                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = aulaOption,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                onAulaSelected(aulaOption)
                                dropdownExpanded = false
                            },
                            modifier = Modifier.testTag("option_$aulaOption")
                        )
                    }
                }
            }
        }

        // 6. Catatan (Multi-line TextField)
        Column {
            OutlinedTextField(
                value = formState.catatan,
                onValueChange = onCatatanChanged,
                label = { Text("Catatan / Kebutuhan Acara") },
                placeholder = { Text("Contoh: Butuh sound system, proyektor, dan 100 kursi susun") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                supportingText = {
                    Text("Opsional: Keterangan jenis acara, fasilitas pendukung, atau susunan acara")
                },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_catatan"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Save & Reset Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (formState.editingId != null) {
                OutlinedButton(
                    onClick = onCancelEditClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("cancel_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Batal")
                }
            }

            Button(
                onClick = onSaveClick,
                enabled = !formState.isSaving,
                modifier = Modifier
                    .weight(if (formState.editingId != null) 2f else 1f)
                    .height(52.dp)
                    .testTag("save_booking_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (formState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (formState.editingId != null) "Perbarui Peminjaman" else "Simpan Peminjaman",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
