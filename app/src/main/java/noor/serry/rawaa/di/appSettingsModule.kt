package noor.serry.rawaa.di

import noor.serry.rawaa.data.repository.AppSettingsRepository
import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.screens.onboarding.OnboardingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module that wires [AppSettingsRepository], [MainViewModel], and
 * the updated [OnboardingViewModel].
 *
 * Merge the contents of this file into your existing appModule / dataModule
 * as appropriate — it is shown as a standalone file for clarity.
 *
 * Prerequisites already assumed to be declared elsewhere in your DI graph:
 *   - DataStore<Preferences>  (bound to Context.dataStore)
 *   - TokenDataStore
 *   - DispatcherProvider
 */
val appSettingsModule = module {

    // ── Repository ────────────────────────────────────────────────────────────

    /**
     * Single instance — shares the same DataStore<Preferences> that
     * TokenDataStore already uses, so there is exactly one file on disk.
     *
     * get() resolves DataStore<Preferences> and TokenDataStore from your
     * existing dataModule.
     */
    single {
        AppSettingsRepository(
            dataStore    = get(),   // DataStore<Preferences> from dataModule
            tokenDataStore = get(), // TokenDataStore from dataModule
        )
    }

    // ── ViewModels ────────────────────────────────────────────────────────────

    /**
     * MainViewModel is declared as a *single* so that MainActivity and
     * RawaaApp always hold a reference to the same instance.
     *
     * If you prefer activity-scoped (survives config change, auto-cleared
     * when the activity is destroyed), use `viewModel { MainViewModel(get()) }`
     * and inject it with `by viewModel()` in MainActivity instead.
     */


}