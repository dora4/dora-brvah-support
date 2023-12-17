plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("kotlin-kapt")
}

android {
    namespace = "dora.brvah"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.dora4:dora:1.1.46")
    api("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.10")
}

afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class) {
                from(components["release"])
                groupId = "com.github.dora4"
                artifactId = "dora-brvah-support"
                version = "1.2"
            }
        }
    }
}