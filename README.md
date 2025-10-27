# 📱 CalbonMobile - EcoFactory

Aplicativo Android Nativo, desenvolvido 100% em **Kotlin**, para o projeto **EcoFactory Calbon**.

Este aplicativo atua como o *client* móvel oficial, consumindo as APIs de backend (Spring Boot) para permitir o gerenciamento de dados [ADICIONE A FUNCIONALIDADE PRINCIPAL AQUI, ex: "de coletas", "de usuários", "de produtos"].

---

## 🚀 Tecnologias Utilizadas

O *stack* principal do projeto é focado no desenvolvimento nativo para a plataforma Android.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-SQUARE-E33F31?style=for-the-badge)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)

*(Nota: Adicione ou remova 'Retrofit' e 'Jetpack Compose' se não estiver usando. Eles são as bibliotecas padrão-ouro para consumo de API e UI, respectivamente. Se você não os está usando, este é um ponto cego de oportunidade técnica a ser explorado.)*

---

## ⚙️ Configuração de Ambiente

Para que o aplicativo possa se comunicar com os servidores de backend, as URLs da API **não devem** ser escritas diretamente no código (*hard-coded*).

O custo de "hard-codar" URLs é a total inflexibilidade. Você não pode testar seu app contra um servidor local (`http://10.0.2.2:8080`) sem recompilar o projeto.

A solução profissional é usar as `buildTypes` do Gradle para gerenciar diferentes ambientes (como `debug` e `release`).

**1. Defina as URLs no seu `build.gradle.kts` (módulo :app):**

```kotlin
android {
    // ...

    buildTypes {
        getByName("debug") {
            // URL para testar contra sua API rodando localmente (10.0.2.2 é o 'localhost' do emulador Android)
            buildConfigField("String", "API_BASE_URL", "\"[http://10.0.2.2:8080/api](http://10.0.2.2:8080/api)\"")
        }
        
        getByName("release") {
            // URLs das suas APIs em produção (Render)
            buildConfigField("String", "API_BASE_URL", "\"[https://api-sql-pdlt.onrender.com/api](https://api-sql-pdlt.onrender.com/api)\"")
            // Ex: buildConfigField("String", "API_MONGO_URL", "\"[https://api-mongo-hi4a.onrender.com/api](https://api-mongo-hi4a.onrender.com/api)\"")
            
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
