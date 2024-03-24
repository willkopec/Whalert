package com.willkopec.whalert.breakingnews

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willkopec.whalert.model.CryptoItem
import com.willkopec.whalert.model.Roi
import com.willkopec.whalert.repository.CoingeckoRepository
import com.willkopec.whalert.util.Crypto
import com.willkopec.whalert.util.MyPreference
import com.willkopec.whalert.util.Resource
import com.willkopec.whalert.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class WhalertViewModel
@Inject
constructor(
    private val newsRepository: CoingeckoRepository,
    private val myPreference: MyPreference,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val TAG = "VIEWMODEL"

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loadError = MutableStateFlow("")
    val loadError: StateFlow<String> = _loadError

    private val _endReached = MutableStateFlow(false)
    val endReached: StateFlow<Boolean> = _endReached

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _darkTheme = MutableStateFlow(myPreference.isDarkMode())
    val darkTheme: StateFlow<Boolean> = _darkTheme

    private var breakingNewsPage = 1
    private var searchNewsPage = 1

    private val _currentSortType = MutableStateFlow(SortType.BREAKING)
    val currentSortType: StateFlow<SortType> = _currentSortType

    // Function to update currentSortType
    fun setCurrentSortType(sortType: SortType) {
        _currentSortType.value = sortType
    }

    private val _scrollToTop = MutableLiveData(false)
    val scrollToTop: LiveData<Boolean>
        get() = _scrollToTop

    private val _articleDeleted = MutableLiveData<Boolean>()
    val articleDeleted: LiveData<Boolean> = _articleDeleted

    fun switchDarkMode() {
        val newValue = !_darkTheme.value
        _darkTheme.value = newValue
        viewModelScope.launch { myPreference.switchDarkMode() }
    }

    fun updateScrollToTop(scroll: Boolean) {
        _scrollToTop.postValue(scroll)
    }

    val cryptoNames = MutableLiveData<List<CryptoItem>>()

    private val _breakingNews = MutableStateFlow<List<CryptoItem>>(emptyList())
    val breakingNews: StateFlow<List<CryptoItem>> = _breakingNews

    init {
        getCryptos()
        printList()
    }

    fun getCryptos() {
        Log.d(TAG, "GETTING CRYPTOS")
        viewModelScope.launch {
            _isLoading.value = true
            val result = newsRepository.getBreakingNews(breakingNewsPage)
            when (result) {
                is Resource.Success -> {
                    val breakingNewsArticles = result.data?.map { crypto ->
                        //Log.d(TAG, "${crypto.name}")
                        CryptoItem(
                            crypto.ath,
                            crypto.ath_change_percentage,
                            crypto.ath_date,
                            crypto.atl,
                            crypto.atl_change_percentage,
                            crypto.atl_date,
                            crypto.circulating_supply,
                            crypto.current_price,
                            crypto.fully_diluted_valuation,
                            crypto.high_24h,
                            crypto.id,
                            crypto.image,
                            crypto.last_updated,
                            crypto.low_24h,
                            crypto.market_cap,
                            crypto.market_cap_change_24h,
                            crypto.market_cap_change_percentage_24h,
                            crypto.market_cap_rank,
                            crypto.max_supply,
                            crypto.name,
                            crypto.price_change_24h,
                            crypto.price_change_percentage_24h,
                            crypto.roi,
                            crypto.symbol,
                            crypto.total_supply,
                            crypto.total_volume
                        )
                    } ?: emptyList()

                    _loadError.value = ""
                    _isLoading.value = false
                    breakingNewsPage++
                    _breakingNews.value += breakingNewsArticles
                }
                is Resource.Error -> {
                    _loadError.value = result.message ?: ""
                    _isLoading.value = false
                }
                else -> {}
            }
        }
    }

    fun printList(){
        Log.d(TAG, "SHOULD PRINT LIST")
        _breakingNews.value.forEach {
            Log.d(TAG, "${it.name}: ")
        }
    }


}

