package com.stviz.app.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stviz.app.analytics.PositionMetrics
import com.stviz.app.transaction.TransactionResponse
import com.stviz.app.transaction.TransactionType

data class PortfolioDetailsScreen(val portfolioId: Long, val portfolioName: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = PortfolioDetailsScreenModel(portfolioId)
        val state by screenModel.state.collectAsState()

        var showTradeDialog by remember { mutableStateOf(false) }
        var showPriceDialog: String? by remember { mutableStateOf(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(portfolioName) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showTradeDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Log Trade")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (val current = state) {
                    is PortfolioDetailsState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is PortfolioDetailsState.Error -> Text(current.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                    is PortfolioDetailsState.Success -> {
                        AnalyticsDashboard(current.analytics, current.transactions) { symbol ->
                            showPriceDialog = symbol
                        }
                    }
                }
            }
        }

        if (showTradeDialog) {
            LogTradeDialog(
                onDismiss = { showTradeDialog = false },
                onLog = { request ->
                    screenModel.createTransaction(request)
                    showTradeDialog = false
                }
            )
        }

        showPriceDialog?.let { symbol ->
            UpdatePriceDialog(
                symbol = symbol,
                onDismiss = { showPriceDialog = null },
                onUpdate = { price ->
                    screenModel.updateAssetPrice(symbol, price)
                    showPriceDialog = null
                }
            )
        }
    }
}

@Composable
fun AnalyticsDashboard(
    analytics: com.stviz.app.analytics.PortfolioAnalyticsResponse, 
    transactions: List<TransactionResponse>,
    onPositionClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SummaryCards(analytics)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Holdings", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(analytics.positions) { position ->
            PositionItem(position) { onPositionClick(position.symbol) }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Recent Transactions", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(transactions) { tx ->
            TransactionRow(tx)
        }
    }
}

@Composable
fun SummaryCards(analytics: com.stviz.app.analytics.PortfolioAnalyticsResponse) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricCard("Total Value", "$${analytics.totalValue}", Modifier.weight(1f))
        MetricCard("ROI", "${analytics.roi.format(2)}%", Modifier.weight(1f), 
            color = if (analytics.roi >= 0) Color(0xFF4CAF50) else Color(0xFFF44336))
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricCard("Unrealized P&L", "$${analytics.unrealizedPnl.format(2)}", Modifier.weight(1f))
        MetricCard("Realized P&L", "$${analytics.realizedPnl.format(2)}", Modifier.weight(1f))
    }
}

@Composable
fun MetricCard(label: String, value: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurface) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleLarge, color = color)
        }
    }
}

@Composable
fun PositionItem(position: PositionMetrics, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(position.symbol, style = MaterialTheme.typography.titleMedium)
                Text(position.sector, style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${position.currentValue.format(2)}", style = MaterialTheme.typography.bodyLarge)
                Text("${position.roi.format(1)}%", color = if (position.roi >= 0) Color(0xFF4CAF50) else Color(0xFFF44336))
            }
        }
    }
}

@Composable
fun TransactionRow(tx: TransactionResponse) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(tx.type.name, color = if (tx.type == TransactionType.BUY) Color(0xFF4CAF50) else Color(0xFFF44336), 
            modifier = Modifier.width(50.dp), style = MaterialTheme.typography.labelLarge)
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.symbol, style = MaterialTheme.typography.bodyLarge)
            Text(tx.transactionDate.substring(0, 10), style = MaterialTheme.typography.bodySmall)
        }
        Text("$${(tx.quantity * tx.price).format(2)}", style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
}

@Composable
fun LogTradeDialog(onDismiss: () -> Unit, onLog: (com.stviz.app.transaction.TransactionRequest) -> Unit) {
    var symbol by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.BUY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Trade") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == TransactionType.BUY, onClick = { type = TransactionType.BUY })
                    Text("BUY")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = type == TransactionType.SELL, onClick = { type = TransactionType.SELL })
                    Text("SELL")
                }
                TextField(value = symbol, onValueChange = { symbol = it }, label = { Text("Symbol (e.g. AAPL)") })
                TextField(value = sector, onValueChange = { sector = it }, label = { Text("Sector") })
                TextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") })
                TextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val q = quantity.toDoubleOrNull() ?: 0.0
                val p = price.toDoubleOrNull() ?: 0.0
                if (symbol.isNotBlank() && q > 0 && p > 0) {
                    onLog(com.stviz.app.transaction.TransactionRequest(
                        symbol = symbol,
                        sector = sector,
                        type = type,
                        quantity = q,
                        price = p,
                        transactionDate = "2026-03-24T00:00:00Z" // TODO: Use real date picker
                    ))
                }
            }) {
                Text("Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UpdatePriceDialog(symbol: String, onDismiss: () -> Unit, onUpdate: (Double) -> Unit) {
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Price for $symbol") },
        text = {
            Column {
                TextField(value = price, onValueChange = { price = it }, label = { Text("Current Price") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val p = price.toDoubleOrNull() ?: 0.0
                if (p > 0) onUpdate(p)
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun Double.format(digits: Int) = this.toString()
