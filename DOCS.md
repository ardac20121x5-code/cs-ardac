# CloudStream3 Eklentisi: CS-Ardac

## 📺 Nedir?

CS-Ardac, CloudStream3 video oynatıcısı için **Dizipal** ve **HDFilmchennemini** kaynaklarından film ve dizi izlemenize olanak tanıyan bir eklentidir.

## ✨ Özellikler

| Özellik | Durum |
|---------|-------|
| Film Oynatma | ✅ Dizipal, HDFilmchennemini |
| Dizi Oynatma | ✅ Dizipal |
| Arama | ✅ Film/Dizi adı ile arama |
| Kalite Seçimi | ✅ 1080p, 720p, 480p, 360p |
| Altyazı | ✅ Türkçe, İngilizce |
| Dublaj | ✅ Türkçe dublaj, Orijinal ses |
| Ana Sayfa | ✅ Popüler, Son Eklenen |

## 🚀 Kurulum

### CloudStream3'e Eklenti Ekleme

1. **CloudStream3 uygulamasını aç**
2. **Ayarlar (Settings)** → **Eklentiler (Extensions)** → **Depo Ekle (Add Repository)**
3. Şu URL'yi yapıştır:
   ```
   https://github.com/ardac20121x5-code/cs-ardac
   ```
4. **cs-ardac** eklentisini bul ve **İndir (Download)** düğmesine tıkla
5. Eklentiyi etkinleştir ve yeniden başlat

## 💻 Kaynaklar

### Dizipal
- **URL**: https://dizipal.com
- **İçerik**: Türk dizileri, filmler
- **Kaliteler**: 1080p, 720p, 480p, 360p
- **Altyazılar**: Türkçe, İngilizce
- **Ses**: Türkçe dublaj, orijinal

### HDFilmchennemini
- **URL**: https://hdfilmchennemini.com
- **İçerik**: Türkçe filmler
- **Kaliteler**: 1080p, 720p, 480p
- **Altyazılar**: Türkçe
- **Ses**: Türkçe dublaj

## 🔧 Geliştiriciler İçin

### Kurulum
```bash
git clone https://github.com/ardac20121x5-code/cs-ardac.git
cd cs-ardac
```

### Derleme
```bash
./gradlew build
```

### Dosya Yapısı
```
cs-ardac/
├── src/
│   └── main/
│       ├── kotlin/com/ardac/
│       │   ├── CsArdacProvider.kt      # Ana provider
│       │   ├── CsArdacPlugin.kt        # Plugin config
│       │   └── TurkishExtractors.kt    # Extractor utilities
│       └── AndroidManifest.xml
├── build.gradle
├── settings.gradle.kts
├── plugin.json
└── README.md
```

## 📝 Lisans

MIT

## 👤 Yazar

**ardac20121x5-code**

## 🐛 Sorun Bildir

Herhangi bir sorun veya öneriniz varsa, [Issues](https://github.com/ardac20121x5-code/cs-ardac/issues) bölümünde bir sorun açın.

## ⚠️ Yasal Uyarı

Bu eklenti sadece eğitim amaçlı oluşturulmuştur. Telif hakkıyla korunan içeriği izlemek için yasalara uyun.

---

**Sürüm**: 1.0.0  
**Son Güncelleme**: 2026-08-14  
**CloudStream3 Uyumluluğu**: v4.8.0+
