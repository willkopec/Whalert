package com.willkopec.whalert.breakingnews

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.api.RetrofitQualifiers
import com.willkopec.whalert.breakingnews.CryptoCache.cachedCryptoItems
import com.willkopec.whalert.datastore.PreferenceDatastore
import com.willkopec.whalert.model.coinAPI.CoinAPIResultItem
import com.willkopec.whalert.model.coingecko.CryptoItem
import com.willkopec.whalert.repository.CoinAPIRepository
import com.willkopec.whalert.repository.CoingeckoRepository
import com.willkopec.whalert.repository.PolygonRepository
import com.willkopec.whalert.util.ChartType
import com.willkopec.whalert.util.DateUtil.getDateBeforeDaysWithTime
import com.willkopec.whalert.util.MyPreference
import com.willkopec.whalert.util.Resource
import com.willkopec.whalert.util.SymbolUtils.convertToCoinAPIFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

object CryptoCache {
    var cachedCryptoItems: List<CryptoItem> = emptyList()
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class WhalertViewModel
@Inject
constructor(
    @RetrofitQualifiers.CoinGeckoRetrofitInstance retrofitInstanceCoinGecko: RetrofitInstance,
    @RetrofitQualifiers.PolygonRetrofitInstance retrofitInstancePolygon: RetrofitInstance,
    @RetrofitQualifiers.CoinAPIRetrofitInstance retrofitInstanceCoinAPI: RetrofitInstance,
    private val myPreference: MyPreference,
    private val preferenceDatastore: PreferenceDatastore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val TAG = "VIEWMODEL"

    private val coinGeckoRepo: CoingeckoRepository = CoingeckoRepository(retrofitInstanceCoinGecko)
    private val polygonRepo: PolygonRepository = PolygonRepository(retrofitInstancePolygon)
    private val coinApiRepo: CoinAPIRepository = CoinAPIRepository(retrofitInstanceCoinAPI)


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loadError = MutableStateFlow("")
    val loadError: StateFlow<String> = _loadError

    private val _endReached = MutableStateFlow(false)
    val endReached: StateFlow<Boolean> = _endReached

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme

    private var breakingNewsPage = 1
    private var searchNewsPage = 1

    private val _currentChartType = MutableStateFlow(ChartType.LINE)
    val currentChartType: StateFlow<ChartType> = _currentChartType

    // Function to update currentSortType
    fun setCurrentSortType(sortType: ChartType) {
        _currentChartType.value = sortType
    }

    private val _scrollToTop = MutableLiveData(false)
    val scrollToTop: LiveData<Boolean>
        get() = _scrollToTop

    private val _articleDeleted = MutableLiveData<Boolean>()
    val articleDeleted: LiveData<Boolean> = _articleDeleted

    private var _savedList = MutableStateFlow<Set<String>>(emptySet())
    val savedList: StateFlow<Set<String>> = _savedList

    private val _breakingNews = MutableStateFlow<List<CryptoItem>>(cachedCryptoItems)
    val breakingNews: StateFlow<List<CryptoItem>> = _breakingNews

    private val _currentChartData = MutableStateFlow<List<CoinAPIResultItem>>(emptyList())
    val currentChartData: StateFlow<List<CoinAPIResultItem>> = _currentChartData.asStateFlow()

    private val _currentChartName = MutableLiveData("")
    val currentChartName: LiveData<String>
        get() = _currentChartName

    init {
        viewModelScope.launch {
            preferenceDatastore.getDetails().collect { userPreferences ->
                _darkTheme.value = userPreferences.darkMode
                _currentChartName.value = userPreferences.currentSymbol
                _savedList.value = userPreferences.favoritesList
            }

        }
        if(cachedCryptoItems.isEmpty()){
            Log.d(TAG, "EMPTY")
            getCryptos()
        }


    }

    fun switchDarkMode() {
        val newValue = !_darkTheme.value
        viewModelScope.launch { updateDarkModePreference(newValue) }
        _darkTheme.value = newValue
    }

    fun updateDarkModePreference(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferenceDatastore.setDarkMode(isDarkMode)
        }
    }

    // Example function to observe dark mode preference
    fun observeDarkModePreference() {
        viewModelScope.launch {
            preferenceDatastore.getDetails().collect { userPreferences ->
                // Handle the retrieved user preferences here
                val isDarkMode = userPreferences.darkMode
                // Do something with the dark mode preference
            }
        }
    }

    fun updateScrollToTop(scroll: Boolean) {
        _scrollToTop.postValue(scroll)
    }

    fun addSymbolToSaved(symbol: String): String {
        viewModelScope.launch {
            preferenceDatastore.addToFavoritesList(symbol.uppercase())
            printSet(_savedList.value)
        }


        return "${"\"\""}"
    }

    fun deleteSymbolFromSaved(symbol: String): String {
        viewModelScope.launch {
            preferenceDatastore.deleteFromFavoritesList(symbol.uppercase())
            printSet(_savedList.value)
        }
        return "${"\"\""}"
    }

    fun isInFavoritesList(symbol: String): Boolean {
        var isInFavories = _savedList.value.contains(symbol.uppercase())
        return isInFavories
    }

    fun getSymbolData(symbol: String, daysPriorToToday: Int = 1) {
        Log.d(TAG, "GETTING DATA HERE")
        var daysUntilToday: Int = daysPriorToToday
        viewModelScope.launch {
            _isLoading.value = false
            preferenceDatastore.setCurrentSymbol(symbol)

            val result = coinApiRepo.getSymbolData(
                convertToCoinAPIFormat(symbol),
                getDateBeforeDaysWithTime(daysPriorToToday),
                daysPriorToToday
            )

            when (result) {
                is Resource.Success -> {

                    _isLoading.value = false
                    //_currentChartName.value = symbol
                    _loadError.value = ""
                    /*val currentChartDataa = result.data?.map {
                        //Log.d(TAG, "${it.c}")
                        CoinAPIResultItem(
                            it.price_close,
                            it.price_high,
                            it.price_low,
                            it.price_open,
                            it.time_close,
                            it.time_open,
                            it.time_period_end,
                            it.time_period_start,
                            it.trades_count,
                            it.volume_traded
                        )
                    } ?: emptyList()

                    _loadError.value = ""
                    _isLoading.value = false
                    _currentChartData.value = currentChartDataa
                    _currentChartName.value = symbol
                    //Log.d(TAG, "HERE 2 : ${_currentChartData.value.size}")*/
                }
                is Resource.Error -> {
                    _loadError.value = result.message ?: ""
                    _isLoading.value = false
                    _currentChartName.value = ""
                }
                else -> {}
            }
        }
    }

    fun getSymbolDataForPreview(symbol: String, daysPriorToToday: Int = 1): StateFlow<List<CoinAPIResultItem>> {
        val currentChartData = MutableStateFlow<List<CoinAPIResultItem>>(emptyList())

        viewModelScope.launch {
            _isLoading.value = true

            val result = coinApiRepo.getSymbolData(
                convertToCoinAPIFormat(symbol),
                getDateBeforeDaysWithTime(daysPriorToToday),
                daysPriorToToday
            )

            when (result) {
                is Resource.Success -> {
                    _loadError.value = ""
                    val data = result.data?.map {
                        //Log.d(TAG, "${it.price_close}")
                        CoinAPIResultItem(
                            it.price_close,
                            it.price_high,
                            it.price_low,
                            it.price_open,
                            it.time_close,
                            it.time_open,
                            it.time_period_end,
                            it.time_period_start,
                            it.trades_count,
                            it.volume_traded
                        )
                    } ?: emptyList()

                    currentChartData.value = data
                    _loadError.value = ""
                    _isLoading.value = false
                }
                is Resource.Error -> {
                    _loadError.value = result.message ?: ""
                    _isLoading.value = false
                    _currentChartName.value = ""
                }
                else -> {}
            }
        }

        return currentChartData
    }

    fun printSet(currentList: Set<String>) {
        Log.d(TAG, "Favorites List:")
        currentList.map {
            Log.d(TAG, "${it}")
        }
    }

    fun printList(currentList: List<CryptoItem>) {
        Log.d(TAG, "Bubbles List:")
        currentList.map {
            Log.d(TAG, "${it}")
        }
    }


    fun getCryptos() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = coinGeckoRepo.getCryptoList(breakingNewsPage)
            when (result) {
                is Resource.Success -> {
                    cachedCryptoItems = result.data ?: emptyList()
                    _loadError.value = ""
                    _breakingNews.value = cachedCryptoItems
                }
                is Resource.Error -> {
                    _loadError.value = result.message ?: "An unknown error occurred"
                }

                else -> {}
            }
            _isLoading.value = false
        }
    }



}

