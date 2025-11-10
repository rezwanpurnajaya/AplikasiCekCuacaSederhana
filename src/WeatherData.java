// WeatherData.java
// Model data sederhana untuk menyimpan informasi cuaca dari API

public class WeatherData {
    private String city;        // Nama kota
    private double tempC;       // Suhu dalam Celcius
    private String description; // Deskripsi cuaca (cerah, berawan, hujan, dll)
    private String iconCode;    // Kode ikon dari API
    private long timestamp;     // Waktu pengambilan data (epoch detik)

    // Konstruktor
    public WeatherData(String city, double tempC, String description, String iconCode, long timestamp) {
        this.city = city;
        this.tempC = tempC;
        this.description = description;
        this.iconCode = iconCode;
        this.timestamp = timestamp;
    }

    // Getter dan Setter
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTempC() {
        return tempC;
    }

    public void setTempC(double tempC) {
        this.tempC = tempC;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconCode() {
        return iconCode;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Untuk debugging / menampilkan data di console
    @Override
    public String toString() {
        return String.format(
            "WeatherData [city=%s, temp=%.1f°C, desc=%s, icon=%s, time=%d]",
            city, tempC, description, iconCode, timestamp
        );
    }
}
