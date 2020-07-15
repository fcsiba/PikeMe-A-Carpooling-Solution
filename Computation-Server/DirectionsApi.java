import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.ApiConfig;
import com.google.maps.internal.ApiResponse;
import com.google.maps.internal.StringJoin.UrlValue;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodedWaypoint;


public class DirectionsApi {
  static final ApiConfig API_CONFIG = new ApiConfig("/maps/api/directions/json");

  private DirectionsApi() {}

  /**
   * Creates a new DirectionsApiRequest using the given context, with all attributes at their
   * default values.
   *
   * @param context Context that the DirectionsApiRequest will be executed against
   * @return A newly constructed DirectionsApiRequest between the given points.
   */
  public static DirectionsApiRequest newRequest(GeoApiContext context) {
    return new DirectionsApiRequest(context);
  }

  /**
   * Creates a new DirectionsApiRequest between the given origin and destination, using the defaults
   * for all other options.
   *
   * @param context Context that the DirectionsApiRequest will be executed against
   * @param origin Origin address as text
   * @param destination Destination address as text
   * @return A newly constructed DirectionsApiRequest between the given points.
   */
  public static DirectionsApiRequest getDirections(
      GeoApiContext context, String origin, String destination) {
    return new DirectionsApiRequest(context).origin(origin).destination(destination);
  }

  static class Response implements ApiResponse<DirectionsResult> {
    public String status;
    public String errorMessage;
    public GeocodedWaypoint[] geocodedWaypoints;
    public DirectionsRoute[] routes;

    @Override
    public boolean successful() {
      return "OK".equals(status) || "ZERO_RESULTS".equals(status);
    }

    @Override
    public DirectionsResult getResult() {
      DirectionsResult result = new DirectionsResult();
      result.geocodedWaypoints = geocodedWaypoints;
      result.routes = routes;
      return result;
    }

    @Override
    public ApiException getError() {
      if (successful()) {
        return null;
      }
      return ApiException.from(status, errorMessage);
    }
  }

  /**
   * Directions may be calculated that adhere to certain restrictions. This is configured by calling
   * {@link com.google.maps.DirectionsApiRequest#avoid} or {@link
   * com.google.maps.DistanceMatrixApiRequest#avoid}.
   *
   * @see <a href="https://developers.google.com/maps/documentation/directions/intro#Restrictions">
   *     Restrictions in the Directions API</a>
   * @see <a
   *     href="https://developers.google.com/maps/documentation/distance-matrix/intro#RequestParameters">
   *     Distance Matrix API Request Parameters</a>
   */
  public enum RouteRestriction implements UrlValue {

    /** Indicates that the calculated route should avoid toll roads/bridges. */
    TOLLS("tolls"),

    /** Indicates that the calculated route should avoid highways. */
    HIGHWAYS("highways"),

    /** Indicates that the calculated route should avoid ferries. */
    FERRIES("ferries");

    private final String restriction;

    RouteRestriction(String restriction) {
      this.restriction = restriction;
    }

    @Override
    public String toString() {
      return restriction;
    }

    @Override
    public String toUrlValue() {
      return restriction;
    }
  }
}
