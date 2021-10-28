package sk.ferini.daggerassistedtest

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

class MainActivity : AppCompatActivity() {

    private lateinit var component: ActivityComponent

    @Inject internal lateinit var foo: Foo
    @Inject internal lateinit var bar: Bar
    @Inject internal lateinit var baz: Baz

    @Inject internal lateinit var manuallyAssistedViewModelFactory: ManuallyAssistedViewModel.Factory
    @Inject internal lateinit var assistedViewModelFactory: AssistedViewModel.Factory

    private val viewModel1 by viewModels<NonAssistedViewModel> {
        application.component.viewModelFactory
    }

    private val viewModel2 by viewModels<ManuallyAssistedViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return manuallyAssistedViewModelFactory.create(42) as T
            }

        }
    }

    private val viewModel3 by viewModels<AssistedViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedViewModelFactory.create(42) as T
            }

        }
    }

    private val viewModel4 by viewModels<AssistedViewModel2> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return application.component.assistedFactory.create(42) as T
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = application.component.activityComponentFactory.build(this)
        component.inject(this)

        setContentView(R.layout.activity_main)

        Log.d("Feri", "ViewModel #1: $viewModel1")
        Log.d("Feri", "ViewModel #2: $viewModel2")
        Log.d("Feri", "ViewModel #3: $viewModel3")
        Log.d("Feri", "ViewModel #4: $viewModel4")
    }
}

class NonAssistedViewModel @Inject constructor(
    foo: Foo,
    bar: Bar,
//    baz: Baz, // - fails with [Dagger/IncompatiblyScopedBindings]
) : ViewModel()

class ManuallyAssistedViewModel constructor(
    foo: Foo,
    bar: Bar,
//    baz: Baz,
    assisted: Int
) : ViewModel() {

    @Singleton
    class Factory @Inject constructor(
        val fooProvider: Provider<Foo>,
        val barProvider: Provider<Bar>,
//        val bazProvider: Provider<Baz>,// - fails with [Dagger/IncompatiblyScopedBindings]
    ) {
        fun create(assited: Int) = ManuallyAssistedViewModel(
            fooProvider.get(),
            barProvider.get(),
//            bazProvider.get(),
            assited
        )
    }

}

class AssistedViewModel @AssistedInject constructor(
    foo: Foo,
    bar: Bar,
    baz: Baz,
    activity: Activity,
    @Assisted assisted: Int
) : ViewModel() {

    @Singleton // this does nothing
    @AssistedFactory
    interface Factory {
        fun create(assited: Int): AssistedViewModel
    }
}

class AssistedViewModel2 @AssistedInject constructor(
    foo: Foo,
    bar: Bar,
//    baz: Baz, // fails with [Dagger/IncompatiblyScopedBindings]
    @Assisted assisted: Int
) : ViewModel() {

    @Singleton // this does nothing
    @AssistedFactory
    interface Factory {
        fun create(assited: Int): AssistedViewModel2
    }
}


class Foo @Inject constructor()

@Singleton
class Bar @Inject constructor()

@ActivityScope
class Baz @Inject constructor()