package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    var selectedBrand by remember { mutableStateOf<String?>(null) }
    var selectedModel by remember { mutableStateOf<String?>(null) }

    // فهرست برندهای موجود در پایگاه‌داده (به‌ترتیب فراوانی راهنماها)
    val brands = remember(guides) {
        guides.map { it.brand }.distinct().sortedBy { if (it == "همه برندها") "" else it }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("عیب‌یابی گوشی", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Text(
            "بیش از ${red.line.tamirkar.util.PersianDateUtils.toPersianNumber(guides.size)} راهنما به تفکیک برند، مدل و نوع ایراد",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            placeholder = { Text("مثلاً: خاموش نمی‌شود، iPhone 7، دوربین...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ===== سطح اول فیلتر: برند =====
        Text("۱. برند دستگاه", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedBrand == null,
                    onClick = { selectedBrand = null; selectedModel = null },
                    label = { Text("همه برندها") }
                )
            }
            items(brands) { brand ->
                FilterChip(
                    selected = selectedBrand == brand,
                    onClick = {
                        selectedBrand = if (selectedBrand == brand) null else brand
                        selectedModel = null
                    },
                    label = { Text(brand) }
                )
            }
        }

        // ===== سطح دوم فیلتر: مدل (فقط وقتی برند انتخاب شده) =====
        if (selectedBrand != null) {
            val models = remember(guides, selectedBrand) {
                guides.filter { it.brand == selectedBrand }.map { it.model }.distinct()
                    .sortedBy { if (it.contains("همه")) "" else it }
            }
            if (models.size > 1) {
                Spacer(Modifier.height(8.dp))
                Text("۲. مدل خاص (اختیاری)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedModel == null,
                            onClick = { selectedModel = null },
                            label = { Text("همه مدل‌های ${selectedBrand}") }
                        )
                    }
                    items(models) { model ->
                        FilterChip(
                            selected = selectedModel == model,
                            onClick = { selectedModel = if (selectedModel == model) null else model },
                            label = { Text(model) }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ===== سطح سوم فیلتر: دسته‌بندی نوع ایراد =====
        Text("۳. نوع ایراد", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
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
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                    label = { Text(cat.label) }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        val filtered = guides.filter {
            (selectedCategory == null || it.category == selectedCategory) &&
                (selectedBrand == null || it.brand == selectedBrand) &&
                (selectedModel == null || it.model == selectedModel)
        }

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                red.line.tamirkar.ui.components.EmptyState(
                    icon = Icons.Filled.FindInPage,
                    title = "موردی یافت نشد",
                    description = "کلمه‌ی جستجو، برند یا دسته‌بندی دیگری را امتحان کنید"
                )
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

    ElevatedCard(modifier = Modifier.fillMaxWidth(), onClick = { expanded = !expanded }) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(guide.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Row {
                        AssistChip(
                            onClick = {},
                            label = { Text(guide.model, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(28.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${guide.category.label} • ${guide.brand} • سطح دشواری: ${guide.difficulty}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                }
            }

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Divider()
                Spacer(Modifier.height(10.dp))
                Text("علائم:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Text(guide.symptoms, style = MaterialTheme.typography.bodyMedium)

                if (guide.possibleCauses != "-") {
                    Spacer(Modifier.height(10.dp))
                    Text("علت‌های احتمالی:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Text(guide.possibleCauses, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(10.dp))
                Text("مراحل رفع ایراد:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Text(guide.solutionSteps, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
