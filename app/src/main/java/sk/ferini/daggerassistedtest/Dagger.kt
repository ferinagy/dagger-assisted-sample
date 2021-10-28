package sk.ferini.daggerassistedtest

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Provider
import javax.inject.Scope
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    val viewModelFactory: ViewModelProvider.Factory

    val assistedFactory: AssistedViewModel2.Factory

    val activityComponentFactory: ActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun build(@BindsInstance app: MyApplication): AppComponent
    }
}

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@ActivityScope
@Subcomponent
interface ActivityComponent {

    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun build(@BindsInstance activity: Activity): ActivityComponent
    }
}

@Module
object AppModule {

    @Singleton
    @Provides
    fun provideViewModelFactory(
        myViewModelProvider: Provider<NonAssistedViewModel>
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(NonAssistedViewModel::class.java) -> {
                        myViewModelProvider.get() as T
                    }
                    else -> TODO()
                }
            }

        }
    }
}