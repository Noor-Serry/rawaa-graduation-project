plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

}

android {
    namespace = "noor.serry.rawaa"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "noor.serry.rawaa"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.constraintlayout)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    //Splash Api
    implementation(libs.androidx.core.splashscreen)
    // koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    //navigation3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(projects.designSystem)
    // google sign in
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // Credential Manager (الطريقة الحديثة)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Koin
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.core)
    // Ktor
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.core)
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.client.okhttp)
    //datastore
    implementation(libs.androidx.datastore.preferences.v121)


    implementation("androidx.compose.material:material-icons-extended")

    implementation(libs.androidx.constraintlayout.compose)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)
}

tasks.register("generateFeature") {
    val featureNameProvider = providers.gradleProperty("featureName")
    val basePackageProvider = providers.gradleProperty("basePackage")

    doLast {
        val featureName = getFeatureName(featureNameProvider)
        val basePackage = getBasePackage(basePackageProvider)

        val sourceRoot = getSourceRoot()
        val featurePackagePath = "$basePackage.${featureName.replaceFirstChar { it.lowercase() }}"
        val featureDir = createDirectories(sourceRoot, featurePackagePath)

        val componentDir = File(featureDir, "component")
        if (!componentDir.exists()) componentDir.mkdirs()

        val filesToGenerate = mapOf(
            "${featureName}Screen.kt" to generateScreenContent(featureName, featurePackagePath),
            "${featureName}ViewModel.kt" to generateViewModelContent(featureName, featurePackagePath),
            "${featureName}Mapper.kt" to generateMapperContent(featurePackagePath),
            "${featureName}Effect.kt" to generateEffectContent(featureName, featurePackagePath),
            "${featureName}InteractionListener.kt" to generateListenerContent(featureName, featurePackagePath),
            "${featureName}UiState.kt" to generateUiStateContent(featureName, featurePackagePath)
        )

        filesToGenerate.forEach { (fileName, content) -> createFile(featureDir, fileName, content) }

        println("Feature '$featureName' generated successfully at $featureDir")
    }
}

// ===== Helper functions =====

fun getFeatureName(provider: Provider<String>) =
    provider.orNull ?: throw GradleException("Please provide -PfeatureName=YourFeature")

fun getBasePackage(provider: Provider<String>) =
    provider.orNull ?: throw GradleException("Please provide -PbasePackage=com.example.app.ui.screens")

fun getSourceRoot(): String {
    val android = project.extensions.getByName("android")

    val sourceSets = android.javaClass
        .getMethod("getSourceSets")
        .invoke(android) as Iterable<*>

    val mainSourceSet = sourceSets.firstOrNull {
        it!!.javaClass.getMethod("getName").invoke(it) == "main"
    } ?: throw GradleException("Main source set not found")

    val java = mainSourceSet.javaClass.getMethod("getJava").invoke(mainSourceSet)
    val dirs = java.javaClass.getMethod("getSrcDirs").invoke(java) as Set<*>

    return dirs.firstOrNull()?.toString()
        ?: throw GradleException("Cannot find source root for main source set")
}

fun createDirectories(sourceRoot: String, featurePackagePath: String): File {
    val featureDir = File(sourceRoot, featurePackagePath.replace(".", "/"))
    if (!featureDir.exists()) featureDir.mkdirs()
    return featureDir
}

fun createFile(dir: File, fileName: String, content: String) {
    val file = File(dir, fileName)
    if (!file.exists()) file.writeText(content)
}

// ===== Template generators =====

fun generateScreenContent(featureName: String, packagePath: String) = """
    package $packagePath

    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.collectLatest
    import org.koin.compose.viewmodel.koinViewModel
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue

    @Composable
    fun ${featureName}Screen(viewModel: ${featureName}ViewModel = koinViewModel()) {
        val state by viewModel.state.collectAsState()
        
        ${featureName}Content(
            state = state,
            interactionListener = viewModel
        )
    }

    @Composable
    private fun ${featureName}Content(
        state: ${featureName}UiState,
        interactionListener: ${featureName}InteractionListener
    ) {

    }

    @Composable
    private fun HandleEffects(effects: Flow<${featureName}Effect>) {
        LaunchedEffect(Unit) {
            effects.collectLatest { effect ->
                when(effect) {
                    else -> {}
                }
            }
        }
    }
""".trimIndent()

fun generateViewModelContent(featureName: String, packagePath: String) = """
    package $packagePath
    
    import ${packagePath.removeSuffix(".screens.${featureName.lowercase()}")}.base.BaseViewModel
    import ${packagePath.removeSuffix(".screens.${featureName.lowercase()}")}.base.DispatcherProvider
        
    class ${featureName}ViewModel (
        dispatcherProvider: DispatcherProvider,
        ) : BaseViewModel<${featureName}UiState, ${featureName}Effect>(
        ${featureName}UiState(),
        dispatcherProvider
        ), ${featureName}InteractionListener {
               
    }
""".trimIndent()

fun generateMapperContent(packagePath: String) = """
    package $packagePath
""".trimIndent()

fun generateEffectContent(featureName: String, packagePath: String) = """
    package $packagePath

    sealed interface ${featureName}Effect {
    
    }
""".trimIndent()

fun generateListenerContent(featureName: String, packagePath: String) = """
    package $packagePath

    interface ${featureName}InteractionListener {
    
    }
""".trimIndent()

fun generateUiStateContent(featureName: String, packagePath: String) = """
    package $packagePath

    data class ${featureName}UiState(val temp: Int = 0)
""".trimIndent()