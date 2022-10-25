package org.zeith.improvableskills.custom.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.net.Network;
import org.zeith.hammerlib.util.XPUtil;
import org.zeith.improvableskills.api.PlayerSkillData;
import org.zeith.improvableskills.api.registry.PlayerAbilityBase;
import org.zeith.improvableskills.client.gui.abil.GuiAutoXpBank;
import org.zeith.improvableskills.data.PlayerDataManager;
import org.zeith.improvableskills.net.PacketSetAutoXpBankData;

import java.math.BigInteger;

public class AbilityAutoXpBank
		extends PlayerAbilityBase
{
	public AbilityAutoXpBank()
	{
		setColor(0x33FF00);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		if(data.autoXpBank && !data.player.level.isClientSide && data.atTickRate(2))
		{
			var threshold = data.autoXpBankThreshold;
			
			var playerXP = XPUtil.getXPTotal(data.player);
			if(playerXP > threshold)
			{
				int diff = Math.max(1, (int) Math.floor(Math.sqrt(playerXP - threshold)));
				XPUtil.takeXP(data.player, diff);
				data.storageXp = data.storageXp.add(BigInteger.valueOf(diff));
				data.sync();
			} else if(playerXP < threshold)
			{
				int diff = Math.max(1, (int) Math.floor(Math.sqrt(threshold - playerXP)));
				var diffBI = data.storageXp.min(BigInteger.valueOf(diff));
				XPUtil.giveXP(data.player, diffBI.intValue());
				data.storageXp = data.storageXp.subtract(diffBI);
				data.sync();
			}
		}
	}
	
	@Override
	public void onUnlocked(PlayerSkillData data)
	{
		data.autoXpBank = false;
		data.autoXpBankThreshold = XPUtil.getXPValueFromLevel(30);
	}
	
	@Override
	public boolean showDisabledIcon(PlayerSkillData data)
	{
		return !data.autoXpBank;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClickClient(Player player, int mouseButton)
	{
		PlayerDataManager.handleDataSafely(player, data ->
		{
			if(mouseButton == 1)
				Network.sendToServer(new PacketSetAutoXpBankData(data.autoXpBank = !data.autoXpBank));
			else if(mouseButton == 0)
				Minecraft.getInstance().pushGuiLayer(new GuiAutoXpBank(data));
		});
	}
}