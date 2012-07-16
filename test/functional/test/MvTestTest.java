package functional.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.List;

import models.MvTestFeature;
import models.MvTestVariant;
import models.User;

import org.junit.BeforeClass;
import org.junit.Test;

import funcy.FunctionalTest;
import global.MvTest;

public class MvTestTest extends FunctionalTest {

	@BeforeClass
	public static void setup() throws Exception {
		Long now = System.currentTimeMillis();
		MvTestFeature feature0 = new MvTestFeature();
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArgument1() throws Exception {
		MvTest.get().variant(-0.1, "feature0");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArgument2() throws Exception {
		MvTest.get().variant(100.01, "feature0");
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
		assertEquals(null,MvTest.get().variant(0.0, "feature1"));
		assertEquals("feature0Variant0",MvTest.get().variant(0, "feature0"));
		assertEquals("feature0Variant0",MvTest.get().variant(0.249, "feature0"));
		assertEquals("feature0Variant1",MvTest.get().variant(0.251, "feature0"));
		assertEquals("feature0Variant1",MvTest.get().variant(0.499, "feature0"));
		assertEquals("feature0Variant2",MvTest.get().variant(0.501, "feature0"));
		assertEquals("feature0Variant2",MvTest.get().variant(0.999, "feature0"));
		assertEquals("feature0Variant2",MvTest.get().variant(100, "feature0"));
	}

	@Test
	public void testUser() throws Exception {
		User u = new User();
		assertTrue(u.getFixRandomNumber()>=0 && u.getFixRandomNumber()<=100);
		User u2 = new User();
		assertTrue(u.getFixRandomNumber()!=u2.getFixRandomNumber());
		u.setFixRandomNumber(0.143473);
		assertEquals(null,u.variant("feature1"));
		assertEquals("feature0Variant0",u.variant("feature0"));
		assertEquals("feature0Variant0",u.variant("feature0"));
		u.setFixRandomNumber(0.343473);
		assertEquals("feature0Variant1",u.variant("feature0"));
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
	
}
