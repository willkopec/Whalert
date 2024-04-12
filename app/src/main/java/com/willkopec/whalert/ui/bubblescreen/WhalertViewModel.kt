package com.willkopec.whalert.breakingnews

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.api.RetrofitQualifiers
import com.willkopec.whalert.model.coingecko.CryptoItem
import com.willkopec.whalert.model.polygon.Result
import com.willkopec.whalert.repository.CoingeckoRepository
import com.willkopec.whalert.repository.PolygonRepository
import com.willkopec.whalert.ui.chartscreen.getHtmlContent
import com.willkopec.whalert.util.DateUtil.getCurrentDate
import com.willkopec.whalert.util.DateUtil.getDateBeforeDays
import com.willkopec.whalert.util.DateUtil.getDateBeforeMonths
import com.willkopec.whalert.util.MyPreference
import com.willkopec.whalert.util.Resource
import com.willkopec.whalert.util.SortType
import com.willkopec.whalert.util.SymbolUtils.convertToApiSymbolString
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
    @RetrofitQualifiers.CoinGeckoRetrofitInstance retrofitInstanceCoinGecko: RetrofitInstance,
    @RetrofitQualifiers.PolygonRetrofitInstance retrofitInstancePolygon: RetrofitInstance,
    private val myPreference: MyPreference,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val TAG = "VIEWMODEL"

    private val coinGeckoRepo: CoingeckoRepository = CoingeckoRepository(retrofitInstanceCoinGecko)
    private val polygonRepo: PolygonRepository = PolygonRepository(retrofitInstancePolygon)

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

    private val _currentChartData = MutableStateFlow<List<Result>>(emptyList())
    val currentChartData: StateFlow<List<Result>> = _currentChartData

    private val _currentChartName = MutableLiveData("")
    val currentChartName: LiveData<String>
        get() = _currentChartName

    init {
        getCryptos()
        //getSymbolData("BTC", 101)
        getSymbolData("BTC", 101)

    }

    fun getSymbolData(symbol: String, daysPriorToToday: Int) {
        var daysUntilToday: Int = daysPriorToToday
        viewModelScope.launch {
            _isLoading.value = true
            val result = polygonRepo.getSymbolData(
                convertToApiSymbolString(symbol),
                getDateBeforeDays(daysPriorToToday),
                getCurrentDate()
            )
            when (result) {
                is Resource.Success -> {
                    val currentChartDataa = result.data?.results?.map {
                        //Log.d(TAG, "${it.c}")
                        daysUntilToday--
                        Result(
                            it.c,
                            it.h,
                            it.l,
                            it.n,
                            it.o,
                            it.t,
                            it.v,
                            getDateBeforeDays(daysUntilToday)
                        )
                    } ?: emptyList()

                    _loadError.value = ""
                    _isLoading.value = false
                    _currentChartData.value += currentChartDataa
                    _currentChartName.value = symbol
                    Log.d(TAG, "HERE 2 : ${_currentChartData.value.size}")
                }
                is Resource.Error -> {
                    _loadError.value = result.message ?: ""
                    _isLoading.value = false
                }
                else -> {}
            }
        }
    }

    private fun printListAfterDataAvailable() {
        viewModelScope.launch {
            _currentChartData.collect { data ->
                if (!data.isNullOrEmpty()) {
                    printList()
                    // You may choose to cancel this coroutine job if needed
                }
            }
        }
        Log.d(TAG, "SIZE: ${currentChartData.value.size}")
    }

    fun printList() {
        Log.d(TAG, "SHOULD PRINT LIST")
        _currentChartData.value?.forEach {
            Log.d(TAG, "${it.c}")
        }
    }


    fun getCryptos() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = coinGeckoRepo.getCryptoList(breakingNewsPage)
            when (result) {
                is Resource.Success -> {
                    val breakingNewsArticles = result.data?.map { crypto ->
                        Log.d(TAG, "${crypto.name}")
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



}

