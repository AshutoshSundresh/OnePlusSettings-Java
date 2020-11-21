package com.oneplus.security.firewall;

public class FirewallRule {
    private Integer id;
    private Integer mobile;
    private String pkg;
    private Integer wlan;

    public FirewallRule() {
    }

    public FirewallRule(String str, Integer num, Integer num2) {
        this.pkg = str;
        this.wlan = num;
        this.mobile = num2;
    }

    public FirewallRule(Integer num, String str, Integer num2, Integer num3) {
        this.id = num;
        this.pkg = str;
        this.wlan = num2;
        this.mobile = num3;
    }

    public String getPkg() {
        return this.pkg;
    }

    public Integer getWlan() {
        return this.wlan;
    }

    public Integer getMobile() {
        return this.mobile;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FirewallRule)) {
            return false;
        }
        FirewallRule firewallRule = (FirewallRule) obj;
        if (!this.pkg.equals(firewallRule.pkg) || !this.mobile.equals(firewallRule.mobile) || !this.wlan.equals(firewallRule.wlan)) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007a, code lost:
        if (r6.getContentResolver().update(com.oneplus.security.database.Const.URI_NETWORK_RESTRICT, r0, "pkg = ? ", new java.lang.String[]{r7.getPkg()}) > 0) goto L_0x007e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean addOrUpdateRole(android.content.Context r6, com.oneplus.security.firewall.FirewallRule r7) {
        /*
        // Method dump skipped, instructions count: 139
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.security.firewall.FirewallRule.addOrUpdateRole(android.content.Context, com.oneplus.security.firewall.FirewallRule):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<com.oneplus.security.firewall.FirewallRule> selectAllFirewallRules(android.content.Context r8) {
        /*
        // Method dump skipped, instructions count: 142
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.security.firewall.FirewallRule.selectAllFirewallRules(android.content.Context):java.util.List");
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Map<java.lang.String, com.oneplus.security.firewall.FirewallRule> selectAllFirewallRulesAsMap(android.content.Context r8) {
        /*
        // Method dump skipped, instructions count: 142
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.security.firewall.FirewallRule.selectAllFirewallRulesAsMap(android.content.Context):java.util.Map");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0064, code lost:
        if (r7 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0074, code lost:
        if (r7 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0076, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0079, code lost:
        return null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.oneplus.security.firewall.FirewallRule selectFirewallRuleByPkg(android.content.Context r7, java.lang.String r8) {
        /*
        // Method dump skipped, instructions count: 130
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.security.firewall.FirewallRule.selectFirewallRuleByPkg(android.content.Context, java.lang.String):com.oneplus.security.firewall.FirewallRule");
    }

    public String toString() {
        return "FirewallRule [id=" + this.id + ", pkg=" + this.pkg + ", wlan=" + this.wlan + ", mobile=" + this.mobile + "]";
    }
}
