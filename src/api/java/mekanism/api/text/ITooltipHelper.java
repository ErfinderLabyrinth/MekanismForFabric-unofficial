package mekanism.api.text;

import mekanism.api.security.ISecurityUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.ServiceLoader;

/**
 * Interface mostly meant as a way to provide us a way to access some internal formatting helpers for some tooltips that are defined in the API. These methods are
 * intentionally not documented as they should not really be relied on and may change at any time.
 */
@Internal
public interface ITooltipHelper {

    ITooltipHelper INSTANCE = ServiceLoader.load(ITooltipHelper.class, ITooltipHelper.class.getClassLoader()).findFirst().orElseThrow(() -> new IllegalStateException("No valid ServiceImpl for ITooltipHelper found"));

    Component getEnergyPerMBDisplayShort(long energy);

    Component getRadioactivityDisplayShort(double radioactivity);

    String getFormattedNumber(long number);

    Component getPercent(double ratio);
}