package dhcoder.support.pattern;

import dhcoder.support.lambda.Action;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public final class ServicesTest {

    private interface TestService {}

    private final class DummyService1 implements TestService {}

    private final class DummyService2 implements TestService {}

    @Test
    public void serviceCanRegisterAndRetrieveServices() {
        Services services = new Services();
        DummyService1 dummyService1 = new DummyService1();
        DummyService2 dummyService2 = new DummyService2();

        services.register(DummyService1.class, dummyService1);
        services.register(DummyService2.class, dummyService2);

        assertThat(services.get(DummyService1.class), equalTo(dummyService1));
        assertThat(services.get(DummyService2.class), equalTo(dummyService2));
    }

    @Test
    public void registeringDuplicateServiceTypesThrowsException() {
        final Services services = new Services();
        TestService dummyService1 = new DummyService1();
        final TestService dummyService2 = new DummyService2();

        services.register(TestService.class, dummyService1);

        assertException("Duplicate services are not allowed", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                services.register(TestService.class, dummyService2);
            }
        });
    }

    @Test
    public void requestingUnregistedServiceThrowsException() {
        final Services services = new Services();
        TestService dummyService1 = new DummyService1();
        services.register(DummyService1.class, dummyService1);

        assertException("Can't request unregistered service.", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                services.get(DummyService2.class);
            }
        });
    }

}