package org.bouncycastle.jce.provider;

import java.security.PrivilegedAction;

class BouncyCastleProviderAction implements PrivilegedAction
{
    final BouncyCastleProvider theBouncyCastleProvider;

    BouncyCastleProviderAction(BouncyCastleProvider par1BouncyCastleProvider)
    {
        this.theBouncyCastleProvider = par1BouncyCastleProvider;
    }

    public Object run()
    {
        BouncyCastleProvider.doSetup(this.theBouncyCastleProvider);
        return null;
    }
}
