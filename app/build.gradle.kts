plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("jacoco")
}

import java.util.Properties

android {
    namespace = "com.example.vinylstore"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.vinylstore"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties().apply {
                    keystorePropertiesFile.inputStream().use { load(it) }
                }
                
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile") ?: "")
                storePassword = keystoreProperties.getProperty("storePassword") ?: ""
                keyAlias = keystoreProperties.getProperty("keyAlias") ?: ""
                keyPassword = keystoreProperties.getProperty("keyPassword") ?: ""
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "/META-INF/*.md"
            excludes += "/META-INF/*.txt"
        }
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation("androidx.compose.material:material-icons-extended")
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.15")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

//configuración de jacoco para coverage
jacoco {
    toolVersion = "0.8.11"
}

//configuración de coverage para tests unitarios
tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

//tarea para generar reporte de coverage de tests unitarios
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/ui/theme/**",
        "**/ui/screens/**"
    )
    
    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree("${project.buildDir}") {
        include("jacoco/testDebugUnitTest.exec")
    })
}

//tarea para generar reporte de coverage de tests de instrumentación
tasks.register<JacocoReport>("jacocoAndroidTestReport") {
    dependsOn("connectedDebugAndroidTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    
    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    
    //obtener archivos de coverage de instrumentación
    val coverageFiles = fileTree("${project.buildDir}/outputs/code_coverage") {
        include("**/*.ec")
    }
    executionData.setFrom(coverageFiles)
}

//tarea para combinar coverage de tests unitarios e instrumentación
tasks.register<JacocoReport>("jacocoCombinedTestReport") {
    dependsOn("testDebugUnitTest")
    //usar mustRunAfter en lugar de dependsOn para que no falle si no hay dispositivo
    //pero si se ejecuta, debe ejecutarse después de connectedDebugAndroidTest
    mustRunAfter("connectedDebugAndroidTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    
    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    
    //combinar execution data de tests unitarios e instrumentación
    val unitTestExecutionData = fileTree("${project.buildDir}") {
        include("jacoco/testDebugUnitTest.exec")
    }
    val androidTestCoverageFiles = fileTree("${project.buildDir}/outputs/code_coverage") {
        include("**/*.ec")
    }
    
    //declarar explícitamente que usa el output de connectedDebugAndroidTest
    inputs.files(androidTestCoverageFiles).optional()
    
    //si hay archivos de coverage de instrumentación, combinarlos
    //si no, solo usar coverage de tests unitarios
    if (androidTestCoverageFiles.files.isNotEmpty()) {
        executionData.setFrom(unitTestExecutionData, androidTestCoverageFiles)
    } else {
        executionData.setFrom(unitTestExecutionData)
    }
}

//tarea para ejecutar todos los tests y generar reporte combinado
//esta tarea NO hace dependsOn de connectedDebugAndroidTest porque requiere dispositivo
//pero puede ser usada después de ejecutar manualmente los tests de instrumentación
tasks.register("runAllTestsWithCoverage") {
    description = "Ejecuta tests unitarios y genera reporte combinado (requiere ejecutar androidTest manualmente primero)"
    group = "verification"
    dependsOn("testDebugUnitTest", "jacocoCombinedTestReport")
    
    doLast {
        println("==========================================")
        println("Tests unitarios ejecutados")
        println("Para incluir tests de instrumentación:")
        println("1. Ejecuta: ./gradlew :app:connectedDebugAndroidTest")
        println("2. Luego ejecuta: ./gradlew :app:jacocoCombinedTestReport")
        println("3. Reporte en: app/build/reports/jacoco/jacocoCombinedTestReport/html/index.html")
        println("==========================================")
    }
}