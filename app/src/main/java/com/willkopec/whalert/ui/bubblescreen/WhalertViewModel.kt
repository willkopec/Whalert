package com.willkopec.whalert.breakingnews

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.willkopec.whalert.api.RetrofitInstance
import com.willkopec.whalert.api.RetrofitQualifiers
import com.willkopec.whalert.breakingnews.CryptoCache.cachedCryptoItems
import com.willkopec.whalert.breakingnews.CryptoCache.cachedFavoritesData
import com.willkopec.whalert.datastore.PreferenceDatastore
import com.willkopec.whalert.model.coinAPI.CoinAPIResultItem
import com.willkopec.whalert.model.coingecko.CryptoItem
import com.willkopec.whalert.model.newsAPI.Article
import com.willkopec.whalert.repository.CoinAPIRepository
import com.willkopec.whalert.repository.CoingeckoRepository
import com.willkopec.whalert.repository.NewsRepository
import com.willkopec.whalert.repository.PolygonRepository
import com.willkopec.whalert.util.BottomBarScreen
import com.willkopec.whalert.util.ChartType
import com.willkopec.whalert.util.DateUtil.getDateBeforeDaysWithTime
import com.willkopec.whalert.util.IndicatorUtil.getRiskLevel
import com.willkopec.whalert.util.MyPreference
import com.willkopec.whalert.util.Resource
import com.willkopec.whalert.util.SymbolUtils.convertToCoinAPIFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

object CryptoCache {
    var cachedCryptoItems: List<CryptoItem> = emptyList()
    var cachedFavoritesData: MutableMap<String, List<CoinAPIResultItem>?> = mutableMapOf()
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class WhalertViewModel
@Inject
constructor(
    @RetrofitQualifiers.CoinGeckoRetrofitInstance retrofitInstanceCoinGecko: RetrofitInstance,
    @RetrofitQualifiers.PolygonRetrofitInstance retrofitInstancePolygon: RetrofitInstance,
    @RetrofitQualifiers.CoinAPIRetrofitInstance retrofitInstanceCoinAPI: RetrofitInstance,
    @RetrofitQualifiers.NewsAPIRetrofitInstance retrofitInstanceNewsAPI: RetrofitInstance,
    private val myPreference: MyPreference,
    private val preferenceDatastore: PreferenceDatastore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val TAG = "VIEWMODEL"

    private val coinGeckoRepo: CoingeckoRepository = CoingeckoRepository(retrofitInstanceCoinGecko)
    private val polygonRepo: PolygonRepository = PolygonRepository(retrofitInstancePolygon)
    private val coinApiRepo: CoinAPIRepository = CoinAPIRepository(retrofitInstanceCoinAPI)
    private val newsApiRepo: NewsRepository = NewsRepository(retrofitInstanceNewsAPI)


    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

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

    private val _currentNews = MutableStateFlow<List<Article>>(emptyList())
    val currentNews: StateFlow<List<Article>> = _currentNews

    private val _articleDeleted = MutableLiveData<Boolean>()
    val articleDeleted: LiveData<Boolean> = _articleDeleted

    private var _savedList = MutableStateFlow<Set<String>>(emptySet())
    val savedList: StateFlow<Set<String>> = _savedList

    private var _savedListData = MutableStateFlow<Map<String, List<CoinAPIResultItem>?>>(cachedFavoritesData)
    val savedListData: StateFlow<Map<String, List<CoinAPIResultItem>?>> = _savedListData

    private val _bubbleList = MutableStateFlow<List<CryptoItem>>(cachedCryptoItems)
    val bubbleList: StateFlow<List<CryptoItem>> = _bubbleList

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

                // Check if getFavoritesData has been called
                if (!_isInitialized.value) {
                    getFavoritesData(50, 350)
                }
            }
        }

        if (cachedCryptoItems.isEmpty()) {
            Log.d(TAG, "EMPTY")
            getCryptos()
        }

        printFavoritesData()
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
    fun observeDarkModePreference(): Boolean {
        var isDarkMode: Boolean = false
        viewModelScope.launch {

            preferenceDatastore.getDetails().collect { userPreferences ->
                // Handle the retrieved user preferences here
                isDarkMode = userPreferences.darkMode
                // Do something with the dark mode preference
            }
        }
        return isDarkMode
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

    fun getSymbolDataPreview(symbol: String): CryptoItem? {

        _bubbleList.value.forEach {
            //Log.d(TAG, "$symbol - $it")
            if(it.symbol.uppercase() == symbol.uppercase()){
                return it
            }
        }
        return null
    }

    fun setCurrentSymbol(symbol: String){
        viewModelScope.launch {
            preferenceDatastore.setCurrentSymbol(symbol)
        }
    }
    fun updateSymbolAndNavigate(symbol: String, navController: NavController) {
        viewModelScope.launch {
            setCurrentSymbol(symbol)
            navController.navigate(BottomBarScreen.ChartsScreen.route)
        }
    }

    fun getFavoritesData(periodOne: Int, periodTwo: Int) {
        viewModelScope.launch {
            var symbolsProcessed: Int = 0
            _savedList.value.forEach { symbol ->
                Log.d(TAG, "Getting ${symbol} ${symbolsProcessed} / ${_savedList.value.size}")
                if (cachedFavoritesData[symbol] == null || cachedFavoritesData[symbol]?.isEmpty() == true) {
                    val result = coinApiRepo.getSymbolData(
                        convertToCoinAPIFormat(symbol),
                        getDateBeforeDaysWithTime(5000),
                        5000
                    )

                    when (result) {
                        is Resource.Success -> {
                            val priceCloseList = result.data?.reversed()?.mapNotNull { it.price_close }
                            val smaList1 = priceCloseList?.windowed(periodOne, 1) { it.average() }
                            val smaList2 = priceCloseList?.windowed(periodTwo, 1) { it.average() }

                            val updatedData = result.data?.reversed()?.mapIndexed { index, dataItem ->
                                CoinAPIResultItem(
                                    dataItem.price_close,
                                    dataItem.price_high,
                                    dataItem.price_low,
                                    dataItem.price_open,
                                    dataItem.time_close,
                                    dataItem.time_open,
                                    dataItem.time_period_end,
                                    dataItem.time_period_start,
                                    dataItem.trades_count,
                                    dataItem.volume_traded,
                                    smaList1?.getOrNull(index) ?: 0.0,
                                    smaList2?.getOrNull(index) ?: 0.0
                                )
                            }

                            updatedData?.get(0)?.current_risk = updatedData?.let { getRiskLevel(it) }

                            // Update cachedFavoritesData with the updated data
                            cachedFavoritesData[symbol] = updatedData ?: emptyList()
                            symbolsProcessed++
                        }
                        is Resource.Error -> {
                            // Handle error
                            symbolsProcessed++
                        }
                        else -> {
                            // Handle other states if necessary
                            //symbolsProcessed++
                        }
                    }
                    delay(200) // Optional delay between API calls
                } else {
                    // Cached data exists, no need to fetch
                    symbolsProcessed++
                }
            }
            // Check if all symbols have been processed
            if (symbolsProcessed + 1 >= _savedList.value.size) {
                // Set _isInitialized to true after all symbols are processed
                _isInitialized.value = true
            }
        }
    }

    fun printFavoritesData(){
        var index: Int = 0
        for ((key, value) in cachedFavoritesData) {
            if(value?.size!! >= 351){
                Log.d(TAG, "$key - ${value?.get(0)?.time_close} Risk Level: ${value.get(0).current_risk}"/**/)
            }

            /*value?.forEach {
                Log.d(TAG, "${it.time_period_start} : ${it.price_close}")
            }*/
        }
    }

    fun simpleMovingAverageData(period: Int): Map<String, List<Double>> {
        val favoritesSMAmap: MutableMap<String, List<Double>> = mutableMapOf()
        for ((key, value) in cachedFavoritesData) {
            value?.let { list ->
                val priceCloseList: List<Double> = list.map { it.price_close }
                val smaList = priceCloseList.windowed(period, 1) { it.average() }

                smaList.forEach {
                    Log.d(TAG, "Current value: ${it.toString()}")
                }

                // Update the original list with the SMA values
                cachedFavoritesData[key] = list.mapIndexed { index, item ->
                    item.copy(current_sma1 = smaList.getOrNull(index) ?: 0.0)
                }

                favoritesSMAmap[key] = smaList
            }
        }
        return favoritesSMAmap
    }

    fun getSymbolData(symbol: String, daysPriorToToday: Int = 1) {
        Log.d(TAG, "GETTING DATA HERE")
        var daysUntilToday: Int = daysPriorToToday
        viewModelScope.launch {
            _isLoading.value = false
            setCurrentSymbol(symbol)

            val result = coinApiRepo.getSymbolData(
                convertToCoinAPIFormat(symbol),
                getDateBeforeDaysWithTime(daysPriorToToday),
                daysPriorToToday
            )

            when (result) {
                is Resource.Success -> {

                    _isLoading.value = false
                    _currentChartName.value = symbol
                    _loadError.value = ""
                    Log.d(TAG, "Success")
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
                    _loadError.value = result.message ?: "Symbol not found or no internet connection."
                    _isLoading.value = false
                    _currentChartName.value = ""
                    Log.d(TAG, "Error - ${_loadError.value}")
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
                    _bubbleList.value = cachedCryptoItems
                }
                is Resource.Error -> {
                    _loadError.value = result.message ?: "An unknown error occurred"
                }

                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun getCryptoNews() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = newsApiRepo.getCryptoNews()
            when (result) {
                is Resource.Success -> {
                    val breakingNewsArticles = result.data?.articles?.map { article ->
                        Article(
                            article.author,
                            article.content,
                            article.description,
                            article.publishedAt,
                            article.source,
                            article.title,
                            article.url,
                            article.urlToImage
                        )
                    } ?: emptyList()

                    _loadError.value = ""
                    _isLoading.value = false
                    breakingNewsPage++
                    _currentNews.value = breakingNewsArticles
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

