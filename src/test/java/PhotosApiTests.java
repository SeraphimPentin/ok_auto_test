import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import sim.Config;


import java.util.Map;
import java.util.TreeMap;

public class PhotosApiTests {

    private static final String BASE_URL = "https://api.ok.ru/fb.do";
    private static final String APPLICATION_KEY = Config.getApplicationKey();
    private static final String APPLICATION_SECRET_KEY = Config.getApplicationSecretKey();
    private static final String ACCESS_TOKEN = Config.getAccessToken();

    @Test
    public void testEditPhotoDescriptionSuccess() throws Exception {
        String photoId = "1234567890";
        String description = "New photo description";

        String url = BASE_URL + "?method=photos.editPhoto" +
                "&photo_id=" + photoId +
                "&description=" + description +
                "&access_token=" + ACCESS_TOKEN +
                "&application_key=" + APPLICATION_KEY +
                "&sig=" + generateSignature(photoId, description);

        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            Assert.assertEquals(statusCode, 200, "Expected HTTP status 200");
            Assert.assertTrue(responseBody.contains("\"response\":\"success\""), "Expected success response");
        } finally {
            client.close();
        }
    }

    @Test
    public void testRemovePhotoDescription() throws Exception {
        String photoId = "1234567890";

        String url = BASE_URL + "?method=photos.editPhoto" +
                "&photo_id=" + photoId +
                "&access_token=" + ACCESS_TOKEN +
                "&application_key=" + APPLICATION_KEY +
                "&sig=" + generateSignature(photoId, null);

        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            Assert.assertEquals(statusCode, 200, "Expected HTTP status 200");
            Assert.assertTrue(responseBody.contains("\"response\":\"success\""), "Expected success response");
        } finally {
            client.close();
        }
    }

    @Test
    public void testInvalidPhotoId() throws Exception {
        String invalidPhotoId = "invalid_id";
        String description = "Description with invalid photo_id";

        String url = BASE_URL + "?method=photos.editPhoto" +
                "&photo_id=" + invalidPhotoId +
                "&description=" + description +
                "&access_token=" + ACCESS_TOKEN +
                "&application_key=" + APPLICATION_KEY +
                "&sig=" + generateSignature(invalidPhotoId, description);

        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            Assert.assertEquals(statusCode, 400, "Expected HTTP status 400");
            Assert.assertTrue(responseBody.contains("error"), "Expected error response");
        } finally {
            client.close();
        }
    }

    @Test
    public void testMissingPermissions() throws Exception {
        String photoId = "1234567890";
        String description = "Description without proper permissions";

        String invalidAccessToken = "invalid_access_token";

        String url = BASE_URL + "?method=photos.editPhoto" +
                "&photo_id=" + photoId +
                "&description=" + description +
                "&access_token=" + invalidAccessToken +
                "&application_key=" + APPLICATION_KEY +
                "&sig=" + generateSignature(photoId, description);

        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            Assert.assertEquals(statusCode, 403, "Expected HTTP status 403");
            Assert.assertTrue(responseBody.contains("error"), "Expected error response");
        } finally {
            client.close();
        }
    }

    @Test
    public void testInvalidRequestFormat() throws Exception {
        String photoId = "1234567890";

        String url = BASE_URL + "?method=photos.editPhoto" +
                "&photo_id=" + photoId +
                "&access_token=" + ACCESS_TOKEN +
                "&application_key=" + APPLICATION_KEY +
                "&sig=" + generateSignature(photoId, null);

        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setEntity(new StringEntity("invalid_format"));

        CloseableHttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            Assert.assertEquals(statusCode, 400, "Expected HTTP status 400");
            Assert.assertTrue(responseBody.contains("error"), "Expected error response");
        } finally {
            client.close();
        }
    }

    private String generateSignature(String photoId, String description) {
        Map<String, String> params = new TreeMap<>();
        params.put("photo_id", photoId);
        if (description != null) params.put("description", description);
        params.put("application_key", APPLICATION_KEY);

        StringBuilder paramString = new StringBuilder();
        params.forEach((key, value) -> paramString.append(key).append("=").append(value).append("&"));

        // Removing the last '&' and appending the session_secret_key
        if (ACCESS_TOKEN != null) {
            String sessionSecretKey = DigestUtils.md5Hex(ACCESS_TOKEN + APPLICATION_SECRET_KEY).toLowerCase();
            paramString.append("application_secret_key=").append(sessionSecretKey);
        } else {
            paramString.append("application_secret_key=").append(APPLICATION_SECRET_KEY);
        }

        return DigestUtils.md5Hex(paramString.toString()).toLowerCase();
    }
}
