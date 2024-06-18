function initializeCoreMod()
{
    return {
        "VibrationSystemHook": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.world.level.gameevent.vibrations.VibrationSystem$Ticker",
                "methodName": "receiveVibration",
                "methodDesc": "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$Data;Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$User;Lnet/minecraft/world/level/gameevent/vibrations/VibrationInfo;)Z"
            },
            "transformer": function(method)
            {
                print("[ImprovableSkills]: Patching VibrationSystem$Ticker.receiveVibration");

                var owner = "org/zeith/improvableskills/proxy/ASMProxy";
                var name = "cancelVibrationReception";
                var desc = "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$Data;Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$User;Lnet/minecraft/world/level/gameevent/vibrations/VibrationInfo;)Z";

                var instr = method.instructions;
                var ASMAPI = Java.type('net.neoforged.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var Label = Java.type('org.objectweb.asm.Label');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var label = new Label();
				var labelNode = new LabelNode(label);

				var list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                list.add(new VarInsnNode(Opcodes.ALOAD, 2));
                list.add(new VarInsnNode(Opcodes.ALOAD, 3));
                list.add(ASMAPI.buildMethodCall(owner, name, desc, ASMAPI.MethodType.STATIC));
                list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                list.add(new InsnNode(Opcodes.ICONST_0));
                list.add(new InsnNode(Opcodes.IRETURN));
                list.add(labelNode);

                instr.insert(instr.get(0), list);

                return method;
            }
        }
    };
}