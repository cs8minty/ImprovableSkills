function initializeCoreMod()
{
    return {
        "ItemStackHurtHook": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.world.item.ItemStack",
                "methodName": "m_41622_",
                "methodDesc": "(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"
            },
            "transformer": function(method)
            {
                print("[ImprovableSkills]: Patching ItemStack.hurtAndBreak");

                var owner = "org/zeith/improvableskills/proxy/ASMProxy";
                var name = "hurtItem";
                var desc = "(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)I";

                var instr = method.instructions;
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
				var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

				var list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                list.add(new VarInsnNode(Opcodes.ILOAD, 1));
                list.add(new VarInsnNode(Opcodes.ALOAD, 2));
                list.add(new VarInsnNode(Opcodes.ALOAD, 3));
                list.add(ASMAPI.buildMethodCall(owner, name, desc, ASMAPI.MethodType.STATIC));
                list.add(new VarInsnNode(Opcodes.ISTORE, 1));

                instr.insert(instr.get(0), list);

                return method;
            }
        }
    };
}