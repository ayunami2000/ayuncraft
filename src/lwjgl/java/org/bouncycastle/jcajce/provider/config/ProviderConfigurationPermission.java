package org.bouncycastle.jcajce.provider.config;

import java.security.BasicPermission;
import java.security.Permission;
import java.util.StringTokenizer;
import org.bouncycastle.util.Strings;

public class ProviderConfigurationPermission extends BasicPermission
{
    private final String actions;
    private final int permissionMask;

    public ProviderConfigurationPermission(String par1Str)
    {
        super(par1Str);
        this.actions = "all";
        this.permissionMask = 15;
    }

    public ProviderConfigurationPermission(String par1Str, String par2Str)
    {
        super(par1Str, par2Str);
        this.actions = par2Str;
        this.permissionMask = this.calculateMask(par2Str);
    }

    private int calculateMask(String par1Str)
    {
        StringTokenizer var2 = new StringTokenizer(Strings.toLowerCase(par1Str), " ,");
        int var3 = 0;

        while (var2.hasMoreTokens())
        {
            String var4 = var2.nextToken();

            if (var4.equals("threadlocalecimplicitlyca"))
            {
                var3 |= 1;
            }
            else if (var4.equals("ecimplicitlyca"))
            {
                var3 |= 2;
            }
            else if (var4.equals("threadlocaldhdefaultparams"))
            {
                var3 |= 4;
            }
            else if (var4.equals("dhdefaultparams"))
            {
                var3 |= 8;
            }
            else if (var4.equals("all"))
            {
                var3 |= 15;
            }
        }

        if (var3 == 0)
        {
            throw new IllegalArgumentException("unknown permissions passed to mask");
        }
        else
        {
            return var3;
        }
    }

    public String getActions()
    {
        return this.actions;
    }

    public boolean implies(Permission par1Permission)
    {
        if (!(par1Permission instanceof ProviderConfigurationPermission))
        {
            return false;
        }
        else if (!this.getName().equals(par1Permission.getName()))
        {
            return false;
        }
        else
        {
            ProviderConfigurationPermission var2 = (ProviderConfigurationPermission)par1Permission;
            return (this.permissionMask & var2.permissionMask) == var2.permissionMask;
        }
    }

    public boolean equals(Object par1Obj)
    {
        if (par1Obj == this)
        {
            return true;
        }
        else if (!(par1Obj instanceof ProviderConfigurationPermission))
        {
            return false;
        }
        else
        {
            ProviderConfigurationPermission var2 = (ProviderConfigurationPermission)par1Obj;
            return this.permissionMask == var2.permissionMask && this.getName().equals(var2.getName());
        }
    }

    public int hashCode()
    {
        return this.getName().hashCode() + this.permissionMask;
    }
}
