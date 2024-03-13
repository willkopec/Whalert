package com.willkopec.whalert.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /*@Singleton
    @Provides
    fun provideNewsDatabase(@ApplicationContext context: Context): ArticleDatabase {
            return Room.databaseBuilder(
                context,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }

    @Singleton
    @Provides
    fun provideNewsDao(newsDatabase: ArticleDatabase): ArticleDao {
        return newsDatabase.getArticleDao()
    }

    @Singleton
    @Provides
    fun provideNewsRepository(newsDao: ArticleDao): NewsRepository {
        return NewsRepository(newsDao)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    class SharedPreferencesModule {

        @Singleton
        @Provides
        fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("preferences_name", Context.MODE_PRIVATE)
        }
    }*/

}