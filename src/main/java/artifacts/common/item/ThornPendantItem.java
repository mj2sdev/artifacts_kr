package artifacts.common.item;

import artifacts.common.config.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class ThornPendantItem extends PendantItem {

    public ThornPendantItem() {
        super("thorn_pendant");
    }

    @Override
    protected void applyEffect(LivingEntity target, LivingEntity attacker) {
        if (attacker.attackable()) {
            int minDamage = ModConfig.server.thornPendant.minDamage.get();
            int maxDamage = ModConfig.server.thornPendant.maxDamage.get();
            attacker.hurt(DamageSource.thorns(target), minDamage + target.getRandom().nextInt(maxDamage - minDamage + 1));
        }
    }
}
