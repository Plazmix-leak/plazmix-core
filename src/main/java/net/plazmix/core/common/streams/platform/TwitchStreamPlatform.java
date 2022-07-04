package net.plazmix.core.common.streams.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.common.streams.detail.AbstractStreamDetails;
import net.plazmix.core.common.streams.exception.StreamException;
import net.plazmix.core.common.streams.exception.StreamNotFoundException;
import net.plazmix.core.connection.http.RequestBuilder;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class TwitchStreamPlatform implements StreamPlatform<TwitchStreamPlatform.TwitchStreamDetails> {

    private static final String TWITCH_STREAM_URL = "https://twitch.tv/";
    private static final String TWITCH_API_URL = "https://api.twitch.tv/helix/streams";

    //matcher group - 3
    private static final Pattern TWITCH_STREAM_PATTERN = Pattern.compile(
            "(https://(www.|)|www.)twitch.tv/(.+)", Pattern.CASE_INSENSITIVE);

    private final String clientId;

    @Override
    public TwitchStreamDetails parseStreamUrl(@NonNull String streamUrl) {
        Matcher matcher = TWITCH_STREAM_PATTERN.matcher(streamUrl);

        //это не ссылка на стрим ютуба
        if (!matcher.matches()) {
            return null;
        }

        return new TwitchStreamDetails(this, matcher.group(3));
    }

    @Override
    public String makeBeautifulUrl(@NonNull AbstractStreamDetails streamDetails) {
        return TWITCH_STREAM_URL + streamDetails.getIdentity();
    }

    @Override
    public JsonObject makeRequest(@NonNull String streamId) {
        System.out.println("получаю response");
        String response = new RequestBuilder(TWITCH_API_URL)
                .header("Client-ID", clientId)
                .parameter("user_login", streamId)
                .makeRequest();

        if (response == null) {
            System.out.println("response null :(");
            return null;
        }

        System.out.println("response: " + response);
        return GSON.fromJson(response, JsonObject.class);
    }

    @Override
    public void updateStreamDetails(@NonNull AbstractStreamDetails details, @NonNull JsonObject jsonObject) throws StreamException {
        JsonArray data = jsonObject.getAsJsonArray("data");

        if (data.size() == 0) {
            throw new StreamNotFoundException();
        }

        JsonObject streamDetails = data.get(0).getAsJsonObject();

        details.setTitle(streamDetails.get("title").getAsString());
        details.setViewers(streamDetails.get("viewer_count").getAsInt());
        details.setStartedAtServiceTime(Instant.parse(streamDetails.get("started_at").getAsString()).toEpochMilli());
    }

    @Override
    public String getDisplayName() {
        return "§3Twitch";
    }

    @Getter
    class TwitchStreamDetails extends AbstractStreamDetails {

        private final String identity;

        TwitchStreamDetails(@NonNull StreamPlatform<?> streamPlatform, @NonNull String identity) {
            super(streamPlatform);

            this.identity = identity;
        }
    }

}
