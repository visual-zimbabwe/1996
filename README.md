# 1996

An editorial Android magazine cover gallery built with Kotlin and Jetpack Compose to explore what **1996** felt like through real print magazine covers published that year.

## Features

- **Curated 1996 Exhibition Wall:** Ships with **265 verified 1996 magazine covers** across all 12 months and 10 genres (`News & Culture`, `Music`, `Fashion & Style`, `Film & TV`, `Science & Nature`, `Sports & Auto`, `Tech & Gaming`, `Comics & Art`, `General`, and tagged `Adult (NSFW)`).
- **Live `archive.org` 1996 Search:** Searches Internet Archive's `advancedsearch.php` API strictly scoped to `date:[1996-01-01 TO 1996-12-31]`, with paginated loading (`EXPLORE MORE 1996 COVERS ON ARCHIVE.ORG`).
- **Safe-for-Work Toggle:** Persisted `[ SFW: ON ]` / `[ SFW: OFF ]` typographic toggle filtering both the bundled catalog and live `archive.org` queries.
- **Grid & Index Views:** Switch between a 2-column gallery wall (`[ GRID ]`) and a flush typographic issue list (`[ INDEX ]`), with month (`ALL`, `JAN`–`DEC`) and genre filters.
- **Museum Detail View:** Tap any cover for pinch-to-zoom, double-tap reset, `PREV` / `NEXT` navigation, and a direct `READ ISSUE ON ARCHIVE.ORG` link.

## Design System

Built directly from [DESIGN.md](DESIGN.md) ("Editorial 1996"):
- **Canvas & Inks:** Uniform `#F0F0F0` museum-wall background, `#1A1A1A` primary ink, `#737373` archival metadata ink.
- **Typography:** Bundled `Playfair Display` (Regular & Italic) and `Inter` variable fonts.
- **Zero-Elevation & Sharp Geometry:** `0.dp` corner radii, no drop shadows, no borders, and no decorative icons.

## Cover Data Pipeline

Covers are ingested from Internet Archive (`mediatype:texts`, `collection:magazine_rack` / `periodicals`, excluding `magazine_packs` and `sim_microfilm`) using [scripts/fetch_1996_covers.py](scripts/fetch_1996_covers.py):

```bash
# Refresh and verify all 1996 cover URLs -> app/src/main/assets/covers_1996.json
python3 scripts/fetch_1996_covers.py

# Verify existing catalog integrity
python3 scripts/fetch_1996_covers.py --verify
```

Cover artwork is loaded via `https://archive.org/download/{identifier}/page/cover_w600.jpg` with automatic fallback to `https://archive.org/services/img/{identifier}`.

## Project Structure

```text
.
├── DESIGN.md                           # Editorial 1996 design tokens & specification
├── code.html                           # Original static HTML/Tailwind UI prototype
├── screen.png                          # Reference mockup screenshot
├── scripts/
│   └── fetch_1996_covers.py            # Internet Archive 1996 cover ingestion & verifier
└── app/
    └── src/
        ├── main/
        │   ├── assets/covers_1996.json # Curated 1996 cover catalog
        │   ├── res/font/               # Bundled Playfair Display & Inter TTFs
        │   └── java/app/editorial1996/
        │       ├── MainActivity.kt
        │       ├── data/               # MagazineCover, InternetArchiveApi, CoverRepository
        │       └── ui/                 # GalleryViewModel, GalleryScreen, CoverDetailScreen, theme/
        └── test/java/app/editorial1996/
            └── InternetArchiveApiTest.kt
```

## Build & Test

### Prerequisites
- JDK 17 or 21
- Android SDK (`compileSdk 35`, `minSdk 26`) with `local.properties` (`sdk.dir=...`) or `ANDROID_HOME` set

### Commands

```bash
# Run unit tests
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Build signed release APK (requires app/release-keystore.jks)
./gradlew assembleRelease

# Install onto a connected Android device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

> **Note:** Signing keystores (`*.jks`, `*.keystore`) and `local.properties` are excluded via `.gitignore`. To build a release APK locally, generate `app/release-keystore.jks` with `keytool` and optionally set `KEYSTORE_PASSWORD` / `KEY_PASSWORD`.
