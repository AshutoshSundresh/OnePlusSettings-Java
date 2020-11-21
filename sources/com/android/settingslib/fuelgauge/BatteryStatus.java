package com.android.settingslib.fuelgauge;

import android.content.Context;
import android.content.Intent;
import com.android.settingslib.R$integer;

public class BatteryStatus {
    public final int health;
    public final int level;
    public final int maxChargingWattage;
    public final int plugged;
    public final int status;

    public BatteryStatus(Intent intent) {
        this.status = intent.getIntExtra("status", 1);
        this.plugged = intent.getIntExtra("plugged", 0);
        this.level = intent.getIntExtra("level", 0);
        this.health = intent.getIntExtra("health", 1);
        int intExtra = intent.getIntExtra("max_charging_current", -1);
        int intExtra2 = intent.getIntExtra("max_charging_voltage", -1);
        intExtra2 = intExtra2 <= 0 ? 5000000 : intExtra2;
        if (intExtra > 0) {
            this.maxChargingWattage = (intExtra / 1000) * (intExtra2 / 1000);
        } else {
            this.maxChargingWattage = -1;
        }
    }

    public boolean isCharged() {
        return this.status == 5 || this.level >= 100;
    }

    public int getChargingSpeed(Context context) {
        context.getResources().getInteger(R$integer.config_chargingSlowlyThreshold);
        context.getResources().getInteger(R$integer.config_chargingFastThreshold);
        return this.maxChargingWattage <= 0 ? -1 : 1;
    }

    public String toString() {
        return "BatteryStatus{status=" + this.status + ",level=" + this.level + ",plugged=" + this.plugged + ",health=" + this.health + ",maxChargingWattage=" + this.maxChargingWattage + "}";
    }
}
