package com.mrsmartguy.logisticsducts.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

public class LogisticsNetwork {
	
	private Set<ILogisticator> endpoints;
	
	public LogisticsNetwork()
	{
		endpoints = new HashSet<ILogisticator>();
	}
	
	/**
	 * Gets all Logisticators on the network.
	 * @return An unmodifiable Set of all Logisticators on the network
	 */
	public Set<ILogisticator> getEndpoints()
	{
		return Collections.unmodifiableSet(endpoints);
	}
	
	/**
	 * Adds a logisticator to the network.
	 * @param endpoint The logisticator to add to the network
	 */
	public void addEndpoint(ILogisticator endpoint)
	{
		endpoints.add(endpoint);
	}
	
	/**
	 * Merges the contents of another LogisticsNetwork into this one.
	 * @param other The LogisticsNetwork object to merge into this one
	 */
	public void merge(LogisticsNetwork other)
	{
		for (ILogisticator endpoint : other.endpoints)
		{
			endpoints.add(endpoint);
		}
	}

}
