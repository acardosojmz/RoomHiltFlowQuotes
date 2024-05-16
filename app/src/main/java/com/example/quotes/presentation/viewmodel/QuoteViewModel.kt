package com.example.quotes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes.domain.model.QuoteModel
import com.example.quotes.domain.model.QuoteState
import com.example.quotes.domain.usecase.AddQuoteUseCase
import com.example.quotes.domain.usecase.GetQuotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel
@Inject constructor (
    private val getQuotesUseCase: GetQuotesUseCase,
    private val addQuoteUseCase: AddQuoteUseCase,
): ViewModel(){

    private val _uiState = MutableStateFlow<QuoteState>(QuoteState.Init)
    val uiState: StateFlow<QuoteState> = _uiState

    private val _quoteModel = MutableStateFlow(QuoteModel(0, "", ""))
    val quoteModel = _quoteModel.asStateFlow()

    fun setId(id: Int) {
        _quoteModel.value = _quoteModel.value.copy(id = id)
    }

    fun setQuote(quote: String) {
        _quoteModel.value = _quoteModel.value.copy(quote = quote)
    }

    fun setAuthor(author: String) {
        _quoteModel.value = _quoteModel.value.copy(author = author)
    }


    fun getQuotes() = viewModelScope.launch {
        try {
            _uiState.value = QuoteState.Loading
            val uiStates = getQuotesUseCase.getQuotes().first()
            withContext(Dispatchers.IO) {
                when (uiStates) {
                    is QuoteState.Data -> notifyDataState(uiStates.quotes)
                    is QuoteState.Error -> notifyErrorState(uiStates.error)
                    is QuoteState.Init -> notifyInitState()
                    is QuoteState.Loading -> notifyLoadingState()
                    is QuoteState.Success -> notifySuccess(uiStates.message)
                }
            }
        } catch (ex: Exception){
            notifyErrorState(ex)
        }
    }

    fun saveQuote() {
        viewModelScope.launch {
             println("ID: ${_quoteModel.value.id}, Quote: ${_quoteModel.value.quote}, Author: ${_quoteModel.value.author}")
            _uiState.value = QuoteState.Loading
            addQuoteUseCase.addQuote(
                QuoteModel(_quoteModel.value.id, _quoteModel.value.quote, _quoteModel.value.author)
            )
        }
    }


    private fun notifyInitState() {
        _uiState.value= QuoteState.Init
    }
    private fun notifyLoadingState() {
        _uiState.value = QuoteState.Loading
    }

    private fun notifyDataState(quotes: List<QuoteModel>) {
        _uiState.value= QuoteState.Data(quotes)
    }

    private fun notifyErrorState(error: Throwable) {
        _uiState.value= QuoteState.Error(error)
    }
    private fun notifySuccess(message: String) {
        _uiState.value= QuoteState.Success(message)
    }
//-------------------------------------------

}