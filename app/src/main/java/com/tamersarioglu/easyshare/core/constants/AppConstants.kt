package com.tamersarioglu.easyshare.core.constants

object AppConstants {
    
    // App Info
    const val APP_TITLE = "EasyShare - YouTube Downloader"
    const val FOLDER_NAME = "EasyShare"
    
    // UI Strings
    const val ENTER_VIDEO_URL = "Enter Video URL"
    const val DOWNLOAD = "Download"
    const val CANCEL = "Cancel"
    const val UPDATE = "Update"
    const val OPEN_FOLDER = "Open Folder"
    const val SHARE = "Share"
    const val SHARE_VIDEO = "Share Video"
    
    // Status Messages
    const val INITIALIZING_DOWNLOAD = "Initializing download..."
    const val DOWNLOADING_PROGRESS = "Downloading... %.1f%% ETA: %s"
    const val CALCULATING_ETA = "Calculating..."
    const val DOWNLOAD_SUCCESSFUL = "Download Successful!"
    const val DOWNLOAD_FAILED = "Download Failed!"
    const val DOWNLOAD_CANCELLED = "Download Cancelled"
    const val SAVED_TO = "Saved to: %s"
    
    // Update Messages
    const val YTDLP_UPDATED_SUCCESS = "yt-dlp updated successfully!"
    const val YTDLP_UPDATE_FAILED = "Failed to update yt-dlp"
    const val UPDATE_SUGGESTION = "💡 Try clicking 'Update' to get the latest yt-dlp version"
    
    // Permission Messages
    const val STORAGE_PERMISSION_REQUIRED = "Storage permissions are required to download videos to your device"
    
    // Error Messages
    const val VIDEO_FILE_NOT_FOUND = "Video file not found"
    const val ERROR_PREFIX = "Error: %s"
    const val ERROR_SHARING_PREFIX = "Error sharing: %s"
    const val FILE_SAVED_TO = "File saved to: Downloads/EasyShare/%s"
    
    // Download Error Messages
    const val YOUTUBE_EXTRACTION_FAILED = "YouTube extraction failed. Try updating the app or the video might be restricted. Error: %s"
    const val VIDEO_UNAVAILABLE = "Video is unavailable or has been removed."
    const val PRIVATE_VIDEO = "Cannot download private videos."
    const val AGE_RESTRICTED_VIDEO = "Age-restricted video. Cannot download without authentication."
    const val VIDEO_NOT_AVAILABLE_REGION = "Video is not available in your region or has been removed."
    const val DOWNLOAD_ERROR_GENERIC = "Download error: %s"
    
    // Log Messages
    const val TRYING_FORMAT = "Trying format: %s (attempt %d/%d)"
    const val DOWNLOAD_SUCCESSFUL_FORMAT = "Download successful with format: %s"
    const val DOWNLOAD_CANCELLED_BY_USER = "Download cancelled by user"
    const val FORMAT_FAILED_EXIT_CODE = "Format %s failed (exit code: %d), trying next option..."
    const val FORMAT_FAILED_EXCEPTION = "Format %s failed with exception: %s"
    const val DOWNLOAD_FAILED_ALL_FORMATS = "Download failed with all format options. Exit code: %d. Output: %s"
    const val DOWNLOAD_ERROR_ALL_FORMATS = "Download error with all format options"
    const val YTDLP_UPDATED_LOG = "yt-dlp binary updated successfully"
    const val YTDLP_UPDATE_FAILED_LOG = "Failed to update yt-dlp binary"
    const val FOUND_FILE_EASYSHARE = "Found file in EasyShare folder: %s"
    const val MOVED_FILE_TO_EASYSHARE = "Moved file from root Downloads to EasyShare: %s"
    const val COULD_NOT_MOVE_FILE = "Could not move file, using original location: %s"
    const val ERROR_MOVING_FILE = "Error moving file, using original location: %s"
    
    // File and Directory Constants
    const val DOWNLOADS_EASYSHARE_URI = "content://com.android.externalstorage.documents/document/primary:Download/EasyShare"
    const val DOWNLOADS_URI = "content://com.android.externalstorage.documents/document/primary:Download"
    const val VIDEO_MIME_TYPE = "video/*"
    const val DIRECTORY_MIME_TYPE = "vnd.android.document/directory"
    
    // YouTube DL Options
    const val OUTPUT_TEMPLATE = "%s/%%(title)s.%%(ext)s"
    const val RESTRICT_FILENAMES = "--restrict-filenames"
    const val FORMAT_OPTION = "-f"
    const val EXTRACTOR_ARGS = "--extractor-args"
    const val EXTRACTOR_ARGS_VALUE = "youtube:player_client=android,web"
    const val NO_CHECK_CERTIFICATES = "--no-check-certificates"
    const val USER_AGENT = "--user-agent"
    const val USER_AGENT_VALUE = "Mozilla/5.0 (Linux; Android 11; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
    const val COMPAT_OPTIONS = "--compat-options"
    const val COMPAT_OPTIONS_VALUE = "no-youtube-channel-redirect"
    const val EXTRACTOR_RETRIES = "--extractor-retries"
    const val EXTRACTOR_RETRIES_VALUE = "3"
    const val SOCKET_TIMEOUT = "--socket-timeout"
    const val SOCKET_TIMEOUT_VALUE = "30"
    const val NEWLINE = "--newline"
    const val NO_WARNINGS = "--no-warnings"
    const val IGNORE_ERRORS = "--ignore-errors"
    const val OUTPUT_OPTION = "-o"
    
    // Video Format Options
    val VIDEO_FORMAT_OPTIONS = listOf(
        "best[height<=720][ext=mp4]",
        "best[height<=480][ext=mp4]",
        "worst[ext=mp4]",
        "best[height<=720]",
        "best[height<=480]",
        "worst"
    )
    
    // Error Detection Strings
    const val EXTRACTION_FAILED_KEYWORD = "extraction failed"
    const val PLAYER_RESPONSE_KEYWORD = "player response"
    const val FAILED_TO_EXTRACT_PLAYER_RESPONSE = "Failed to extract any player response"
    const val VIDEO_UNAVAILABLE_KEYWORD = "Video unavailable"
    const val PRIVATE_VIDEO_KEYWORD = "Private video"
    const val SIGN_IN_CONFIRM_AGE = "Sign in to confirm your age"
    const val VIDEO_NOT_AVAILABLE_KEYWORD = "This video is not available"
    
    // Log Tags
    const val TAG_VIDEO_DOWNLOAD_REPOSITORY = "VideoDownloadRepository"
    
    // Time Constants
    const val FILE_SEARCH_TIME_WINDOW_MS = 60000L // 1 minute
    const val UPDATE_MESSAGE_DISPLAY_TIME_MS = 3000L // 3 seconds
}