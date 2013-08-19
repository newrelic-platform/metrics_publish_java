package com.newrelic.metrics.publish.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

public class MetricDataTest {
    
    @Test
    public void testMetrics() {
        MetricData metric = new MetricData("test metric", 7.0f);

        assertEquals(1, metric.count);
        assertEquals(7.0f, metric.value, 0.00001);
        assertEquals(7.0f, metric.minValue, 0.00001);
        assertEquals(7.0f, metric.maxValue, 0.00001);
        assertEquals(49.0f, metric.sumOfSquares, 0.00001);
    }
    
    @Test
    public void testIntegerConversion() {
        MetricData metric = new MetricData("test metric", 10);
        
        assertEquals(1, metric.count);
        assertEquals(10.0f, metric.value, 0.00001);
        assertEquals(10.0f, metric.minValue, 0.00001);
        assertEquals(10.0f, metric.maxValue, 0.00001);
        assertEquals(100.0f, metric.sumOfSquares, 0.00001);
    }
    
    @Test
    public void testAggregateWith() {  
        MetricData first = new MetricData("test metric", 10.0f);
        first.aggregrateWith(new MetricData("test metric", 9.0f));

        assertEquals(2, first.count);
        assertEquals(19.0f, first.value, 0.00001);
        assertEquals(9.0f, first.minValue, 0.00001);
        assertEquals(10.0f, first.maxValue, 0.00001);
        assertEquals(181.0f, first.sumOfSquares, 0.00001);
    }

    @Test
    public void testSerialize() {
        MetricData metric = new MetricData("test metric", 10.0f);

        HashMap<String, Object> data = new HashMap<String, Object>();
        metric.serialize(data);

        HashMap<String, Object> expected = new HashMap<String, Object>();
        expected.put("test metric", Arrays.<Number>asList(10.0f, 1, 10.0f, 10.0f, 100.0f));
        
        assertEquals(expected, data);
    }
    
    @Test
    public void testEquals() {
        MetricData metric = new MetricData("test metric", 5);
        MetricData other = new MetricData("test metric", 7);
        
        assertEquals(metric, metric);
        assertEquals(metric, other);
    }
    
    @Test
    public void testNotEquals() {
        MetricData metric = new MetricData("test metric", 5);
        MetricData other = new MetricData("different test metric", 5);
        
        assertFalse(metric.equals(other));
    }
}
