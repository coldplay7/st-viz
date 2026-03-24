package com.stviz.app.portfolio

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PortfolioListState {
    object Loading : PortfolioListState()
    data class Success(val portfolios: List<PortfolioResponse>) : PortfolioListState()
    data class Error(val message: String) : PortfolioListState()
}

class PortfolioListScreenModel(private val repository: PortfolioRepository = PortfolioRepository()) : ScreenModel {

    private val _state = MutableStateFlow<PortfolioListState>(PortfolioListState.Loading)
    val state: StateFlow<PortfolioListState> = _state.asStateFlow()

    init {
        loadPortfolios()
    }

    fun loadPortfolios() {
        screenModelScope.launch {
            _state.value = PortfolioListState.Loading
            repository.getPortfolios()
                .onSuccess { 
                    _state.value = PortfolioListState.Success(it)
                }
                .onFailure { 
                    _state.value = PortfolioListState.Error(it.message ?: "Failed to load portfolios")
                }
        }
    }
    
    fun createPortfolio(name: String, description: String?) {
        screenModelScope.launch {
            repository.createPortfolio(CreatePortfolioRequest(name, description))
                .onSuccess { loadPortfolios() }
        }
    }
}
