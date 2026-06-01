# Security: Handling Secrets

This project does **not** commit any API keys, service-account files, or
other credentials. Everything sensitive is loaded at build time from
`local.properties` (which is git-ignored) or from environment variables
on CI.

---

## 1. First-time setup (for every developer)

After cloning the repo, do the following:

### 1.1 Configure `local.properties`

Open `local.properties` at the project root and add:

```properties
sdk.dir=/path/to/your/Android/sdk

# Get this from https://console.cloud.google.com/google/maps-apis/credentials
MAPS_API_KEY=your_google_maps_api_key_here
```

### 1.2 Configure `app/google-services.json`

1. Go to the [Firebase Console](https://console.firebase.google.com/),
   open the `chw-disease-mapper` project (or your own), and download the
   `google-services.json` for the Android app
   `com.healthtracker.chw`.
2. Place the downloaded file at `app/google-services.json`.

A reference shape is provided in `app/google-services.json.template`.

That's it — `./gradlew assembleDebug` should now succeed.

---

## 2. How keys flow into the build

| Secret                | Source (dev)                   | Source (CI)          | Consumed by                       |
| --------------------- | ------------------------------ | -------------------- | --------------------------------- |
| `MAPS_API_KEY`        | `local.properties`             | env var              | `AndroidManifest.xml` (placeholder) |
| Firebase API key      | `app/google-services.json`     | secret file / artifact | `google-services` Gradle plugin |

The `app/build.gradle.kts` file loads `local.properties`, then exposes
`MAPS_API_KEY` to the manifest via `manifestPlaceholders`. The manifest
references it as `${MAPS_API_KEY}` — no real key ever lives in
version-controlled files.

---

## 3. If a secret is leaked (what just happened to us)

GitHub flagged the following keys in our repo history:

| Secret           | Where                                                                  | Commit       |
| ---------------- | ---------------------------------------------------------------------- | ------------ |
| Google Maps key  | `app/src/main/AndroidManifest.xml:28`                                  | `de27ee72`   |
| Firebase API key | `app/google-services.json:18`                                          | `de27ee72`   |
| Supabase key     | `app/src/main/java/com/healthtracker/chw/config/SupabaseConfig.java:27` | `13a68111` |

**Rotation is mandatory** — removing them from the repo does not
invalidate them. Anyone who saw the public repo before the rotation has
copies.

### 3.1 Rotate the Google Maps API key

1. Open <https://console.cloud.google.com/google/maps-apis/credentials>.
2. Find the leaked key (the one starting with `AIzaSyCW6V_…` — full
   value visible in commit `de27ee72`) and click **Delete** (or
   **Regenerate**).
3. Create a new key, then **Restrict it**:
   - **Application restrictions** → *Android apps* → add the package
     `com.healthtracker.chw` together with your SHA-1 signing
     certificate fingerprint.
   - **API restrictions** → restrict to *Maps SDK for Android* only.
4. Put the new key in `local.properties`:
   ```properties
   MAPS_API_KEY=AIza...new_key
   ```

### 3.2 Rotate the Firebase Web API key

1. Open <https://console.cloud.google.com/apis/credentials?project=chw-disease-mapper>.
2. Find the auto-generated *Android key* / *Browser key* used by
   Firebase and **Regenerate** or **Delete and recreate** it.
3. Re-download `google-services.json` from the Firebase Console and put
   it at `app/google-services.json`.

> Note: Firebase Web API keys are designed to be embedded in client
> apps and are not "secret" by themselves — security is enforced by
> Firebase Auth rules, Firestore rules, and App Check. Still, GitHub
> flags them, and rotating + tightening rules is the right hygiene.

### 3.3 Revoke the Supabase key

The Supabase backend was already replaced with FHIR (commit
`f99be01`), so the project no longer uses Supabase. Still:

1. Open the Supabase project dashboard.
2. **Project Settings → API → Reset** both the `anon` and
   `service_role` keys.
3. If the project is no longer needed, **delete** it.

---

## 4. (Optional) Scrub the leaked keys from git history

The fix above stops *new* commits from containing secrets, but the old
commits (`de27ee72`, `13a68111`) still hold them and remain
clone-able. After you've rotated the keys (step 3), you can also
rewrite history to remove them.

> WARNING: Rewriting history changes commit hashes. Everyone who has
> cloned the repo must re-clone or hard-reset. Coordinate with your
> teammates first.

```bash
# 1. Make sure you have a fresh clone with all branches.
git clone --mirror https://github.com/MagnifiqueUwizeye01/Disease-Outbreak-Mapper.git
cd Disease-Outbreak-Mapper.git

# 2. Install git-filter-repo (https://github.com/newren/git-filter-repo)
#    pip install git-filter-repo

# 3. Create a replacements file (one secret per line, format: literal==>REDACTED)
#    For the exact literals, look at:
#      git show de27ee72 -- app/google-services.json app/src/main/AndroidManifest.xml
#      git show 13a68111 -- app/src/main/java/com/healthtracker/chw/config/SupabaseConfig.java
#    Then put one `<leaked-literal>==>REDACTED_*` per line:
cat > ../replacements.txt <<'EOF'
AIzaSy<...firebase-key-from-de27ee72...>==>REDACTED_FIREBASE_KEY
AIzaSy<...maps-key-from-de27ee72...>==>REDACTED_MAPS_KEY
<...supabase-key-from-13a68111...>==>REDACTED_SUPABASE_KEY
EOF
# (Add any Supabase keys from SupabaseConfig.java in commit 13a68111.)

# 4. Rewrite history.
git filter-repo --replace-text ../replacements.txt

# 5. Force-push the cleaned history.
git push --force --all
git push --force --tags
```

Then ask GitHub Support to purge any cached views of the old commits.

---

## 5. Prevent recurrence

- **Pre-commit hook**: install
  [`gitleaks`](https://github.com/gitleaks/gitleaks) or
  [`detect-secrets`](https://github.com/Yelp/detect-secrets) and run
  it on every commit.
- **GitHub push protection**: enable it under
  *Settings → Code security and analysis → Secret scanning →
  Push protection*.
- **Never** put credentials in `AndroidManifest.xml`, resource files,
  or Java/Kotlin source. Use `local.properties` +
  `manifestPlaceholders` / `BuildConfig` like this project now does.
