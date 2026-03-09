package noor.serry.rawaa.di

import noor.serry.rawaa.ui.base.DefaultDispatcherProvider
import noor.serry.rawaa.ui.base.DispatcherProvider
import noor.serry.rawaa.ui.screens.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val uiModule = module{
    viewModelOf(::OnboardingViewModel)
    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class
}