package artifacts.common.config.item;

import artifacts.common.init.ModItems;
import net.minecraftforge.common.ForgeConfigSpec;

public class WhoopeeCushionConfig extends ItemConfig {

    public ForgeConfigSpec.DoubleValue flatulence;

    public WhoopeeCushionConfig(ForgeConfigSpec.Builder builder) {
        super(builder, ModItems.WHOOPEE_CUSHION.get(), "Affects how many farts can be farted using the whoopee cushion");
    }

    @Override
    public void addConfigs(ForgeConfigSpec.Builder builder) {
        flatulence = builder
                .comment("Affects the amount of farts farted by players wearing the whoopee cushion")
                .translation(translate("flatulence"))
                .defineInRange("flatulence", 0.125, 0, 1);
    }
}
