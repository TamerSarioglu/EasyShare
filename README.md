# EasyShare - YouTube Downloader

A modern Android application for downloading YouTube videos, built with Clean Architecture principles and Jetpack Compose.

## 🚀 Features

- **YouTube Video Downloads**: Download videos in multiple quality formats (720p, 480p, etc.)
- **Smart Format Selection**: Automatically tries different formats for optimal compatibility
- **Progress Tracking**: Real-time download progress with ETA estimation
- **Download History**: Local database storage with beautiful LazyColumn UI display
- **File Management**: Organized downloads in dedicated EasyShare folder
- **Share Functionality**: Easy sharing of downloaded videos
- **Auto-Updates**: Built-in yt-dlp updater for latest compatibility
- **Permission Handling**: Smart permission management for different Android versions
- **Error Handling**: Comprehensive error messages with helpful suggestions

## 🏗️ Architecture

This project follows **Clean Architecture** principles with clear separation of concerns:

### 📁 Project Structure

```
app/src/main/java/com/tamersarioglu/easyshare/
├── core/
│   ├── constants/
│   │   └── AppConstants.kt          # Centralized constants
│   └── utils/
│       ├── FileUtils.kt             # File operations utilities
│       └── PermissionUtils.kt       # Permission handling utilities
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   └── DownloadHistoryEntity.kt
│   │   ├── dao/
│   │   │   └── DownloadHistoryDao.kt
│   │   └── database/
│   │       └── EasyShareDatabase.kt
│   └── repository/
│       ├── DownloadHistoryRepositoryImpl.kt
│       └── VideoDownloadRepositoryImpl.kt
├── di/
│   ├── DatabaseModule.kt            # Room database DI
│   └── RepositoryModule.kt          # Repository DI
├── domain/
│   ├── model/
│   │   ├── DownloadHistory.kt
│   │   ├── DownloadState.kt
│   │   └── UpdateState.kt
│   ├── repository/
│   │   ├── DownloadHistoryRepository.kt
│   │   └── VideoDownloadRepository.kt
│   └── usecase/
│       ├── CancelDownloadUseCase.kt
│       ├── DeleteDownloadHistoryUseCase.kt
│       ├── DownloadVideoUseCase.kt
│       ├── GetDownloadHistoryUseCase.kt
│       ├── SaveDownloadHistoryUseCase.kt
│       └── UpdateYoutubeDLUseCase.kt
├── presentation/
│   ├── ui/
│   │   ├── components/
│   │   │   ├── ActionButtons.kt
│   │   │   ├── DownloadHistorySection.kt
│   │   │   └── DownloadSection.kt
│   │   └── screen/
│   │       └── MainScreen.kt
│   └── viewmodel/
│       └── MainViewModel.kt
└── MainActivity.kt
```

### 🎯 Architecture Layers

#### **Presentation Layer**
- **UI Components**: Reusable Compose components
- **Screens**: Complete screen implementations  
- **ViewModels**: State management with StateFlow
- **Clean separation** of UI logic from business logic

#### **Domain Layer**
- **Models**: Core business entities (`DownloadState`, `UpdateState`)
- **Use Cases**: Single-responsibility business operations
- **Repository Interfaces**: Abstract contracts for data access
- **Pure Kotlin** with no Android dependencies

#### **Data Layer**
- **Repository Implementations**: Concrete data access implementations
- **Local Database**: Room database for download history persistence
- **External Dependencies**: YouTube DL integration, file system operations

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Async Programming**: Coroutines + StateFlow
- **Video Downloading**: yt-dlp (youtube-dl-android)
- **Build System**: Gradle with Kotlin DSL

## 📱 Screenshots & Usage

### Main Interface
- Clean, intuitive URL input field
- Download/Cancel button with real-time state
- Update button for yt-dlp maintenance
- Progress indicators with ETA display
- Download history in vertical scrolling list

### Download Process
1. **Enter URL**: Paste any YouTube video URL
2. **Download**: Tap download to start the process
3. **Progress**: Watch real-time progress with ETA
4. **Complete**: Access downloaded file via "Open Folder" or "Share"
5. **History**: View and manage downloads in the history section

### Download History
- **Automatic Saving**: All downloads automatically saved to local database
- **LazyColumn Display**: Vertical scrolling list showing recent downloads
- **Rich Information**: Video title, download date, file size
- **Quick Actions**: Open folder, share video, or delete from history
- **Persistent Storage**: History persists across app restarts

## 🔧 Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/easyshare.git
   cd easyshare
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on device/emulator**
   - Connect Android device or start emulator
   - Click "Run" in Android Studio

## 🎨 Key Features Implementation

### Smart Download System
- **Multiple Format Support**: Tries 6 different quality/format combinations
- **Fallback Strategy**: Automatically falls back to lower quality if needed
- **Error Recovery**: Comprehensive error handling with user-friendly messages

### Modern UI/UX
- **Material Design 3**: Latest design system implementation
- **Reactive UI**: StateFlow-based reactive programming
- **Progress Feedback**: Linear progress bars with real-time updates
- **Accessibility**: Proper content descriptions and navigation

### File Management
- **Organized Storage**: Downloads saved to `/Downloads/EasyShare/`
- **File Detection**: Smart file detection and organization
- **Share Integration**: Native Android sharing capabilities
- **History Tracking**: All downloads tracked in local database

### Download History System
- **Room Database**: Local SQLite database for persistent storage
- **Automatic Tracking**: Downloads automatically saved with metadata
- **Rich UI**: Beautiful LazyColumn list with Material Design 3
- **File Operations**: Direct access to open folder or share from history
- **Data Management**: Delete individual history entries

## 🔒 Permissions

The app requests the following permissions:

- **Android 13+**: `READ_MEDIA_VIDEO`
- **Android 10-12**: `READ_EXTERNAL_STORAGE`
- **Android 9 and below**: `READ_EXTERNAL_STORAGE` + `WRITE_EXTERNAL_STORAGE`

### Smart Permission Management
- **Simplified Architecture**: Uses utility class instead of over-engineered repository pattern
- **Version-Aware**: Automatically requests appropriate permissions based on Android version
- **User-Friendly**: Clear explanations when permissions are required
- **Graceful Handling**: App continues to function with limited features if permissions denied

## 🧪 Testing

### Architecture Benefits for Testing
- **Unit Tests**: Each layer can be tested independently
- **Use Case Testing**: Business logic isolated and testable
- **Repository Testing**: Data layer with mockable interfaces
- **Database Testing**: Room database with in-memory testing
- **UI Testing**: Compose UI components are easily testable

### Running Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 🚀 Performance & Optimization

- **Efficient State Management**: StateFlow for reactive UI updates
- **Memory Optimization**: Proper lifecycle management
- **Background Processing**: Coroutines for non-blocking operations
- **Database Optimization**: Room with lazy loading and efficient queries
- **UI Performance**: LazyColumn for efficient scrolling of large datasets
- **Resource Management**: Centralized constants and proper cleanup

## 🌍 Internationalization

The app is **i18n ready** with:
- **Centralized Constants**: All strings in `AppConstants.kt`
- **Format Strings**: Proper string formatting for dynamic content
- **Easy Translation**: Ready for multiple language support

## 📊 Download History

### Local Database Storage
- **Room Database**: SQLite database for persistent storage
- **Automatic Tracking**: Every successful download is automatically saved
- **Rich Metadata**: Stores URL, video title, file path, download date, and file size
- **Data Persistence**: History survives app restarts and updates

### Beautiful UI Display
- **LazyColumn Layout**: Vertical scrolling list below action buttons
- **Material Design 3**: Modern card design with proper elevation and spacing
- **Optimized Layout**: Full-width cards with efficient space utilization
- **Responsive Design**: Adapts to different screen sizes and orientations
- **Empty State**: Elegant message when no downloads exist

### Interactive Features
- **Quick Actions**: Each history item has three action buttons:
  - 📁 **Open Folder**: Opens file manager to download location
  - 📤 **Share**: Native Android sharing for the video file
  - 🗑️ **Delete**: Removes item from history (keeps file on device)
- **Real-time Updates**: UI updates immediately when new downloads complete
- **Formatted Display**: Human-readable dates and file sizes

### Technical Implementation
```kotlin
// Automatic saving on download success
if (state is DownloadState.Success) {
    saveDownloadHistoryUseCase(
        youtubeUrl = url,
        videoTitle = extractedTitle,
        downloadPath = filePath,
        fileSize = calculatedSize
    )
}

// Reactive UI updates
val downloadHistory: StateFlow<List<DownloadHistory>> = 
    getDownloadHistoryUseCase().stateIn(viewModelScope)
```

## 🔄 Updates & Maintenance

### Built-in Update System
- **yt-dlp Updates**: One-tap updates for latest video compatibility
- **Error Suggestions**: Smart suggestions when updates are needed
- **Version Management**: Automatic handling of yt-dlp versions

## 📋 Roadmap

- [x] **Download History**: Track and manage download history ✅
- [ ] **Video Thumbnails**: Extract and display video thumbnails in history
- [ ] **Search & Filter**: Search through download history
- [ ] **Batch Downloads**: Support for multiple video downloads
- [ ] **Playlist Support**: Download entire YouTube playlists
- [ ] **Audio-Only Downloads**: Extract audio from videos
- [ ] **Custom Quality Selection**: Manual quality/format selection
- [ ] **Export History**: Export download history to file
- [ ] **Dark Theme**: Complete dark mode implementation
- [ ] **Localization**: Multi-language support

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Clean Architecture principles
- Use centralized constants for all strings
- Write unit tests for use cases
- Follow Material Design 3 guidelines
- Use proper Git commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **yt-dlp**: Powerful video downloading library
- **youtube-dl-android**: Android wrapper for yt-dlp
- **Jetpack Compose**: Modern Android UI toolkit
- **Room Database**: Local database solution
- **Material Design**: Google's design system
- **Hilt**: Dependency injection framework

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/yourusername/easyshare/issues) page
2. Create a new issue with detailed information
3. Include device information and error logs

---

**Built with ❤️ using Clean Architecture and Jetpack Compose**