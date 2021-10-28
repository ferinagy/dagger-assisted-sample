package sk.ferini.daggerassistedtest

import android.app.Application

class MyApplication : Application() {

    lateinit var component: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.factory().build(this)
    }
}

val Application.component
    get() = (this as MyApplication).component