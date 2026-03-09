package noor.serry.rawaa

import android.app.Application
import noor.serry.rawaa.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

     override fun onCreate() {
        super.onCreate()
         startKoin {
             androidContext(this@App)
             modules(appModule)
         }
    }
}