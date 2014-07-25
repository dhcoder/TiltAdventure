package dhcoder.support.memory;

import dhcoder.support.lambda.Action;
import org.junit.Before;
import org.junit.Test;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public final class PoolTest {

    private final class PooledItem {

        private int releasedCount;

        public PooledItem() {
            allocationCount++;
        }

        public int getReleasedCount() { return releasedCount; }

        public void release() { releasedCount++; }
    }

    private int allocationCount;

    @Before
    public void setUp() {
        allocationCount = 0;
    }

    @Test
    public void grabNewAvoidsAllocation() {

        assertThat(allocationCount, equalTo(0));
        Pool<PooledItem> pool = createPool(1);
        assertThat(allocationCount, equalTo(1));

        PooledItem item = pool.grabNew();
        assertThat(allocationCount, equalTo(1));
        assertThat(item.getReleasedCount(), equalTo(0));

        pool.free(item);
        assertThat(item.getReleasedCount(), equalTo(1));

        item = pool.grabNew();
        assertThat(item.getReleasedCount(), equalTo(1));
        assertThat(allocationCount, equalTo(1));

        PooledItem allocatedItem = new PooledItem();
        assertThat(allocationCount, equalTo(2));
    }

    @Test
    public void poolRemainingCountsAreCorrect()  {
        Pool<PooledItem> pool = createPool(5);
        assertThat(pool.getRemainingCount(), equalTo(5));
        PooledItem item1 = pool.grabNew();

        assertThat(pool.getRemainingCount(), equalTo(4));
        PooledItem item2 = pool.grabNew();
        PooledItem item3 = pool.grabNew();
        PooledItem item4 = pool.grabNew();
        PooledItem item5 = pool.grabNew();

        assertThat(pool.getRemainingCount(), equalTo(0));

        pool.free(item1);
        assertThat(pool.getRemainingCount(), equalTo(1));

        pool.free(item2);
        pool.free(item3);
        pool.free(item4);
        pool.free(item5);
        assertThat(pool.getRemainingCount(), equalTo(5));

    }

    @Test
    public void freeingUnownedObjectThrowsException() {
        final Pool<PooledItem> pool = createPool(1);
        assertException("Pool can't free what it doesn't own", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                pool.free(new PooledItem());
            }
        });

        final PooledItem item = pool.grabNew();
        pool.free(item);
        assertException("Pool can't free the same item twice in a row", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                pool.free(item);
            }
        });

    }

    @Test
    public void goingOverCapacityThrowsException() {
        final Pool<PooledItem> pool = createPool(3);
        pool.grabNew();
        pool.grabNew();
        pool.grabNew();
        assertException("Pool is out of capacity", IllegalStateException.class, new Action() {
            @Override
            public void run() {
                pool.grabNew();
            }
        });
    }

    @Test
    public void invalidCapacityThrowsException() {
        assertException("Pool can't be instantiated with no capacity", IllegalArgumentException.class, new Action() {
            @Override
            public void run() {
                createPool(0);
            }
        });

        assertException("Pool can't be instantiated with negative capacity", IllegalArgumentException.class,
            new Action() {
                @Override
                public void run() {
                    createPool(-5);
                }
            });
    }

    private Pool<PooledItem> createPool(int capacity) {
        return new Pool<PooledItem>(new Pool.AllocateMethod<PooledItem>() {
            @Override
            public PooledItem run() {
                return new PooledItem();
            }
        }, new Pool.ResetMethod<PooledItem>() {
            @Override
            public void run(final PooledItem item) {
                item.release();
            }
        }, capacity);
    }
}