package com.willkopec.whalert.di

// AppComponent.kt
import com.willkopec.whalert.breakingnews.WhalertViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(viewModel: WhalertViewModel)
    // Add other functions to inject dependencies into other classes if needed
}