package au.com.dius.resilience.location;

import android.location.Location;
import android.location.LocationManager;
import au.com.dius.resilience.location.criteria.IsAccurateEnoughCriteria;
import au.com.dius.resilience.location.criteria.IsMoreAccurateCriteria;
import au.com.dius.resilience.location.criteria.IsMoreRecentCriteria;
import au.com.dius.resilience.location.criteria.IsRecentEnoughCriteria;
import com.google.inject.Inject;
import roboguice.inject.ContextSingleton;

import java.util.ArrayList;
import java.util.List;

@ContextSingleton
public class BestLocationDelegate {

  @Inject
  private LocationManager locationManager;

  private List<LocationCriteria> criteria = new ArrayList<LocationCriteria>();

  @Inject
  public BestLocationDelegate(IsAccurateEnoughCriteria isAccurateEnoughCriteria,
                              IsMoreAccurateCriteria isMoreAccurateCriteria,
                              IsRecentEnoughCriteria isRecentEnoughCriteria,
                              IsMoreRecentCriteria isMoreRecentCriteria) {

    criteria.add(isAccurateEnoughCriteria);
    criteria.add(isMoreAccurateCriteria);
    criteria.add(isRecentEnoughCriteria);
    criteria.add(isMoreRecentCriteria);
  }

  public Location getBestLastKnownLocation() {

    Location bestLocation = null;
    for (String provider : locationManager.getAllProviders()) {
      Location candidateLocation = locationManager.getLastKnownLocation(provider);
      if (passesCriteria(candidateLocation, bestLocation)) {
        bestLocation = candidateLocation;
      }
    }

    return bestLocation;
  }

  private boolean passesCriteria(Location candidateLocation, Location previousLocation) {
    for (LocationCriteria locationCriteria : criteria) {
      if (!locationCriteria.passes(candidateLocation, previousLocation)) {
        return false;
      }
    }

    return true;
  }
}
