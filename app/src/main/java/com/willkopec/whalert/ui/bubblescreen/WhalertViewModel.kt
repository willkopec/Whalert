package com.willkopec.whalert.breakingnews

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willkopec.whalert.util.Crypto
import com.willkopec.whalert.util.MyPreference
import com.willkopec.whalert.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class WhalertViewModel
@Inject
constructor(
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

    val cryptoNames = MutableLiveData<List<Crypto>>()

    init {
        Log.d("VIEWMODEL", "SOMETHING HERE??")
        scrapeCryptoNames(cryptoNames)
        cryptoNames.observeForever { names ->
            names?.forEach { name ->
                Log.d("VIEWMODEL", "${name.symbol} ${name.price}")
            }
        }
    }

    fun scrapeCryptoNames(cryptoLiveData: MutableLiveData<List<Crypto>>) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Fetch the webpage
                val url = "https://coinmarketcap.com/"
                val doc: Document = Jsoup.connect(url).get()

                // Select the table containing crypto names
                val table: Elements = doc.select("table.sc-14cb040a-3.dsflYb.cmc-table")

                // Extract the rows from the table
                val rows: Elements = table.select("tbody > tr")

                val cryptoList = mutableListOf<Crypto>()

                // Iterate over each row and extract the crypto data
                for (row in rows) {
                    // Extracting data from each row
                    val rankText: String = row.select("td:nth-child(1)").text()
                    val rank: Int? = if (rankText.isNotBlank()) rankText.toIntOrNull() else null
                    val cryptoName: String = row.select("td:nth-child(2)").text()
                    val cryptoSymbol: String = row.select("td:nth-child(3)").text()
                    val cryptoPrice: String = row.select("td:nth-child(4)").text()

                    // Create a Crypto object and add it to the list
                    val crypto = Crypto(cryptoName, cryptoSymbol, cryptoPrice)
                    cryptoList.add(crypto)
                }

                // Update the MutableLiveData with the new list of cryptocurrencies
                cryptoLiveData.postValue(cryptoList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*private fun scrapeTopCryptos() {
        val url = "https://coinmarketcap.com/"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(url).get()
                val cryptoNamesList = mutableListOf<String>()
                val rows = doc.select("tr")

                for (row in rows) {
                    val nameElement = row.select("td > a > span")
                    val name = nameElement.text().trim()
                    if (name.isNotEmpty()) {
                        cryptoNamesList.add(name)

                        Log.d(TAG, "${name}")
                    }
                }

                cryptoNames.postValue(cryptoNamesList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/
}

