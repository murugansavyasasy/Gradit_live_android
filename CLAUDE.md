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

### Dashboard (`DashBoardActivityRewamp`)

- **Loading:**
  - `onResume` → `loadDashboard()` clears `CommonUtil.MenuListDashboard`, then sends `UserMenuRequest` (`GetParentUserMenuWithReadWriteAccess`, observed in `BaseActivity`) and `DashBoardRequest` (`DashboardApi_Live`) **in parallel**.
  - `isDashboardLoaded` stops it reloading on later `onResume`s.
  - The first-time permission-granted path waits instead: `UserMenuRequest(this) { DashBoardRequest() }`.
  - Don't make the normal path wait for the menu API. `DashboardServices.GetUsermenu` posts nothing on HTTP 400/404/500, an empty body, or status ≠ 1, so the callback may never fire and the dashboard would never load.
  - If the dashboard answers first, the menu section is built empty. Then `onMenusLoaded()` (if the dashboard is already drawn) or `bindAdapter()` (if it is still being built, see `isBuildingDashboard`) fills it via `fillEmptyMenuSection()`. Log tag `DashboardMenuGrid`.
- **Sections:**
  - `processDashboardData()` maps each response `type` to a `DashboardOverall`.
  - A fake `"DashBoard_Menu"` section (the menu grid) is inserted at index 1.
  - Sections are matched by display text (`"Upcoming Events"`, `"Notice Board"` …).
- **Menu grid:**
  - `DashboardParent.bindDashboardMenu` hides Home (id 1) and pages the rest with `DashboardMenuPagerAdapter` (12 per page, 4×3). Each page is a `DashboardChild` with `type = "Menu"`.
  - Icons are chosen by menu **id** in `DashboardChild.bindMenu`. An unknown id gives a blank cell.
- **Redirects:**
  - `DashboardParent.ParticularMenuClick` is a hardcoded id → screen switch. It copies `is_read_enabled` / `is_write_enabled` into `CommonUtil.menu_read*` / `menu_write*` and launches only if that menu's `CommonUtil.Menu*` flag is true.
  - Screens call `CommonUtil.OnMenuClicks("<self>")` so they don't relaunch themselves.
  - Section "View all" links, cards, `NotificationAdapter` and `FacultyAdapter` also hardcode menu ids. Update all of these when menus change.

### Dashboard menu order (Sort, saved per user)

- **UI:**
  - The menu section has a "Menu" header with a **Sort** button where "View all" is on other sections.
  - Sort starts sorting mode: Cancel + Done, a hint line, dashed tiles (`bg_menu_edit_tile`), and a tap does not open the menu.
  - Long-press lifts a menu. The others slide aside live as it moves, on the same page or across pages; holding near the left/right edge flips the page.
  - **Done** saves; **Cancel** discards.
  - While sorting, back press or a tap outside the menu row shows a confirmation (Save / Discard / Keep editing, or Done / Keep editing if nothing changed). See `DashBoardActivityRewamp.dispatchTouchEvent` / `onBackPressed`.
- **Classes:**
  - `Utils/MenuOrderPreference`: storage and sync.
  - `Adapters/MenuDragController`: drag shadow, hover moves, edge page switch.
  - `DashboardParent`: sorting state, `moveMenu` (with a 250 ms pause between moves), `saveMenuEdit` / `cancelMenuEdit`.
  - `DashboardMenuPagerAdapter.updateMenus` and `DashboardChild.updateMenuListAnimated`: per-page DiffUtil animation.
- **Storage:**
  - Kept only on the phone, never sent to an API, in its own SharedPreferences file `MenuOrderPref`.
  - The key is `local_menu_order_list_<CollegeId>_<MemberId>_<Priority>`, so each role/college login (`LoginFromAppNew` `login_data`) has its own list.
  - Each entry is a `LocalMenuOrderItem` (`menu_id`, `menu_name`, `menu_slug`, `local_menu_order_no`).
  - Only the order comes from storage; read/write access always comes from the latest API response.
  - `CommonUtil.MenuListDashboard` stays in API order.
- **Display order:** menus are sorted by `local_menu_order_no`.
  - The first time, it is the position in the API response; the API's `order_id` is **not** used.
  - On every menu API response, `BaseActivity` calls `MenuOrderPreference.syncWithApi()`:
    1. menus no longer sent are removed;
    2. new menus are appended at the end;
    3. the numbers are rewritten 1..n.
  - An empty API list changes nothing.
- **Logout:** `SharedPreference.clearShLogin()` (used by every logout path) also calls `MenuOrderPreference.clearAll()`, which clears every user's saved order.
- **Logs:** tags `MenuOrder` (sync/save/clear) and `MenuOrderEdit` (sort start/end, drag, moves, Done/Cancel, dialog choice).

### Resume Builder (`Activities/ResumeBuilder/`, dashboard menu id 23)

- **Backend:**
  - A separate server, reached through `RestClient.resumeApiInterfaces`.
  - Its base URL comes from the `VersionCheck` field `resumebuilder_baseurl` and is saved as SharedPreferences key `resumebuilderbaseurl` in `Mypref`. Both `SplashRewamp` and `CountryRewamp` write it.
  - Every resume service in `AppServices` first reads that key. If it is blank, it posts `null` and returns. Otherwise it calls `RestClient.setResumeBaseUrl()`. New resume APIs must keep this guard, because `RESUME_URL` starts as `""`.
  - `clearShLogin()` (logout) wipes `Mypref` and the login screens don't call `VersionCheck`, so resume screens stay empty after logout → login until the app restarts through Splash.
  - Placement screens (`PlacementEvent`, `CareerTraining`) use the same client and base URL.
- **Where data lives:**
  - **Server only**, keyed by `idMember` (`CommonUtil.MemberId`): profile, academic records, skill set, saved resumes (`{title, url, placementOfficer}`).
  - Generated PDFs are built on the server and stored in S3.
  - There is no local database. SharedPreferences holds only the URL.
- **Screen hand-off through `CommonUtil` globals:**
  - `ResumeBuilder` fills them just before navigating: `saveBasicDetails`, `saveAcademicDetails`, `isSkillSetDataSending`, then `BuildResumeActivity` fills `fullResumeData`.
  - Some values are read from on-screen labels, so the `"-"` placeholder (`CommonUtil.isIffin`) can leak through.
  - The globals are lost if the process is killed (`!!` reads then crash). Only the Resume Builder package uses them.
  - `CommonUtil.SelcetedFileList` is shared with about 9 sender screens; clear it before reusing.
- **Flow:**
  - `ResumeBuilder` (`get-profile`, `get-academic`, `get-skillset`, `profileresume`) leads to:
    - **Edit 1** `EditBasicDetails` → `add-edit-profile` (always sends `placementStatus "Placed"`)
    - **Edit 2** `EditAcademicDetails` → `add-edit-academic`
    - **Edit 3** `EditSkillSet` → `add-edit-skillset`
    - **Build** `BuildResumeActivity` (tick what to include) → `BuildMyResume` (template = grid position + 1, colour) → `resume/post-resume` (`bucketPath` is hardcoded `CommonUtil.isRBBucketPath`) → `ResumePreviewActivity`
  - `ResumePreviewActivity` modes (`ScreenName`): `BuildMyResume`, `UploadResume`, `MyResumes`. Save title → `saveTitleResume`; delete → `delete/studentresume/{id}`.
- **Profile photo on Build My Resume:**
  - The "Personal information" section in `BuildResumeActivity` shows `saveBasicDetails.memberImagePath` (Glide `circleCrop`).
  - Empty URL: dashed circle, "No photo uploaded", checkbox disabled. Bad URL or failed load: placeholder, "Photo could not be loaded", checkbox disabled. Loaded: ticked by default.
  - The ticked URL goes into `ResumeContextData.profile` and is sent as `context.profile` in the generate request (`""` when not included). Log tag `ResumeProfilePhoto`.

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
- **All app themes (`Prinipal`, `Staff`, `Student`, `Parent`… set by `CommonUtil.SetTheme`) extend `Theme.AppCompat`.** Material Components that enforce a MaterialComponents theme crash at inflate time (`MaterialCardView` did, which is why it was replaced). Use `androidx.cardview.widget.CardView` and plain `android.app.Dialog`s (styled as bottom sheets) instead of `MaterialCardView` or `BottomSheetDialog`. `ShapeableImageView` is fine.
- Avoid Kotlin `when` guard conditions (`X if cond ->`); they need a newer Kotlin than some teammates use.
- The app handles edge-to-edge for targetSdk 35+ (see `EdgeToEdgeUtils`, `BaseActivity`, and `res/layout-v35`).
- Screen header bars are not a shared component. Each layout defines its own header with a `lblMenuTitle` TextView (white, bold, usually `@dimen/thirteen`), so a header style change has to be made in each layout file separately. `add_image_new.xml` (Add Assignment) sets its title to `13sp` directly (it was `17sp` before).
- `ActivitySender/AddAssignment` (layout `add_image_new.xml`, used only by this screen) has three modes, chosen by the `ScreenName` intent extra:
  - New assignment (the default).
  - `CommonUtil.Forward_Assignment`: forwarding an existing assignment. The title and type are locked, and the description and upload views are hidden.
  - `"Assignment Submit"`: a student submitting work. The date, type and title are hidden, and the files are uploaded to S3 and then submitted.

  For new and forwarded assignments, the next screen depends on `Priority`: `p7` goes to `HeaderRecipient`, `p1` to `PrincipalRecipient`, and `p2`/`p3` to `AddRecipients`.

## Events & venue booking

### Menu flags
`GetParentUserMenuWithReadWriteAccess` returns `approval_required`, `isapprove` and `isrequest` per menu. These are stored on `MenuDetailsResponse` (nullable) and copied into `CommonUtil.MenuListDashboard` by `BaseActivity`. Read them null-safely with `CommonUtil.isMenuApprovalRequired(id)`, `isMenuApprove(id)` and `isMenuRequest(id)`. Events is menu id **8**. Login's `CommonUtil.approval_required` is **not** used for Events.
- **`approval_required`** (→ `CommonUtil.menu_approvalEvent`): shows the **Request** button on `Events`, and makes `AddEvents` run Check Venue Availability before recipients.
- **`isrequest` / `isapprove`** (→ `menu_isRequestEvent` / `menu_isApproveEvent`): decide the `VenueRequestList` sections. With both: a My Requests | Approve Event switch. With only `isapprove`: an "Approve Event" screen. Otherwise: My Requests only.

### Events → Details → AddEvents
- **Data flow:**
  - Tapping an Upcoming event sets `CommonUtil.EventParticulerId` (event id) and `EventCreatedby`, and passes the event as the `"EventsData"` intent extra (`GetEventDetailsData`, which includes `starttime`/`endtime`/`displaytime`/`venueid`/`bookingid`).
  - `EventsViewDetails` shows Edit only when `CommonUtil.EventEdit == "Edit"` (Upcoming tab) **and** `MemberId == EventCreatedby`.
  - Edit opens `AddEvents` with extras `"EventsData"` and `"EventEdit" = "EventEdit"`.
- **`AddEvents` has 3 modes** (`"EventEdit"` extra):
  - **null — new event:** full validation, then venue availability (if `approval_required`), then recipient screens.
  - **`"EventEdit"` — edit event:** only title and description are editable, and venue/date/times are locked. Sends `ManageEvents` `processtype "edit"` directly (never opens a recipient screen). `event_date` may be `dd/MM/yyyy` or `dd MMM yyyy`, handled by `toDdMmYyyy()`.
  - **`"VenueRequestEdit"` — edit a waiting request from My Requests:** every field is editable. Sends `UpdateVenueRequest`, then `RESULT_OK` back to the list.
- **Venue UI:** the `GetVenueList` dropdown and Quick chips (`CommonUtil.QuickVenueCount`), time presets (`CommonUtil.EventTimePresets`, past ones hidden for today), From/To + Duration, and the `CheckVenueAvailability` popup.
- **Send requests:** every `ManageEvents` / `ManageEventsFromTutor` builder (`AddRecipients`, `PrincipalRecipient`, `HeaderRecipient`, `SpecificStudent`, `AddEvents.editSendEvents`) calls `CommonUtil.addEventScheduleFields(json)`. This adds `starttime`/`endtime` ("4:30 pm") and `venueid`. The venue name still goes as `eventvenue`. The old single `eventtime` key is no longer sent; its lines are commented out in all these builders, so don't add it back. Don't change the existing `eventid "0"` lines in the tutor builders.
- **Time display:**
  - Upcoming and Past share `EventsAdapter`. Its `eventTimeText()` shows `starttime - endtime`, falling back to `displaytime`, then `starttime`, then `event_time`.
  - `EventsViewDetails` shows `displaytime`, falling back to `event_time`.
- **Dashboard "Upcoming Events" card** (`DashBoardActivityRewamp` → `DashboardChild.bindUpcomingEvents`):
  - `DashboardApi_Live` sends `venue`/`starttime`/`endtime`, which are copied onto `DashboardSubItems` (`EventVenue`/`EventStartTime`/`EventEndTime`).
  - Blank or null values hide their row. Times are formatted with the safe `formatEventTime()`, never `LocalTime.parse` (`eventtime` can be `""`).
  - The event constructor's parameter names are swapped: `EventTitle` holds the date, `Eventdate` the time, and `Eventtime` the topic.

### VenueRequestList (Events → Request button)
- **My Requests:** `GetVenueRequestList`; ⋮ on Waiting cards → Edit (`AddEvents` request-edit) or Cancel (`CancelVenueRequest`, with confirmation). On cancel success the list is updated locally (no reload) and the screen switches to the Cancelled tab.
- **Approve Event:** `GetVenueApprovalList`. Cards show the requester and Reject/Approve (by `canreject`/`canapprove`).
  - Approve sheet → `ApproveVenueRequest` → "Request approved" sheet, then reload.
  - Reject dialog (reason required, max 250) → `RejectVenueRequest` → Rejected tab, then reload.
- Both lists share `VenueRequestData` (approval-only fields are nullable) and `VenueRequestAdapter` (`approveMode`).
- **Statuses:** `PENDING` (Waiting), `APPROVED`, `REJECTED`, `CANCELLED` and `EXPIRED`. Each has a tab in both sections, styled in `VenueRequestAdapter.statusStyle()`.
  - `EXPIRED` uses the same grey as Cancelled, with an hourglass icon and no strike-through. Its detail line is the decider, or "No decision was made before the event time."
  - Only `PENDING` cards get actions (⋮ menu / Approve / Reject).
- **Counts:** `GetOverallcountByMenuType` (menu "8"):
  - `venuerequests` → My Requests badge;
  - `approvevenuerequests` → Approve Event badge;
  - Events Request button badge = `venuerequests + approvevenuerequests`.
- User-facing text on these screens says "event", not "venue".
