# OpenSubtitles API Integration Guide

## Overview
I've successfully added OpenSubtitles API (v3) integration to your TripleRockTV project. This allows you to query subtitles from OpenSubtitles.com with two different search methods.

## Files Created/Modified

### 1. OpenSubtitlesService.kt (NEW)
**Location:** `app/src/main/kotlin/com/joshgm3z/triplerocktv/repository/retrofit/OpenSubtitlesService.kt`

This is the Retrofit service interface that defines the OpenSubtitles API endpoints:

- **searchSubtitles()** - Search by query string (movie/show name)
- **searchSubtitlesByImdbId()** - Search by IMDb ID

**Response Models:**
- `OpenSubtitlesSearchResponse` - Main response wrapper
- `OpenSubtitlesSubtitle` - Individual subtitle metadata
- `OpenSubtitlesAttributes` - Detailed subtitle information including:
  - Language
  - Download count
  - Hearing impaired flag
  - HD flag
  - Rating
  - Release name
  - Upload date
  - Vote count
  - Download URL

### 2. SubtitleRepositoryImpl.kt (UPDATED)
**Location:** `app/src/main/kotlin/com/joshgm3z/triplerocktv/repository/impl/SubtitleRepositoryImpl.kt`

Added two new methods to support OpenSubtitles:

#### Method 1: Search by Query
```kotlin
suspend fun findSubtitlesFromOpenSubtitles(
    query: String,
    apiKey: String,
    language: String = "en"
): List<SubtitleData>
```

**Parameters:**
- `query`: Search string (e.g., "The Matrix", "Breaking Bad")
- `apiKey`: Your OpenSubtitles API key (get from https://www.opensubtitles.com/api)
- `language`: Language code (default: "en" for English)

#### Method 2: Search by IMDb ID
```kotlin
suspend fun findSubtitlesFromOpenSubtitlesById(
    imdbId: String,
    apiKey: String,
    language: String = "en"
): List<SubtitleData>
```

**Parameters:**
- `imdbId`: IMDb ID without "tt" prefix (e.g., "0133093" for The Matrix)
- `apiKey`: Your OpenSubtitles API key
- `language`: Language code (default: "en")

Both methods include error handling and return an empty list if the request fails.

## How to Use

### Setup
1. Register at https://www.opensubtitles.com/ to get an API key
2. Store the API key securely (consider using BuildConfig or SharedPreferences)

### Example Usage in Your Code
```kotlin
// Inject the repository
@Inject
lateinit var subtitleRepository: SubtitleRepository

// Cast to implementation to access the new methods
val impl = subtitleRepository as SubtitleRepositoryImpl

// Search by query
val subtitles = impl.findSubtitlesFromOpenSubtitles(
    query = "The Matrix",
    apiKey = "your_api_key_here",
    language = "en"
)

// Search by IMDb ID
val subtitles = impl.findSubtitlesFromOpenSubtitlesById(
    imdbId = "0133093",
    apiKey = "your_api_key_here",
    language = "en"
)
```

## API Endpoints
- **Base URL:** https://api.opensubtitles.com/
- **Search Endpoint:** `GET /search/subtitles`

## Query Parameters
- `query` - Search string
- `imdb_id` - IMDb ID (without "tt" prefix)
- `languages` - Comma-separated language codes (e.g., "en", "en,fr", etc.)
- `type` - "movie" or "episode" (optional)

## Headers Required
- `Api-Key` - Your OpenSubtitles API key (required)

## Important Notes

1. **API Key:** You need to obtain an API key from OpenSubtitles.com. Free tier accounts have rate limits.

2. **Language Codes:** Standard ISO 639-3 codes:
   - `en` - English
   - `es` - Spanish
   - `fr` - French
   - `de` - German
   - `pt` - Portuguese
   - etc.

3. **Error Handling:** Both methods include try-catch blocks that return an empty list on error.

4. **Response Mapping:** Subtitle results are converted to `SubtitleData` objects for consistency with the existing codebase.

5. **Supported Formats:** OpenSubtitles supports .srt, .vtt, .ssa, .sub formats.

## Next Steps

1. Store your OpenSubtitles API key securely (e.g., in local.properties or a secrets management service)
2. Update your ViewModel/UI to use the new methods
3. Consider adding a preference/setting for language selection
4. Implement caching if needed to reduce API calls

## Dependencies
All required dependencies (Retrofit, Gson) are already present in your project's build.gradle.kts.

