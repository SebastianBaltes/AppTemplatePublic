package functional.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import models.MvTestFeature;
import models.MvTestVariant;
import models.User;

import org.junit.BeforeClass;
import org.junit.Test;

import funcy.FunctionalTest;
import global.MvTest;

public class MvTestTest extends FunctionalTest {

	public static MvTestFeature feature0;
	
	@BeforeClass
	public static void setup() throws Exception {
		Long now = System.currentTimeMillis();
		feature0 = new MvTestFeature();
		feature0.setName("feature0");
		// 1 h before now
		feature0.setStartTime(new Timestamp(now-60*60*1000));
		// 1 h after now
		feature0.setEndTime(new Timestamp(now+60*60*1000));
		feature0.setLastUpdate(new Timestamp(now));
		MvTestVariant feature0Variant0 = new MvTestVariant();
		feature0Variant0.setName("feature0Variant0");
		feature0Variant0.setPercent(25.0);
		feature0Variant0.setIndex(0);
		feature0Variant0.setFeature(feature0);
		feature0Variant0.setLastUpdate(new Timestamp(now));		
		feature0.getVariants().add(feature0Variant0);
		MvTestVariant feature0Variant1 = new MvTestVariant();
		feature0Variant1.setName("feature0Variant1");
		feature0Variant1.setPercent(25.0);
		feature0Variant1.setIndex(1);
		feature0Variant1.setFeature(feature0);
		feature0Variant1.setLastUpdate(new Timestamp(now));		
		feature0.getVariants().add(feature0Variant1);
		MvTestVariant feature0Variant2 = new MvTestVariant();
		feature0Variant2.setName("feature0Variant2");
		feature0Variant2.setPercent(50.0);
		feature0Variant2.setIndex(2);
		feature0Variant2.setFeature(feature0);
		feature0Variant2.setLastUpdate(new Timestamp(now));		
		feature0.getVariants().add(feature0Variant2);
		feature0.save();
		MvTest.get().updateFeatures();
	}
	
	@Test
	public void testFeaturesSetup() throws Exception {
		List<MvTestFeature> features = MvTestFeature.find.all();
		assertEquals(1,features.size());
		MvTestFeature feature0 = features.get(0);
		assertEquals(3,feature0.getVariants().size());
	}
	
	@Test
	public void testVariants() throws Exception {
		assertEquals(null,MvTest.get().variant(fake(0.0), "feature1"));
		assertEquals("feature0Variant0",MvTest.get().variant(fake(0), "feature0"));
		assertEquals("feature0Variant0",MvTest.get().variant(fake(0.249), "feature0"));
		assertEquals("feature0Variant1",MvTest.get().variant(fake(0.251), "feature0"));
		assertEquals("feature0Variant1",MvTest.get().variant(fake(0.499), "feature0"));
		assertEquals("feature0Variant2",MvTest.get().variant(fake(0.501), "feature0"));
		assertEquals("feature0Variant2",MvTest.get().variant(fake(0.999), "feature0"));
		assertEquals("feature0Variant2",MvTest.get().variant(fake(100), "feature0"));
	}

	private static long fake(double percentFactor) {
		return feature0.getFakeRandomNumberMatchingGivenPercentageFactor(percentFactor);
	}

	@Test
	public void testUser() throws Exception {
		User u1 = new User();
		User u2 = new User();
		assertTrue(u1.getFixRandomNumber()!=u2.getFixRandomNumber());
		u1.setFixRandomNumber(fake(0.143473));
		assertEquals(null,u1.variant("feature1"));
		assertEquals("feature0Variant0",u1.variant("feature0"));
		assertEquals("feature0Variant0",u1.variant("feature0"));
		u1.setFixRandomNumber(fake(0.343473));
		assertEquals("feature0Variant1",u1.variant("feature0"));
	}

	@Test
	public void testActive() throws Exception {
		MvTestFeature feature0 = new MvTestFeature();
		Long now = System.currentTimeMillis();
		feature0.setName("feature0");
		feature0.setStartTime(new Timestamp(now+60*60*1000));
		feature0.setEndTime(new Timestamp(now+60*60*1000+100));
		feature0.setLastUpdate(new Timestamp(now));
		MvTestVariant feature0Variant0 = new MvTestVariant();
		feature0Variant0.setName("feature0Variant0");
		feature0Variant0.setPercent(100.0);
		feature0Variant0.setIndex(0);
		feature0Variant0.setFeature(feature0);
		feature0Variant0.setLastUpdate(new Timestamp(now));		
		feature0.getVariants().add(feature0Variant0);
		assertFalse(feature0.isCurrentlyActive());
		feature0.setStartTime(new Timestamp(now-60*60*1000));
		feature0.setEndTime(new Timestamp(now-60*60*1000+100));
		assertFalse(feature0.isCurrentlyActive());
		feature0.setStartTime(new Timestamp(now-60*60*1000));
		feature0.setEndTime(new Timestamp(now+60*60*1000+100));
		assertTrue(feature0.isCurrentlyActive());
	}
	
	@Test
	public void proveRandomUniformityOfBaseAlgorithm() {
		Random rnd = new Random(0);
		long[] c = new long[10];
		long a = rnd.nextLong();
		for (int i = 0; i < 10000; i++) {
			long b = rnd.nextLong();
			long r = a ^ b;
			double d = Math.abs(r)/((double)Long.MAX_VALUE);
			int x = (int)(d*10);
			c[x]++;
		}
		for (int i = 0; i < c.length; i++) {
			if (c[i]<900) {
				fail();
			}
		}
		
	}
	
}
