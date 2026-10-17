# Axeron Clean Edition (1:1 Shizuku Rebuild)

Proyek Game Launcher & Display Engine murni (Clean Code) 1:1 dengan fitur Axeron, dibangun ulang dengan **Shizuku API resmi (Standar Pixels)** untuk menghilangkan bug koneksi Wireless Debugging.

---

## Fitur Utama

1. **Space (Game Launcher & Per-App Config)**:
   - Deteksi otomatis game & aplikasi terpasang.
   - **Per-Game Config**: Atur resolusi, DPI, Mode X, dan Grafik MC khusus per game.
   - Saat game ditekan **PLAY**, resolusi dan tweak langsung diterapkan otomatis.

2. **Display Engine (Pixels Standard)**:
   - Mengubah resolusi (`wm size`) dan DPI (`wm density`) secara instan.
   - Presets cepat: Native, 720p HD, dan **Grafik MC**.
   - Input custom resolusi dan tombol **Reset ke Default**.

3. **Tweak Khusus**:
   - **🎮 Grafik MC (Potato Mode)**: Memaksa downscaling tekstur dan resolusi 50% sehingga grafis 3D in-game menjadi kotak-kotak rata / flat ala Minecraft, mendongkrak performa dan kestabilan FPS.
   - **🔥 Mode X (ROG Performance)**: Memaksa 4x MSAA, mematikan HW Overlays (paksa render via GPU), dan memangkas proses background.
   - **⚡ Quick Boost (Memory Loosener)**: Membersihkan cache RAM dan memori proses.

4. **In-Game Corner Panel & Tools**:
   - Floating handle di sudut layar saat bermain game.
   - Custom Crosshair bidik presisi.
   - Real-time Session Monitor (FPS & RAM HUD).
   - HW Touch Enhancer (respon sentuh cepat).

5. **Vortex Control**:
   - Monitoring status Shizuku secara real-time.
   - Informasi perangkat dan uji eksekusi shell binder.

---

## Cara Build Menjadi APK

### Cara 1: Otomatis via GitHub Actions (Tanpa PC, Cukup dari HP)
1. Buat repository baru di akun GitHub Anda (bisa lewat browser HP).
2. Ekstrak dan upload seluruh isi folder proyek ini ke repository tersebut.
3. Buka tab **Actions** di GitHub, proses build akan berjalan otomatis (~2-3 menit).
4. Setelah selesai, download file installer `.apk` dari bagian **Artifacts**.

### Cara 2: Via Android Studio (Laptop/PC)
1. Buka folder `AxeronClean` di Android Studio.
2. Tunggu Gradle sync selesai.
3. Klik menu **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
