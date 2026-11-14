# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Gradit is a single-module Android app for college communication (applicationId and package `com.vsca.vsnapvoicecollege`, Gradle root project `GraditNew`). The same APK serves several roles: principal, teaching staff, students and parents. Each role has its own receiver-side (view) and sender-side (compose and send) features. The code is mostly Kotlin, with some Java in the `AWS/`, `albumImage/`, `VideoAlbum/` packages and the biometric/location attendance screens.

Git flow: day-to-day work happens on `development`, and `master` is the main/PR branch.

## Build & run

Use `gradlew.bat` from PowerShell or cmd, or `./gradlew` from Git Bash.

```
./gradlew assembleDebug              # debug APK
./gradlew installDebug               # install on connected device/emulator
./gradlew assembleRelease            # release APK (no minify/shrink)
./gradlew bundleRelease              # Play Store AAB
./gradlew lint
./gradlew test                       # JVM unit tests
./gradlew connectedAndroidTest       # instrumented tests (device required)
./gradlew test --tests "com.vsca.vsnapvoicecollege.ExampleUnitTest"   # single test class
```

- Toolchain: AGP 9.3.1, JDK 17, compileSdk/targetSdk 37, minSdk 24. Core library desugaring and multidex are enabled.
- The only tests are the generated `ExampleUnitTest` and `ExampleInstrumentedTest` stubs, so there is no real test suite.
- `app/google-services.json` is required (Firebase Messaging and Analytics).
- `app/release/` holds checked-in release outputs (`app-release.apk`/`.aab`).
- **Version bump:** change `versionCode`/`versionName` in `app/build.gradle` **and** the hardcoded `"Version X.Y"` text in `app/src/main/res/layout/splash_rewamp.xml`. Separately, `CommonUtil.VersionId` (an int) is the version ID sent to the `VersionCheck` API.

## Architecture

The app uses a loose MVVM pattern built on Retrofit callbacks and LiveData. There are no coroutines, DI, or Room.

```
Activity  ──calls──▶  ViewModel (ViewModel/)  ──delegates──▶  *Services (Repository/)  ──Retrofit enqueue──▶  ApiInterfaces
   ▲                                                               │
   └──────────── observes LiveData exposed by ViewModel ◀── MutableLiveData.postValue(body or null)
```

- **`Repository/RestClient.kt`** is a static Retrofit singleton with two clients:
  - `apiInterfaces` uses `BASE_URL`, which defaults to `https://www.thegradit.com/mobileapp/`. It is **replaced at runtime** through `changeApiBaseUrl()` with the base URL of the country the user picks in `CountryRewamp`, and that URL is persisted in `SharedPreference` (`getSH_Baseurl`).
  - `resumeApiInterfaces` uses a separate base URL for the Resume Builder. This URL comes from the `VersionCheck` API response field `resumebuilder_baseurl` and is set via `setResumeBaseUrl()`. Don't hardcode either URL.
- **`Repository/ApiMethods.kt`** holds the endpoint path constants (mostly `api/AppDetailsBal/...`). **`Repository/ApiRequestNames.kt`** holds the JSON request key constants. Requests are usually built as a Gson `JsonObject` using these keys.
- **`Interfaces/ApiInterfaces.kt`** is the single Retrofit interface for every endpoint.
- **Services and ViewModels** come in three pairs:
  - `AuthServices` ↔ `ViewModel/Auth`: country list, version check, login, OTP/mobile validation, logout.
  - `DashboardServices` ↔ `ViewModel/Dashboards`: dashboard, user menus, notifications.
  - `AppServices` (very large, ~360 functions) ↔ `ViewModel/App`: everything else.

  Each service method shows `CustomLoading` progress dialogs itself (it takes an `Activity`), checks HTTP 200/201 and then `body.status == 1`, and posts `null` on failure. Callers treat `null` as "something went wrong" and show `CommonUtil.ApiAlert(...)`.
- **Adding an API:**
  1. Add a path constant to `ApiMethods`.
  2. Add the method to `ApiInterfaces`.
  3. Add a response model under `Model/`.
  4. Add the method plus its MutableLiveData/LiveData pair to the relevant `*Services` class.
  5. Expose it in the matching ViewModel's `init()` and a wrapper function.
  6. Observe it in the Activity.

### Base activities

Two base classes coexist:
- **`BaseActivity<VB : ViewBinding>`** is the newer one (~34 screens). It uses ViewBinding via `inflateBinding()` and creates the `App`, `Auth` and `Dashboards` ViewModels in `onCreate`. It also handles logout, user-menu loading, bottom menu, priority-color theming and edge-to-edge.
- **`ActionBarActivity`** is the older one (~18 screens). It uses `layoutResourceId` and has similar shared helpers.

Prefer `BaseActivity` for new screens.

### Global state

`Utils/CommonUtil.kt` is a large `object` of mutable globals, and much screen-to-screen state is passed through it instead of through Intent extras. Examples: logged-in member and college IDs, `Priority`, `MenuListDashboard`, selected recipients, attendance lists and exam-creation buffers. Check these globals when tracing data flow, and clear or reset them where existing screens do.

`Utils/SharedPreference.kt` persists login credentials, country details, base URL and version-check data across launches.

### Roles (`CommonUtil.Priority`)

The login response gives the role as `p1`–`p7`, and it drives menus, colors and API variants throughout the app:
- `p1` is principal and `p7` is principal-like (admin side).
- `p2`, `p3` and `p6` are teaching staff.
- `p4` is student (receiver).
- `p5` is parent. `CommonUtil.isParentEnable` toggles parent targeting.

Many endpoints come in sender and receiver variants, or in principal, staff and tutor variants. For example, `FacultyList`, `FacultyListStaff` and `Fcultylistforreveiver` are separate endpoints. Pick the variant by `Priority`, following the pattern in nearby code.

Menu visibility and read/write permission come from the `UserMenus` API (`menu_slug`, `is_read_enabled`, `is_write_enabled`). These values are stored in `CommonUtil.MenuListDashboard`.

### Launch / auth flow

1. **`SplashRewamp`** is the launcher. It runs the version check, which may force an update and also supplies the resume base URL.
2. If no base URL is saved, the app goes to **`CountryRewamp`**, where the user picks a country and that country's base URL is set.
3. The user then logs in through **`LoginRewamp`** or **`MobileNumberRewamp`**, followed by **`OtpRewamp`**, **`PasswordRewamp`** or **`CreatePasswordRewamp`**.
4. Users with multiple roles see **`LoginRolesRewamp`**.
5. Everyone lands on **`DashBoardActivityRewamp`**, which shows the menu grid (`DashboardParent`/`DashboardChild` adapters).

Screens with a `Rewamp` suffix are the current versions, and commented-out non-Rewamp entries in the manifest are legacy.

### Packages

- `Activities/`: receiver/view screens. Subpackages: `ResumeBuilder/` (profile, academic, skill-set editing, build and preview resume), `FilePreviewActivity/`.
- `ActivitySender/`: compose and send screens. Examples are `AddAssignment`, `AddExamination`, `AddTextNoticeboard`, `CommunicationVoice` and `AddVideo`.
  - Recipient selection lives in `AddRecipients`, `PrincipalRecipient`, `HeaderRecipient` and `SpecificStudent`. These are huge files, so edit them surgically.
  - Biometric and geo-fenced staff attendance is in `PunchStaffAttendanceUsingFinger`, `AddLocationForAttendance` and `StaffWiseAttendanceReports`.
- `Model/` and `SenderModel/`: Gson data classes. Their names are inconsistent (`DataX`, `DataXX`… are auto-generated), so match the existing class rather than creating near-duplicates.
- `Adapters/`: RecyclerView adapters. Click callbacks go through small listener interfaces in `Interfaces/`.
- `AWS/`: file uploads. `AwsUploadingPreSigned` fetches a presigned URL from `get-s3-presigned-url` (via `RestClient.client`) and PUTs the file. The S3 bucket is `gradit-communication` and the key path is `<date>/<instituteId>/`. Upload endpoints then receive the cloud URL (the `...WithCloudURL` APIs).
- `FCM/`:
  - `MyApp` is the Application class.
  - `MyFirebaseMessagingService` handles push notifications.
  - `NotificationCallScreen` and `RingtonePlayer` implement full-screen "voice call" style announcements.
- `Utils/`:
  - `CommonUtil` holds globals and helpers.
  - `PermissionManager` handles runtime permissions.
  - `VimeoUploader` uploads videos to Vimeo.
  - There are also download, PDF and WebView helpers and `EdgeToEdgeUtils`.
- `albumImage/` and `VideoAlbum/`: custom image and video pickers (Java).

### UI conventions

- New screens use ViewBinding (`viewBinding { enabled = true }`).
- Glide is the main image library, and Picasso is also present.
- The app handles edge-to-edge for targetSdk 35+ (see `EdgeToEdgeUtils`, `BaseActivity`, and `res/layout-v35`).
