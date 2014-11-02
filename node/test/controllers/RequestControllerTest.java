package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import play.Logger;
import play.libs.Json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.fest.assertions.Assertions.assertThat;

public class RequestControllerTest {

    @Test
    public void test3NodesChain_shouldReturnHello() {
        final String payload = "ew0gICAgInBheWxvYWQiIDogImV3MGdJQ0FnSW5CaGVXeHZZV1FpSURvZ0ltVjNiMmRKUTBGblNXMW9kbU16VW5WWlZ6RnNTV3B2WjBscFNYTkRhVUZuU1VOQmFXTkhPWGxrUTBrMlNVTkphVXhCYjJkSlEwRm5TVzB4YkdSSGFIWmFRMGsyU1VOSmFVeEJiMmRKUTBGblNXMVNhR1JIUldsUGFVRnBTV2wzUzBsRFFXZEpRMHAyWTIxc2JtRlhOV2hrUnpsNVdETkNNVmx0ZUhCWk1UbHlXbGhyYVU5cFFXbEpaM0E1UTJjOVBTSXNEU0FnSUNBaWRHRnlaMlYwSWpvZ2V3MGdJQ0FnSUNBZ0lDSnBjQ0k2SUNJeE1qY3VNQzR3TGpFaUxBMGdJQ0FnSUNBZ0lDSndiM0owSWpvZ0lqa3dNREFpRFNBZ0lDQjlEWDBOIiwNICAgICJ0YXJnZXQiOiB7DSAgICAgICAgImlwIiA6ICIxMjcuMC4wLjEiLA0gICAgICAgICJwb3J0IiA6ICI5MDAwIg0gICAgfQ19DQ==";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://localhost:9000/request");
        ObjectNode result = Json.newObject();
        result.put("payload", payload);

        try {
            httpPost.setEntity(new StringEntity(result.toString()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String inputStreamString = new Scanner(inputStream,"UTF-8").useDelimiter("\\A").next();

            Logger.info("Got message: " + inputStreamString);
            assertThat(inputStreamString).isEqualTo("Hello!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}