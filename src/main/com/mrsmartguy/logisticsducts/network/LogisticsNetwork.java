package com.mrsmartguy.logisticsducts.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

public class LogisticsNetwork {
	
	private Set<LogisticatorItem> endpoints;
	
	public LogisticsNetwork()
	{
		endpoints = new HashSet<LogisticatorItem>();
	}
	
	/**
	 * Gets all Logisticators on the network
	 * @return An unmodifiable Set of all Logisticators on the network
	 */
	public Set<LogisticatorItem> getEndpoints()
	{
		return Collections.unmodifiableSet(endpoints);
	}
	
	public void addEndpoint(LogisticatorItem endpoint)
	{
		endpoints.add(endpoint);
	}
	
	/**
	 * Merges the contents of another LogisticsNetwork into this one
	 * @param other The LogisticsNetwork object to merge into this one
	 */
	public void merge(LogisticsNetwork other)
	{
		for (LogisticatorItem endpoint : other.endpoints)
		{
			endpoints.add(endpoint);
		}
	}

}
