package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.TroubleshootingCategory
import red.line.tamirkar.data.TroubleshootingGuide

@Composable
fun TroubleshootingScreen(
    isVip: Boolean,
    guides: List<TroubleshootingGuide>,
    onQueryChange: (String) -> Unit,
    onUpgradeClick: () -> Unit
) {
    if (!isVip) {
        VipLockedContent(
            title = "پایگاه‌داده عیب‌یابی گوشی",
            description = "دسترسی به راهنمای رفع ایرادهای رایج برندهای مختلف، مخصوص اعضای VIP است.",
            onUpgradeClick = onUpgradeClick
        )
        return
    }

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<TroubleshootingCategory?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("عیب‌یابی گوشی", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            placeholder = { Text("جستجو بر اساس ایراد، برند یا علائم...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("همه") }
                )
            }
            items(TroubleshootingCategory.entries) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat.label) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val filtered = guides.filter { selectedCategory == null || it.category == selectedCategory }

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("موردی یافت نشد", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { guide ->
                    GuideCard(guide)
                }
            }
        }
    }
}

@Composable
private fun GuideCard(guide: TroubleshootingGuide) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(guide.title, style = MaterialTheme.typography.titleMedium)
                    Text("${guide.category.label} • ${guide.brand} • سطح: ${guide.difficulty}", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                }
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text("علائم:", style = MaterialTheme.typography.labelSmall)
                Text(guide.symptoms, style = MaterialTheme.typography.bodyMedium)

                if (guide.possibleCauses != "-") {
                    Spacer(Modifier.height(8.dp))
                    Text("علت‌های احتمالی:", style = MaterialTheme.typography.labelSmall)
                    Text(guide.possibleCauses, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(8.dp))
                Text("مراحل رفع ایراد:", style = MaterialTheme.typography.labelSmall)
                Text(guide.solutionSteps, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
