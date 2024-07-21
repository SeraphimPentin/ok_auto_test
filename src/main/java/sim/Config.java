package sim;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getBaseUrl() {
        return dotenv.get("BASE_URL");
    }

    public static String getApplicationKey() {
        return dotenv.get("APPLICATION_KEY");
    }

    public static String getApplicationSecretKey() {
        return dotenv.get("APPLICATION_SECRET_KEY");
    }

    public static String getAccessToken() {
        return dotenv.get("ACCESS_TOKEN");
    }

    public static String getSessionSecretKey() {
        return dotenv.get("SESSION_SECRET_KEY");
    }
}
