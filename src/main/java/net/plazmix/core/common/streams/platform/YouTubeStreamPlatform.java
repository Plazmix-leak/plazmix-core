package net.plazmix.core.common.streams.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.common.streams.detail.AbstractStreamDetails;
import net.plazmix.core.common.streams.exception.StreamEndedException;
import net.plazmix.core.common.streams.exception.StreamException;
import net.plazmix.core.common.streams.exception.StreamNotFoundException;
import net.plazmix.core.connection.http.RequestBuilder;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class YouTubeStreamPlatform implements StreamPlatform<YouTubeStreamPlatform.YouTubeStreamDetails> {

    private static final String YOUTUBE_STREAM_URL = "https://youtu.be/";
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos";

    //matcher group - 4
    private static final Pattern YOUTUBE_VIDEO_PATTERN = Pattern.compile(
            "(https://(www.|)|www.)(youtube.com/watch\\?v=|youtu.be/)(.+)", Pattern.CASE_INSENSITIVE);

    /**
     * API ключ для YouTube
     */
    private final String apiKey;

    @Override
    public YouTubeStreamDetails parseStreamUrl(@NonNull String streamUrl) {
        Matcher matcher = YOUTUBE_VIDEO_PATTERN.matcher(streamUrl);

        //это не ссылка на стрим ютуба
        if (!matcher.matches()) {
            return null;
        }

        return new YouTubeStreamDetails(this, matcher.group(4));
    }

    @Override
    public String makeBeautifulUrl(@NonNull AbstractStreamDetails streamDetails) {
        return YOUTUBE_STREAM_URL + streamDetails.getIdentity();
    }

    @Override
    public JsonObject makeRequest(@NonNull String streamId) {
        String response = new RequestBuilder(YOUTUBE_API_URL)
                .parameter("key", apiKey)
                .parameter("part", "snippet,liveStreamingDetails")
                .parameter("id", streamId)
                .makeRequest();

        if (response == null) {
            return null;
        }

        return GSON.fromJson(response, JsonObject.class);
    }

    @Override
    public void updateStreamDetails(@NonNull AbstractStreamDetails streamDetails,
                                    @NonNull JsonObject jsonObject) throws StreamException {

        JsonArray items = jsonObject.getAsJsonArray("items");

        if (items.size() == 0) {
            throw new StreamNotFoundException();
        }

        JsonObject videoDetails = items.get(0).getAsJsonObject();

        JsonObject snippet = videoDetails.getAsJsonObject("snippet");
        JsonObject liveStreamingDetails = videoDetails.getAsJsonObject("liveStreamingDetails");

        if (liveStreamingDetails.has("actualEndTime")) {
            throw new StreamEndedException();
        }

        streamDetails.setTitle(snippet.get("title").getAsString());
        streamDetails.setViewers(liveStreamingDetails.get("concurrentViewers").getAsInt());
        streamDetails.setStartedAtServiceTime(Instant.parse(liveStreamingDetails.get("actualStartTime").getAsString()).toEpochMilli());
    }

    @Override
    public String getDisplayName() {
        return "§6YouTube";
    }

    @Getter
    class YouTubeStreamDetails extends AbstractStreamDetails {

        private final String identity;

        YouTubeStreamDetails(@NonNull StreamPlatform<?> streamPlatform, @NonNull String identity) {
            super(streamPlatform);

            this.identity = identity;
        }
    }

}
