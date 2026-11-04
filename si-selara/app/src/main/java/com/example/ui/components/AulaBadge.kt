package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AulaConstants
import com.example.ui.theme.AulaKecilBg
import com.example.ui.theme.AulaKecilColor
import com.example.ui.theme.AulaKemenagBg
import com.example.ui.theme.AulaKemenagColor
import com.example.ui.theme.AulaPlhutBg
import com.example.ui.theme.AulaPlhutColor

@Composable
fun AulaBadge(
    aulaName: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor, icon) = when (aulaName) {
        AulaConstants.AULA_PLHUT -> AulaBadgeStyle(
            bgColor = AulaPlhutBg,
            textColor = AulaPlhutColor,
            borderColor = AulaPlhutColor.copy(alpha = 0.3f),
            icon = Icons.Default.Mosque
        )
        AulaConstants.AULA_KEMENAG -> AulaBadgeStyle(
            bgColor = AulaKemenagBg,
            textColor = AulaKemenagColor,
            borderColor = AulaKemenagColor.copy(alpha = 0.3f),
            icon = Icons.Default.Apartment
        )
        else -> AulaBadgeStyle(
            bgColor = AulaKecilBg,
            textColor = AulaKecilColor,
            borderColor = AulaKecilColor.copy(alpha = 0.3f),
            icon = Icons.Default.MeetingRoom
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = aulaName,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private data class AulaBadgeStyle(
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val icon: ImageVector
)
