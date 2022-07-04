package net.plazmix.core.api.utility;

import com.google.common.base.Preconditions;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.experimental.UtilityClass;
import net.plazmix.core.connection.callback.Callback;
import net.plazmix.core.connection.http.HttpClient;

import java.net.InetSocketAddress;

@UtilityClass
public class IpAddressUtil {

    private static final EventLoop EXECUTORS    = new NioEventLoopGroup().next();

    public static final String HTTP_REQUEST_URL = "http://api.ipstack.com/%s?access_key=%s";
    public static final String ACCESS_KEY       = "aa8d91ad3514ebe2a8783827a5faf8b8";

    public static class IpAddressStats {

        public String ip;
        public String type;

        public String continent_code;
        public String continent_name;

        public String county_code;
        public String country_name;

        public String region_code;
        public String region_name;

        public String city;

        public String zip;

        public double latitude;
        public double longitude;

        public IpAddressLocation location;
    }

    public static class IpAddressLocation {

        public int geoname_id;

        public String capital;

        public String county_flag;
        public String county_flag_emoji;
        public String county_flag_emoji_unicode;

        public String calling_code;

        public boolean is_eu;
    }


    public void getAddressStats(InetSocketAddress inetSocketAddress, Callback<IpAddressStats> addressStatsCallback) {
        Preconditions.checkArgument(inetSocketAddress != null, "inetSocketAddress");

        getAddressStats(inetSocketAddress.getHostName(), addressStatsCallback);
    }

    public void getAddressStats(String address, Callback<IpAddressStats> addressStatsCallback) {
        Preconditions.checkArgument(address != null, "address");
        Preconditions.checkArgument(addressStatsCallback != null, "callback");

        HttpClient.connectToUrl(String.format(HTTP_REQUEST_URL, address, ACCESS_KEY), EXECUTORS,
                (result, error) -> {

            if (error != null) {
                addressStatsCallback.done(null, error);
                return;
            }

            IpAddressStats ipAddressStats = JsonUtil.fromJson(result, IpAddressStats.class);

            if (ipAddressStats != null) {
                addressStatsCallback.done(ipAddressStats, null);
            }
        });
    }

}
