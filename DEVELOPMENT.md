# Geliştirme Rehberi

## Eklenti Yapısı

CS-Ardac, CloudStream3 eklentisi olarak iki ana provider içerir:

### 1. CsArdacProvider
```kotlin
class CsArdacProvider : MainAPI()
```

Ana işlevler:
- `getMainPage()` - Ana sayfadaki içeriği gösterir
- `search()` - Arama fonksiyonu
- `load()` - İçerik detaylarını yükler
- `loadLinks()` - Video linklerini ve altyazıları çeker

### 2. HDFilmchennemiProvider
```kotlin
class HDFilmchennemiProvider : MainAPI()
```

Aynı işlevleri HDFilmchennemini kaynağı için sağlar.

## Yeni Kaynağı Nasıl Eklerim?

1. `CsArdacProvider.kt` dosyasına yeni bir sınıf ekle:

```kotlin
class NewProviderName : MainAPI() {
    override var mainUrl = "https://example.com"
    override var name = "Example"
    // ... implement required methods
}
```

2. `plugin.json` dosyasında yeni kaynağı ekle:

```json
{
  "name": "example",
  "url": "https://example.com",
  "type": "movies"
}
```

## Kalite Değerleri

```kotlin
Qualities.P1080.value   // 1080p
Qualities.P720.value    // 720p
Qualities.P480.value    // 480p
Qualities.P360.value    // 360p
Qualities.Unknown.value // Bilinmiyor
```

## Altyazı Formatları

Desteklenen formatlar:
- `.vtt` - WebVTT
- `.srt` - SubRip
- `.ass` / `.ssa` - Advanced SubStation Alpha
- `.sub` - MicroDVD

## Hata Ayıklama

Android Studio ile hata ayıklamak için:

```bash
./gradlew installDebug
adb logcat | grep "cs-ardac"
```

## CloudStream3 API

Daha fazla bilgi için: https://github.com/recloudstream/cloudstream/wiki

## Katkı Yapma

1. Fork'la
2. Feature branch oluştur (`git checkout -b feature/AmazingFeature`)
3. Commit'le (`git commit -m 'Add some AmazingFeature'`)
4. Push'la (`git push origin feature/AmazingFeature`)
5. Pull Request aç
