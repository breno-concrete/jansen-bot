import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestJavaNet {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://oauth2.googleapis.com/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            System.out.println("Response code: " + code);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
