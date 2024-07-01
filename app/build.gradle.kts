plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.project.wechat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.wechat"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Scalable size unit (support for different screen sizes)
    implementation(libs.intuit.sdp.android)
    implementation(libs.ssp.android)

    // Rounded ImageView
    implementation(libs.roundedimageview)

    // Import the Firebase BoM
    implementation( platform(libs.firebase.bom))

    //firebase cloud messaging and firestore
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)

    //MultiDex
    implementation(libs.multidex)

    // Retrofit
    implementation (libs.retrofit.v2110)
    implementation (libs.converter.gson)
    implementation (libs.okhttp)

}