package com.mrsmartguy.logisticsducts.roles;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import cofh.thermaldynamics.duct.Attachment;
import cofh.thermaldynamics.duct.tiles.TileGrid;

/**
 * Factory class that maintains a registry to create LogisticRole subtypes at runtime.
 */
public class LDRoleRegistry {

	// Mapping of role names to constructors
	private final static Map<String, Supplier<LogisticsRole>> REGISTRY = new LinkedHashMap<>();
	// Sorted list of role names, used to convert name to index
	private final static LinkedList<String> roleNames = new LinkedList<String>();
	
	/**
	 * Adds all of the roles in LogisticsDucts to the registry.
	 */
	public static void registerRoles()
	{
		registerRole(RoleAcceptor::new);
		registerRole(RoleCrafter::new);
		registerRole(RoleExtractor::new);
		registerRole(RoleProvider::new);
		registerRole(RoleRequester::new);
		registerRole(RoleStockKeeper::new);
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
			roleNames.add(roleName);
			Collections.sort(roleNames);
		}
	}
	
	/**
	 * Creates a role given a role name.
	 * @param roleName The name of the role to create (retrieved from LogisticsRole::getName).
	 * @return The constructed LogisticsRole.
	 */
	public static LogisticsRole createRole(String roleName)
	{
		if (REGISTRY.containsKey(roleName))
		{
			return REGISTRY.get(roleName).get();
		}
		throw new RuntimeException("The given LogisticsRole name is not registered.");
	}
	
	/**
	 * Creates a role given a role index.
	 * @param roleIndex The index of the role to create (retrieved from LDRoleRegistry::getRoleIndex)
	 * @return The constructed LogisticsRole.
	 */
	public static LogisticsRole createRole(int roleIndex)
	{
		if (roleIndex < roleNames.size() && roleIndex >= 0)
		{
			return createRole(roleNames.get(roleIndex));
		}
		return null;
	}
	
	/**
	 * Returns a set of all role names
	 * @return The set of all role names
	 */
	public static Set<String> getRoleNames()
	{
		return REGISTRY.keySet();
	}
	
	/**
	 * Gets the index of the role corresponding to the given name.
	 * @param roleName The name of the role to get the index of
	 * @return The index of the given role
	 */
	public static int getRoleIndex(String roleName)
	{
		return roleNames.indexOf(roleName);
	}
	
	/**
	 * Gets the index of the given role.
	 * @param role The role to get the index of
	 * @return The index of the given role
	 */
	public static int getRoleIndex(LogisticsRole role)
	{
		return role != null ? getRoleIndex(role.getName()) : -1;
	}

}
