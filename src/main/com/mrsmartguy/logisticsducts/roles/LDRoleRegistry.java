package com.mrsmartguy.logisticsducts.roles;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import cofh.thermaldynamics.duct.Attachment;
import cofh.thermaldynamics.duct.tiles.TileGrid;

/**
 * Factory class that maintains a registry to create LogisticRole subtypes at runtime.
 */
public class LDRoleRegistry {

	private final static Map<String, Supplier<LogisticsRole>> REGISTRY = new HashMap<>();
	
	/**
	 * Adds all of the roles in LogisticsDucts to the registry.
	 */
	public static void registerRoles()
	{
		registerRole(RoleAcceptor::new);
		registerRole(RoleExtractor::new);
		registerRole(RoleProvider::new);
	}
	
	/**
	 * Registers a role to the registry.
	 * @param supplier The constructor for the given LogisticsRole subtype.
	 */
	public static void registerRole(Supplier<LogisticsRole> supplier)
	{
		String roleName = supplier.get().getName();
		if (!REGISTRY.containsKey(roleName))
		{
			REGISTRY.put(roleName, supplier);
		}
	}
	
	/**
	 * Creates a role given a role name.
	 * @param roleName The name of the role to create (retrieved from LogisticsRole::getName()).
	 * @return The constructed LogisticsRole.
	 */
	public static LogisticsRole createRole(String roleName)
	{
		if (REGISTRY.containsKey(roleName))
		{
			return REGISTRY.get(roleName).get();
		}
		throw new RuntimeException("Illegal LogisticsRole name");
	}

}
