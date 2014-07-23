package dhcoder.support.memory;

import dhcoder.support.lambda.Action;
import dhcoder.test.TestUtils;
import org.junit.Test;

import java.util.UUID;

import static dhcoder.test.TestUtils.assertException;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class PoolTest {

    private class PooledItem {

        private UUID uniqueId = UUID.randomUUID();
        private int releasedCount;

        public UUID getUniqueId() {
            return uniqueId;
        }

        public int getReleasedCount() {
            return releasedCount;
        }

        public void release() {
            releasedCount++;
        }
    }

    @Test
    public void grabNewAvoidsAllocation() {
        Pool<PooledItem> pool = createPool(1);

        PooledItem item = pool.grabNew();
        UUID uniqueId = item.getUniqueId();
        assertThat(item.getReleasedCount(), equalTo(0));
        pool.free(item);

        item = pool.grabNew();
        // Assert we definitely have the old item, and that it wasn't reallocated
        assertThat(item.getReleasedCount(), equalTo(1));
        assertThat(item.getUniqueId(), equalTo(uniqueId));

        assertThat(new PooledItem().getUniqueId(), not(equalTo(uniqueId)));
    }

    @Test
    public void poolRemainingCountsAreCorrect() throws Exception {
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