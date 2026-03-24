package com.stviz.app.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class PortfolioListScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = PortfolioListScreenModel()
        val state by screenModel.state.collectAsState()
        
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("My Portfolios") })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Portfolio")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (val current = state) {
                    is PortfolioListState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is PortfolioListState.Error -> Text(current.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                    is PortfolioListState.Success -> {
                        if (current.portfolios.isEmpty()) {
                            Text("No portfolios yet. Create one!", modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(current.portfolios) { portfolio ->
                                    PortfolioItem(portfolio) {
                                        navigator.push(PortfolioDetailsScreen(portfolio.id, portfolio.name))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            CreatePortfolioDialog(
                onDismiss = { showDialog = false },
                onCreate = { name, desc ->
                    screenModel.createPortfolio(name, desc)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun PortfolioItem(portfolio: PortfolioResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(portfolio.name, style = MaterialTheme.typography.titleLarge)
            portfolio.description?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun CreatePortfolioDialog(onDismiss: () -> Unit, onCreate: (String, String?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Portfolio") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onCreate(name, desc) }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
