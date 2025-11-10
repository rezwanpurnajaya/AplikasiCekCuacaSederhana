import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.gson.*;

/**
 * Kelas ini berfungsi untuk mengambil data cuaca real-time
 * dari API OpenWeatherMap dan mengelola data favorit.
 */
public class LogikaCekCuacaSederhana {

    // Ganti dengan API key kamu dari https://openweathermap.org/api
    public static String API_KEY = "3ac2ccd21d3c378a62aeefd8e237ba9d";

    // =========================
    // Ambil Data Cuaca dari API
    // =========================
    public static WeatherData ambilDataCuaca(String namaKota) throws IOException {
        // Format URL API
        String apiUrl = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s",
                URLEncoder.encode(namaKota, "UTF-8"),
                API_KEY);

        // Koneksi HTTP
        HttpURLConnection koneksi = (HttpURLConnection) new URL(apiUrl).openConnection();
        koneksi.setRequestMethod("GET");
        koneksi.setConnectTimeout(8000);
        koneksi.setReadTimeout(8000);

        int kodeRespon = koneksi.getResponseCode();
        InputStream is = (kodeRespon >= 200 && kodeRespon < 300)
                ? koneksi.getInputStream()
                : koneksi.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(is)) {
            JsonElement root = JsonParser.parseReader(reader);
            JsonObject obj = root.getAsJsonObject();

            // Kalau error
            if (kodeRespon >= 400) {
                String pesan = obj.has("message") ? obj.get("message").getAsString() : "Gagal mengambil data dari API";
                throw new IOException("API Error: " + pesan);
            }

            // Ambil data dari JSON
            String nama = obj.get("name").getAsString();
            double suhu = obj.getAsJsonObject("main").get("temp").getAsDouble();

            JsonArray weatherArr = obj.getAsJsonArray("weather");
            JsonObject cuacaObj = weatherArr.get(0).getAsJsonObject();
            String deskripsi = cuacaObj.get("description").getAsString();
            String kodeIcon = cuacaObj.get("icon").getAsString();

            long waktu = obj.get("dt").getAsLong();

            return new WeatherData(nama, suhu, deskripsi, kodeIcon, waktu);
        } finally {
            koneksi.disconnect();
        }
    }

    // ======================
    // URL untuk ikon cuaca
    // ======================
    public static String urlIkon(String kodeIcon) {
        return "https://openweathermap.org/img/wn/" + kodeIcon + "@2x.png";
    }

    // ===========================
    // Format waktu hasil API
    // ===========================
    public static String formatWaktu(long epochSeconds) {
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // ===========================
    // Simpan & muat favorit kota
    // ===========================
    private static final String FILE_FAVORIT = "favorit_kota.txt";

    public static List<String> muatFavorit() {
        try {
            Path p = Paths.get(FILE_FAVORIT);
            if (!Files.exists(p)) return new ArrayList<>();
            return Files.readAllLines(p);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void simpanFavorit(List<String> daftar) {
        try {
            Files.write(Paths.get(FILE_FAVORIT), daftar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===========================
    // Simpan data tabel ke CSV
    // ===========================
    public static void simpanCSV(File file, javax.swing.table.TableModel model) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                pw.print(model.getColumnName(c));
                if (c < model.getColumnCount() - 1) pw.print(",");
            }
            pw.println();

            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object val = model.getValueAt(r, c);
                    String cell = val == null ? "" : val.toString();
                    pw.print(cell);
                    if (c < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }
        }
    }

    // ===========================
    // Muat data CSV ke JTable
    // ===========================
    public static void muatCSV(File file, javax.swing.table.DefaultTableModel model) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    model.addRow(parts);
                }
            }
        }
    }
}
