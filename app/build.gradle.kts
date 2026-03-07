plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
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
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Splash Api
    implementation(libs.androidx.core.splashscreen)
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

    @Composable
    fun ${featureName}Screen(viewModel: ${featureName}ViewModel) {

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

fun generateViewModelContent(featureName: String, packagePath: String,) = """
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

    sealed interface ${featureName}Effect
""".trimIndent()

fun generateListenerContent(featureName: String, packagePath: String) = """
    package $packagePath

    interface ${featureName}InteractionListener
""".trimIndent()

fun generateUiStateContent(featureName: String, packagePath: String) = """
    package $packagePath

    data class ${featureName}UiState(val temp: Int = 0)
""".trimIndent()