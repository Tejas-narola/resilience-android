package au.com.dius.resilience.test.unit.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import au.com.dius.resilience.test.unit.utils.ResilienceTestRunner;
import au.com.justinb.open311.model.ServiceRequest;
import com.xtremelabs.robolectric.Robolectric;
import junitx.util.PrivateAccessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

@RunWith(ResilienceTestRunner.class)
public class SimpleListLoaderTest {

  public static final String REFRESH_OBSERVER = "refreshObserver";
  public static final String DATA = "data";

  @Mock
  private Context mockContext;

  @Mock
  private BroadcastReceiver mockObserver;

  private SimpleListLoader listLoader;

  private ShadowLoader shadowLoader;

  @Before
  public void setUp() throws NoSuchFieldException {
    listLoader = new SimpleListLoader(mockContext);
    PrivateAccessor.setField(listLoader, REFRESH_OBSERVER, mockObserver);

    shadowLoader = (ShadowLoader) Robolectric.shadowOf_(listLoader);
  }

  @Test
  public void shouldOnlyDeliverResultWhenInStartedState() {
    shadowLoader.setStarted(true);
    ArrayList<Object> objects = new ArrayList<Object>();

    listLoader.deliverResult(objects);
    Assert.assertSame(objects, shadowLoader.getDeliveredResult());
  }

  @Test
  public void onStartLoadingShouldUseExistingObserver() {
    BroadcastReceiver refreshObserver = getMockObserver();

    assertNotNull(refreshObserver);

    listLoader.onStartLoading();
    assertSame(refreshObserver, getMockObserver());
  }

  @Test
  public void onStartLoadingShouldCreateNewObserverIfNotAlreadySet() throws NoSuchFieldException {
    PrivateAccessor.setField(listLoader, REFRESH_OBSERVER, null);

    assertNull(getMockObserver());

    listLoader.onStartLoading();
    assertNotNull(getMockObserver());
  }

  @Test
  public void shouldNotDeliverResultWhenStarted() {
    shadowLoader.setStarted(false);
    ArrayList originalData = new ArrayList();
    shadowLoader.deliverResult(originalData);

    listLoader.deliverResult(new ArrayList<Object>());

    assertSame(shadowLoader.getDeliveredResult(), originalData);
  }

  @Test
  public void onResetShouldUnregisterObserver() {
    listLoader.onReset();

    Mockito.verify(mockContext).unregisterReceiver(mockObserver);
    Object refreshObserver = getMockObserver();
    Assert.assertThat(refreshObserver, nullValue());

    Object data = getData();
    Assert.assertThat(data, nullValue());
  }

  @Test
  public void startLoadingShouldForceLoadWhenContentChanged() {
    shadowLoader.setTakeContentChanged(true);

    listLoader.onStartLoading();
    assertThat(shadowLoader.isForceLoadCalled(), is(true));
  }

  @Test
  public void startLoadingShouldForceLoadWhenDataIsNull() throws NoSuchFieldException {
    shadowLoader.setTakeContentChanged(false);
    PrivateAccessor.setField(listLoader, DATA, null);

    listLoader.onStartLoading();
    assertThat(shadowLoader.isForceLoadCalled(), is(true));
  }

  @Test
  public void startLoadingShouldDeliverResultIfDataAlreadySet() throws NoSuchFieldException {
    ArrayList<ServiceRequest> data = new ArrayList<ServiceRequest>();
    PrivateAccessor.setField(listLoader, DATA, data);
    shadowLoader.setStarted(true);

    listLoader.onStartLoading();

    assertSame(data, shadowLoader.getDeliveredResult());
    assertThat(shadowLoader.isForceLoadCalled(), is(false));
  }

  @Test
  public void startLoadingShouldNotForceLoadWhenDataIsNullOrContentHasNotChanged() throws NoSuchFieldException {
    shadowLoader.setTakeContentChanged(false);
    PrivateAccessor.setField(listLoader, DATA, new ArrayList<ServiceRequest>());

    listLoader.onStartLoading();
    assertThat(shadowLoader.isForceLoadCalled(), is(false));
  }

  private BroadcastReceiver getMockObserver() {
    try {
      return (BroadcastReceiver) PrivateAccessor.getField(listLoader, REFRESH_OBSERVER);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private Object getData() {
    try {
      return PrivateAccessor.getField(listLoader, DATA);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

}
