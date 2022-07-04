package net.plazmix.core.connection.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class MultipartUtility {

    private HttpURLConnection httpConn;
    private DataOutputStream request;
    private final String boundary = "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";

    public MultipartUtility(String requestURL) {
        try {
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("Cache-Control", "no-cache");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);

            request = new DataOutputStream(httpConn.getOutputStream());
        } catch (IOException ignored) {
            System.out.println("Error when trying to connect to the url for uploading file in multipart/form-data, url: " + requestURL);
        }
    }

    public void addFilePart(String fieldName, File uploadFile) {
        try {
            String fileName = uploadFile.getName();
            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    fieldName + "\";filename=\"" +
                    fileName + "\"" + this.crlf);
            request.writeBytes(this.crlf);

            byte[] bytes = Files.readAllBytes(uploadFile.toPath());
            request.write(bytes);
        } catch (IOException ignored) {
            System.out.println("Error when adding file as multipart/form-data field. Field name is " + fieldName + " and file path is " + uploadFile.getAbsolutePath());
        }
    }

    public void addBytesPart(String fieldName, String fileName, byte[] bytes) {
        try {
            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + fileName + "\"" + this.crlf);
            request.writeBytes(this.crlf);

            request.write(bytes);
        } catch (IOException ignored) {
            System.out.println("Error when adding bytes as multipart/form-data field. Field name is " + fieldName + " and file name is " + fileName);
        }
    }

    public String finish() throws IOException {
        String response = "error";

        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        int status = httpConn.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new BufferedInputStream(httpConn.getInputStream());

            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            response = stringBuilder.toString();

            httpConn.disconnect();
        }

        return response;
    }
}