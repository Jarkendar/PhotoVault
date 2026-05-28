# Gallery — Dry-run status & next step

Temporary handoff note. Tracks how the gallery is wired to run **without a backend**
(dry-run, Milestone 1) and what is required to make it run **against a real server**
(Milestone 2). Delete once the server step is complete.

## What's done — dry-run (Milestone 1)

The gallery is a full vertical slice wired with the **real** components, fed from the
**local Room cache** so it works with no server reachable.

- **Use cases** (`:feature:gallery`, `...feature.gallery.domain.usecase`): `ObservePhotosUseCase`,
  `ObserveCategoriesUseCase`, `RefreshGalleryUseCase`, `ToggleFavoriteUseCase` — thin wrappers over
  the `:core:domain` repository interfaces.
- **`GalleryViewModel`**: combines `observePhotos()` + `observeCategories()` with in-VM selection
  state (`selectedCategoryId`, `searchQuery`, `currentPage`) into `GalleryUiState`
  (`Loading → Empty/Content/Error`) via `stateIn(viewModelScope, WhileSubscribed)`.
  - Filtering, per-category `counts`, `totalCount`, and page slicing are computed **client-side**
    over the cached list (`PAGE_SIZE` constant). Cursor pagination and server `search()` are not used yet.
  - Actions wired: category select, text filter, page click, favorite toggle, refresh.
  - Navigation actions (photo click, upload, menu, avatar, destination) are no-ops for now (no nav).
- **Koin graph (per layer)** assembled in `:app`:
  `commonModule` (dispatchers) · `databaseModule` (Room + DAOs) · `networkModule`
  (HttpClient + APIs) · `dataModule` (repositories) · `galleryModule` (use cases + ViewModel).
- **`MainActivity`** uses `koinViewModel()` + `collectAsStateWithLifecycle()` and passes real
  callbacks into `GalleryScreen` (replaced the hardcoded `GalleryUiState.Loading`).
- **Auth / baseUrl**: `StubAuthTokenProvider` (returns null tokens) + a placeholder `baseUrl`.
  With no server reachable, `refreshGallery()` fails and the screen settles on `Empty`/`Error`
  while still rendering anything already cached in Room.

Net effect: the app launches, DI resolves the whole graph, the gallery renders cached data and all
local interactions work — without any backend.

## Next step — run against the server (Milestone 2)

Goal: feed the already-wired Ktor + Room stack real data so the gallery fills end-to-end.
Start with a **local stub**, defer the production server.

1. **Configurable `baseUrl`** — expose via `BuildConfig`/Koin parameter so stub ↔ real server is a
   one-line switch (emulator → `http://10.0.2.2:<port>/v1`).
2. **Local stub HTTP** (MockWebServer or n8n) returning JSON matching the DTOs:
   - `GET /v1/photos` → `PhotoPageDto` (`items`, `nextCursor`, `hasMore`)
   - `GET /v1/photos/{id}`, tag/category/label lists, `PATCH /v1/photos/{id}` (favorite)
   - RFC 7231 Problem Details for error cases.
3. **Fixtures** with real thumbnail/medium/original URLs, plus empty + error scenarios, to verify the
   full path: network → mappers → Room → Flow → ViewModel → UI.
4. **Image loading** — add/confirm an image library (e.g. Coil) so `StaggeredPhotoGrid` renders
   thumbnails from URLs.
5. **Verify** end-to-end: photos appear, favorite toggle round-trips, category filter works, error
   path shows `Error`, empty path shows `Empty`.

Deferred beyond this step: real Kotlin + Ktor server in Docker on the Raspberry Pi, real auth tokens,
and switching local client-side pagination/search over to cursor-based `loadMorePhotos()`/`search()`.
