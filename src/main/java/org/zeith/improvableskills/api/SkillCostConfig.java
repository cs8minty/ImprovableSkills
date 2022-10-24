package org.zeith.improvableskills.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.hammerlib.util.mcf.LogicalSidePredictor;
import org.zeith.improvableskills.api.math.ExpressionEvaluator;
import org.zeith.improvableskills.api.math.functions.ExpressionFunction;

public class SkillCostConfig
		extends ExpressionFunction
{
	public static final String DEF_FORMULA = "(%lvl%+1)^%xpv%";
	public int xpValue;
	
	private String baseFormula = DEF_FORMULA, serverFormula, clientFormula;
	
	public SkillCostConfig(int xpValue)
	{
		super("skill");
		this.xpValue = xpValue;
	}
	
	public void setBaseFormula(String baseFormula)
	{
		this.baseFormula = this.serverFormula = this.clientFormula = baseFormula;
	}
	
	public void load(ConfigEntryCategory cfg, String skill)
	{
		serverFormula = cfg.getStringEntry(skill, baseFormula).setDescription("Cost calculator for this skill.\nAvailable variables:\n- %lvl% = the level we want to calculate XP value for.\n- %xpv% preset value (" + xpValue + ") for current skill.").getValue();
	}
	
	public void writeServerNBT(CompoundTag nbt)
	{
		if(serverFormula != null)
			nbt.putString("Formula", serverFormula);
	}
	
	public void readClientNBT(CompoundTag nbt)
	{
		resetClient();
		if(nbt.contains("Formula", Tag.TAG_STRING))
			clientFormula = nbt.getString("Formula");
	}
	
	public void resetClient()
	{
		clientFormula = baseFormula;
	}
	
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		if(clientFormula != null && LogicalSidePredictor.getCurrentLogicalSide().isClient())
		{
			String formula = this.clientFormula.replace("%lvl%", Short.toString(targetLvl)).replace("%xpv%", Integer.toString(xpValue));
			int val = (int) Math.ceil(ExpressionEvaluator.evaluateDouble(formula, this));
			return val;
		}
		
		if(serverFormula != null)
		{
			String formula = this.serverFormula.replace("%lvl%", Short.toString(targetLvl)).replace("%xpv%", Integer.toString(xpValue));
			int val = (int) Math.ceil(ExpressionEvaluator.evaluateDouble(formula, this));
			return val;
		}
		
		return (int) Math.pow(targetLvl + 1, xpValue);
	}
}