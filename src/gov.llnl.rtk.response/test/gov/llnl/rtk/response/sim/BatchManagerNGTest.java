package gov.llnl.rtk.response.sim;

import static org.testng.Assert.*;
import org.testng.annotations.*;

public class BatchManagerNGTest
{
  BatchManager instance;
  MonteSim dummyMonteSim;

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
    // Minimal stub for MonteSim to allow BatchManager construction
    dummyMonteSim = new MonteSim()
    {
      @Override public double[] nextConfiguration()
      {
        return new double[]
        {
          1.0, 2.0, 3.0
        };
      }

      @Override public void draw(double[] values)
      {
        for (int i = 0; i < values.length; ++i)
          values[i] = i;
      }

      @Override public void setSeed(long l)
      {
      }

      @Override public int getConditionSize()
      {
        return 3;
      }

      @Override public int getDynamicsSize()
      {
        return 0;
      }
    };
    instance = new BatchManager(dummyMonteSim);
    // Use small sizes for fast test execution
    instance.samples = 5;
    instance.batches = 2;
    instance.over = 1;
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
    instance = null;
    dummyMonteSim = null;
  }

  @Test
  public void testSeed()
  {
    // Should not throw and should set seeds on random, nd, and monte
    instance.seed(12345L);
    // No assertion possible unless we expose the RNG state, but should not throw
  }

  @Test
  public void testInitial()
  {
    instance.initial();
    // After init, pools should be allocated and filled
    assertNotNull(instance.featurePool, "featurePool should not be null after initial()");
    assertNotNull(instance.x0Pool, "x0Pool should not be null after initial()");
    assertNotNull(instance.x1Pool, "x1Pool should not be null after initial()");
    assertEquals(instance.count, instance.samples * instance.batches, "Pool should be filled to capacity");
  }

  @Test
  public void testChangeConfiguration()
  {
    instance.changeConfiguration();
    assertNotNull(instance.batchFeatures, "batchFeatures should not be null after changeConfiguration()");
    assertEquals(instance.batchFeatures.length, 3, "batchFeatures length should match dummyMonteSim");
  }

  @Test
  public void testDrawSamples()
  {
    // Should fill tmp0 and tmp1, and return true
    instance.initial(); // to allocate tmp0/tmp1
    boolean result = instance.drawSamples();
    assertTrue(result, "drawSamples should return true with dummyMonteSim");
    // Check that tmp0 and tmp1 are filled
    for (int i = 0; i < instance.tmp0.length; ++i)
    {
      assertTrue(instance.tmp0[i] >= 0.0 && instance.tmp0[i] <= 1.0, "tmp0 values should be in [0,1]");
    }
  }

  @Test
  public void testSampleSubSet()
  {
    instance.initial();
    int prevCount = instance.count;
    // Fill tmp0/tmp1 with values
    for (int i = 0; i < instance.tmp0.length; ++i)
    {
      instance.tmp0[i] = i * 0.1;
      instance.tmp1[i] = i * 0.2;
    }
    instance.batchFeatures = new double[]
    {
      1.0, 2.0, 3.0
    };
    instance.count = 0;
    instance.sampleSubSet();
    assertTrue(instance.count > 0, "sampleSubSet should increase count");
    assertNotNull(instance.featurePool[0], "featurePool should be filled");
  }

  @Test
  public void testFill()
  {
    // not testable with this harness.
   }

  @Test
  public void testFetch()
  {
    instance.initial();
    int batch = 2;
    instance.fetch(batch);
    assertNotNull(instance.features, "features should not be null after fetch()");
    assertNotNull(instance.rank, "rank should not be null after fetch()");
    assertNotNull(instance.length, "length should not be null after fetch()");
    assertEquals(instance.features.length, batch, "features length should match batch");
    assertEquals(instance.rank.length, batch, "rank length should match batch");
    assertEquals(instance.length.length, batch, "length length should match batch");
  }
}
