package me.sheaksadi.villagerutils;


import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

public class BlockUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();



    public static boolean breakBlock(BlockPos blockPos){
        switchToTool(blockPos);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert MinecraftClient.getInstance().interactionManager != null;
        assert player != null;


        boolean value = MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(blockPos, Direction.DOWN);
        player.swingHand(Hand.MAIN_HAND);
        if (!value) {
            VillagerUtils.stage=3;
        }
        if (player.isCreative()&&mc.world.getBlockState(blockPos).isAir()) VillagerUtils.stage=3;
        return value;
    }

    public static boolean placeBlock (BlockPos blockPos){
        if (!mc.world.getBlockState(blockPos).isAir()){
            VillagerUtils.stage=1;
        }
        switchToWorkStation();
        Vec3d hitpos = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        BlockPos neighbor;
        Direction side = getPlaceSide(blockPos);

        if(side == null){
            side = Direction.UP;
            neighbor = blockPos;
        }else{
            neighbor = blockPos.offset(side.getOpposite());
            hitpos.add(side.getOffsetX() * 0.5, side.getOffsetY() * 0.5, side.getOffsetZ() * 0.5);
        }
        Direction s = side;
        boolean placed =  place(blockPos, new BlockHitResult(hitpos, s, neighbor, false));
        if (placed){
            VillagerUtils.stage=1;
        }
        return placed;
    }

    public static void interactWithVillager(VillagerEntity entity){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert MinecraftClient.getInstance().interactionManager != null;
        assert player != null;

        ActionResult result = MinecraftClient.getInstance().interactionManager.interactEntity(player, entity, Hand.MAIN_HAND);
        if(result.isAccepted()){
            player.swingHand(Hand.MAIN_HAND);
        }
    }



    public static boolean getVillagerTrades() {
        if (lookingAtValidVillager()!= null&& lookingAtValidVillager()==VillagerProfession.LIBRARIAN) {
            interactWithVillager((VillagerEntity) mc.targetedEntity);

            Screen s = mc.currentScreen;

            if(s instanceof MerchantScreen screen){
                //VillagerUtils.LOGGER.info("screen is on");
                StringBuilder sb = new StringBuilder();
                for(TradeOffer offer : screen.getScreenHandler().getRecipes()){
                    //player.sendMessage(new LiteralText(offer.getSellItem().getItem().getName().getString() + ""), false);
                    if(sb.length() != 0) sb.append(" | ");
                    if(offer.getSellItem().getItem() == Items.ENCHANTED_BOOK){

                        NbtCompound enchantNBT = ((NbtCompound)((NbtList) offer.getSellItem().getNbt().get("StoredEnchantments")).get(0));
                        String id = enchantNBT.get("id").asString();
                        int lvl = Integer.parseInt(enchantNBT.get("lvl").asString().replace("s", ""));
                        sb.append(Registry.ENCHANTMENT.get(new Identifier(id)).getName(lvl).getString());
                        //player.sendMessage(new LiteralText(Registry.ENCHANTMENT.get(new Identifier(id)).getName(lvl).getString() + ""), false);
                        Enchantment enchantment = Registry.ENCHANTMENT.get(new Identifier(id));
                        if (VillagerUtils.enchantments.contains(enchantment)){
                            if (VillagerUtils.rollForMax){
                                assert enchantment != null;
                                if (lvl==enchantment.getMaxLevel()){
                                    VillagerUtils.enchantments.remove(enchantment);
                                    VillagerUtils.toggle();
                                }else {
                                    assert mc.player != null;
                                    mc.player.sendMessage(new LiteralText("not right level"),true);
                                }
                            }else {
                                VillagerUtils.enchantments.remove(enchantment);
                                VillagerUtils.toggle();
                            }
                        }
                        else VillagerUtils.stage=2;

                    }
                    else{
                        VillagerUtils.stage=2;
                        sb.append(offer.getSellItem().getItem().getName().getString());
                    }
                    sb.append(" (").append(offer.getOriginalFirstBuyItem().getCount()).append(")");
                }
                assert mc.player != null;
                mc.player.sendMessage(new LiteralText(sb.toString()),true);
//                mc.player.closeHandledScreen();
            }

            return true;
        }
        return false;
    }
    public static VillagerProfession lookingAtValidVillager() {
        VillagerEntity entity = (VillagerEntity) mc.targetedEntity;
        if (entity==null) return null;
        return  entity.getVillagerData().getProfession();
    }
    private static boolean place(BlockPos blockPos, BlockHitResult blockHitResult){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        boolean wasSneaking = player.input.sneaking;
        player.input.sneaking = false;

        ActionResult result = MinecraftClient.getInstance().interactionManager.interactBlock(player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, blockHitResult);

        if(result.shouldSwingHand()){
            player.swingHand(Hand.MAIN_HAND);
        }

        player.input.sneaking = wasSneaking;
        return result.isAccepted();
    }


    public static BlockPos getWorkstationPos() {
        assert mc.player != null;
        BlockPos playerPos = mc.player.getBlockPos();
        Direction facingDir = mc.player.getHorizontalFacing();
        BlockPos workstationPos = null;

        if (facingDir == Direction.EAST) {
            workstationPos = playerPos.add(1, 0, 0);
        }

        if (facingDir == Direction.WEST) {
            workstationPos = playerPos.add(-1, 0, 0);
        }

        if (facingDir == Direction.NORTH) {
            workstationPos = playerPos.add(0, 0, -1);
        }

        if (facingDir == Direction.SOUTH) {
            workstationPos = playerPos.add(0, 0, 1);
        }

        return workstationPos;
    }

    public static boolean canBreak(BlockPos blockPos, BlockState state) {
        assert mc.player != null;
        if (!mc.player.isCreative() && state.getHardness(mc.world, blockPos) < 0) return false;
        return state.getOutlineShape(mc.world, blockPos) != VoxelShapes.empty();
    }



    public static int getBestTool(BlockPos blockPos){
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        assert minecraftClient.world != null;
        assert minecraftClient.player != null;

        PlayerScreenHandler playerScreenHandler = new InventoryScreen(minecraftClient.player).getScreenHandler();
        BlockState state = minecraftClient.world.getBlockState(blockPos);

        int airIndex = -1, bestToolIndex = -1;
        float bestMiningSpeed = 1;

        for(int i = 0; i < playerScreenHandler.slots.size(); i++){
            ItemStack stack = playerScreenHandler.getSlot(i).getStack();
            if(stack.getMiningSpeedMultiplier(state) > bestMiningSpeed) {
                bestMiningSpeed = stack.getMiningSpeedMultiplier(state);
                bestToolIndex = i;
            }else if(stack.equals(ItemStack.EMPTY)){
                airIndex = i;
            }
        }
        return bestToolIndex > 1 ? bestToolIndex : airIndex;
    }

    public static void switchToTool(BlockPos blockPos){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player.isCreative();
        assert mc.interactionManager != null;

        int bestTool = getBestTool(blockPos);
        int selectedSlot = player.getInventory().selectedSlot + 36;
        InventoryScreen inv = new InventoryScreen(player);

        if(bestTool != -1) { //No Best Tool
            if(bestTool >= 36){
                if(PlayerInventory.isValidHotbarIndex(bestTool - 36))
                    player.getInventory().selectedSlot = bestTool - 36;

            }else {
                try{
                    mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, bestTool, 0, SlotActionType.PICKUP, player);
                    mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, selectedSlot, 0, SlotActionType.PICKUP, player);
                    mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, bestTool, 0, SlotActionType.PICKUP, player);
                }
                catch (Exception ignored){

                }

            }

        }

    }





    public static void switchToWorkStation(){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert mc.interactionManager != null;

        int slot = getWorkstationSlot(VillagerProfession.LIBRARIAN);
        InventoryScreen inv = new InventoryScreen(player);
        int selectedSlot = player.getInventory().selectedSlot + 36;

        if(slot != -1){
            if(slot >= 36){
                if(PlayerInventory.isValidHotbarIndex(slot - 36))
                    player.getInventory().selectedSlot = slot - 36;
            }else {

                mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, slot, 0, SlotActionType.PICKUP, player);
                mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, selectedSlot, 0, SlotActionType.PICKUP, player);
                mc.interactionManager.clickSlot(inv.getScreenHandler().syncId, slot, 0, SlotActionType.PICKUP, player);
            }
        }else{
            player.sendMessage(new TranslatableText("villagertradefindermod.error.noworkstation"), true);
        }

    }

    public static int getWorkstationSlot(VillagerProfession villProf){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        PlayerScreenHandler playerScreenHandler = new InventoryScreen(player).getScreenHandler();

        for(int i = 0; i < playerScreenHandler.slots.size(); i++){
            ItemStack stack = playerScreenHandler.getSlot(i).getStack();
            Block block = Block.getBlockFromItem(stack.getItem());
            BlockState state = block.getDefaultState();
            if(villProf.getWorkStation().contains(state)){
                return i;
            }
        }
        return -1;
    }
    private static Direction getPlaceSide(BlockPos blockPos) {
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            BlockState state = MinecraftClient.getInstance().world.getBlockState(neighbor);

            // Check if neighbour isn't empty
            if (state.isAir() || isClickable(state.getBlock())) continue;

            // Check if neighbour is a fluid
            if (!state.getFluidState().isEmpty()) continue;

            return side2;
        }

        return null;
    }

    public static boolean isClickable(Block block) {
        return block instanceof CraftingTableBlock
                || block instanceof AnvilBlock
                || block instanceof AbstractButtonBlock
                || block instanceof AbstractPressurePlateBlock
                || block instanceof BlockWithEntity
                || block instanceof BedBlock
                || block instanceof FenceGateBlock
                || block instanceof DoorBlock
                || block instanceof NoteBlock
                || block instanceof TrapdoorBlock;
    }

}
