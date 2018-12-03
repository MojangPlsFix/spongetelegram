package io.github.fabrossmann.spongetelegram.spongetelegram.commands;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;

import java.util.List;
import java.util.stream.Collectors;

public class CommandTelegram implements CommandExecutor {
    /**
     * Callback for the execution of a command.
     *
     * @param src  The commander who is executing this command
     * @param args The parsed command arguments for this command
     * @return the result of executing this command
     * @throws CommandException If a user-facing error occurs while
     *                          executing this command
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            ItemStack sword = ItemStack.builder().itemType(ItemTypes.DIAMOND_SWORD).build();

            EnchantmentData enchantmentData = sword.getOrCreate(EnchantmentData.class).get();
            final List<EnchantmentType> enchantments = Sponge.getRegistry().getAllOf(EnchantmentType.class).stream().collect(Collectors.toList());

            for (EnchantmentType enchantment : enchantments) {
                enchantmentData.set(enchantmentData.enchantments().add(Enchantment.of(enchantment, 1000)));
            }
            sword.offer(enchantmentData);

            sword.offer(Keys.DISPLAY_NAME, Text.of(TextColors.BLUE, "Telegram", TextColors.DARK_BLUE, "SWORD"));

            sword.offer(Keys.UNBREAKABLE, true);

            Location<World> spawnLocation = player.getLocation();
            Extent extent = spawnLocation.getExtent();
            Entity item = extent.createEntity(EntityTypes.ITEM, spawnLocation.getPosition());
            item.offer(Keys.REPRESENTED_ITEM, sword.createSnapshot());

            try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLACEMENT);
                extent.spawnEntity(item);
            }

            player.playSound(SoundTypes.ENTITY_ENDERDRAGON_GROWL , player.getLocation().getPosition() ,2);

            ParticleEffect effect1 = ParticleEffect.builder().type(ParticleTypes.ENCHANTING_GLYPHS).quantity(10).build();
            ParticleEffect effect2 = ParticleEffect.builder().type(ParticleTypes.FLAME).quantity(20).build();

            Vector3d l=player.getLocation().getPosition();
            for (double t = 0; t < 2 * Math.PI; t += 0.39) {
                player.spawnParticles(effect1,new Vector3d(l.getX()+(1 * Math.cos(t)), l.getY()+1,l.getZ() + (1 * Math.sin(t))));
                player.spawnParticles(effect2,new Vector3d(l.getX()+(1 * Math.cos(t)), l.getY()+2,l.getZ() + (1 * Math.sin(t))));
            }



        }
        return CommandResult.success();
    }
}
