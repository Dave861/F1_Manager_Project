package com.david.f1_manager.ui.screens.race.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.david.f1_manager.domain.RaceStanding

@Composable
fun LiveLeaderboard(
    standings: List<RaceStanding>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Text(
                text = "Final Results",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            // Column headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Pos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp)
                )
                Text(
                    text = "Driver",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Team",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Total Time",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(90.dp)
                )
                Text(
                    text = "Gap",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(70.dp)
                )
            }

            HorizontalDivider()

            // Standings list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(standings) { index, standing ->
                    LeaderboardRow(standing = standing)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(standing: RaceStanding) {
    val backgroundColor = when (standing.position) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when (standing.position) {
        in 1..3 -> Color.Black
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor.copy(alpha = if (standing.position <= 3) 0.4f else 1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position
            Text(
                text = standing.position.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )

            // Driver name
            Text(
                text = standing.driverName,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            // Team name
            Text(
                text = standing.teamName,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )

            // Total time
            Text(
                text = String.format("%.1fs", standing.totalTime),
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                modifier = Modifier.width(90.dp)
            )

            // Gap
            Text(
                text = standing.gap,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                fontWeight = if (standing.gap == "Leader") FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.width(70.dp)
            )
        }
    }
}
