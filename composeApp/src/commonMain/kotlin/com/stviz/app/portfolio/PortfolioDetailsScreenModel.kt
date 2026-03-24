package com.stviz.app.portfolio

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.stviz.app.analytics.PortfolioAnalyticsResponse
import com.stviz.app.transaction.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PortfolioDetailsState {
    object Loading : PortfolioDetailsState()
    data class Success(
        val analytics: PortfolioAnalyticsResponse,
        val transactions: List<TransactionResponse>
    ) : PortfolioDetailsState()
    data class Error(val message: String) : PortfolioDetailsState()
}

class PortfolioDetailsScreenModel(
    private val portfolioId: Long,
    private val repository: PortfolioRepository = PortfolioRepository(),
    private val priceRepository: com.stviz.app.price.AssetPriceRepository = com.stviz.app.price.AssetPriceRepository()
) : ScreenModel {

    private val _state = MutableStateFlow<PortfolioDetailsState>(PortfolioDetailsState.Loading)
    val state: StateFlow<PortfolioDetailsState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        screenModelScope.launch {
            _state.value = PortfolioDetailsState.Loading
            
            val analyticsResult = repository.getAnalytics(portfolioId)
            val transactionsResult = repository.getTransactions(portfolioId)

            if (analyticsResult.isSuccess && transactionsResult.isSuccess) {
                _state.value = PortfolioDetailsState.Success(
                    analyticsResult.getOrThrow(),
                    transactionsResult.getOrThrow()
                )
            } else {
                val error = analyticsResult.exceptionOrNull()?.message 
                    ?: transactionsResult.exceptionOrNull()?.message 
                    ?: "Unknown error"
                _state.value = PortfolioDetailsState.Error(error)
            }
        }
    }

    fun createTransaction(request: com.stviz.app.transaction.TransactionRequest) {
        screenModelScope.launch {
            repository.createTransaction(portfolioId, request)
                .onSuccess { refresh() }
        }
    }

    fun updateAssetPrice(symbol: String, price: Double) {
        screenModelScope.launch {
            priceRepository.updatePrice(com.stviz.app.price.AssetPriceRequest(symbol, price))
                .onSuccess { refresh() }
        }
    }
}
