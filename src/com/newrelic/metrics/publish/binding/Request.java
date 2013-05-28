package com.newrelic.metrics.publish.binding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

public class Request {
	
	private final Context context;
	private final HashMap<ComponentData, LinkedList<MetricData>> metrics = new HashMap<ComponentData, LinkedList<MetricData>>(); 
	private final int duration;
	
	public Request(Context context, int duration) {
		super();
		this.context = context;
		this.duration = duration;
	}
	
	public int getDuration() {
		return duration;
	}	

	public MetricData addMetric(ComponentData component, String name, int value) {
		return addMetric(component, new MetricData(name, value));
	}
	
	public MetricData addMetric(ComponentData component, String name, float value) {
		return addMetric(component, new MetricData(name, value));
	}
	
    public void send() {
        HttpURLConnection connection = null;
        Logger logger = Context.getLogger();
        
        try {
            connection = context.createUrlConnectionForOutput();
            
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            try {
            	Map<String, Object> data = serialize();
            	
            	String json = JSONObject.toJSONString(data);
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Sending JSON: " + json);
                }
            	
                out.write(json);
            } finally {
                out.close();
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    logger.finest("Server response: " + inputLine);
                }
            } finally {
                in.close();
            }
        } 
        catch (Exception ex) 
        {
            logger.severe("An error occurred communicating with the New Relic service - " + ex.getMessage());
            logger.log(Level.FINE, ex.getMessage(), ex);

            if (connection != null) {
                try {
                   logger.info("Response: " + connection.getResponseCode() + " : " + connection.getResponseMessage() );
                 } catch (IOException e) {
                    logger.log(Level.FINER, ex.getMessage(), ex);
                }
            }
        } finally {
            connection.disconnect();
        }
    }
    
	/* package */ Map<String, Object> serialize() {		
		return context.serialize(this);
	}	

	/* package */ LinkedList<MetricData> getMetrics(ComponentData component) {
		if( ! metrics.containsKey(component)) {
			metrics.put(component, new LinkedList<MetricData>() );
		}
		return metrics.get(component);
	}
	
	private MetricData addMetric(ComponentData component, MetricData metric) {
		Context.getLogger().finest(component.getGUID() + " " + metric.name + ":" + metric.value);
		getMetrics(component).add(metric);
		return metric;
	}
}
