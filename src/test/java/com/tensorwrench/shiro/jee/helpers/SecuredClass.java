package com.tensorwrench.shiro.jee.helpers;


import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;

import com.tensorwrench.shiro.jee.annotations.Secured;

@Secured
public class SecuredClass {
	@RequiresUser	public boolean requiresUser() {return true;}
	@RequiresAuthentication	public boolean requiresAuthentication() {return true;}
	@RequiresGuest	public boolean requiresGuest() {return true;}
	
	@RequiresRoles("USER")	public boolean requiresRoleUser() {return true;}
	@RequiresRoles("USER")	public boolean requiresRoleAdmin() {return true;}
	@RequiresRoles(value={"USER","ADMIN"},logical=Logical.AND)	public boolean requiresRoleUserAndAdmin() {return true;}
	@RequiresRoles(value={"USER","ADMIN"},logical=Logical.OR)	public boolean requiresRoleUserOrAdmin() {return true;}
	
	
	@RequiresPermissions("doc:read:1")	public boolean requiresPermissionDoc1() {return true;}
	@RequiresPermissions(value={"doc:read:1","doc:read:2"},logical=Logical.AND)	public boolean requiresPermissionDoc1AndDoc2() {return true;}
	@RequiresPermissions(value={"doc:read:1","doc:read:2"},logical=Logical.OR)	public boolean requiresPermissionDoc1OrDoc2() {return true;}
}
