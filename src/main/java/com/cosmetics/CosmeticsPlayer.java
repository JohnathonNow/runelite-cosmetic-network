package com.cosmetics;

import net.runelite.api.Player;
import net.runelite.api.kit.KitType;

public class CosmeticsPlayer {
    public String name;
    public int head;
    public int body;
    public int cape;
    public int legs;
    public int neck;
    public int hand;
    public int ring;
    public int feet;
    public int weap;
    public int shld;
    public int jaws;
    public int hair;

    public CosmeticsPlayer(Player p) {
        int[] equipmentIds = p.getPlayerComposition().getEquipmentIds();
        name = p.getName();
        weap = equipmentIds[KitType.WEAPON.getIndex()];
        shld = equipmentIds[KitType.SHIELD.getIndex()];
        body = equipmentIds[KitType.TORSO.getIndex()];
        cape = equipmentIds[KitType.CAPE.getIndex()];
        legs = equipmentIds[KitType.LEGS.getIndex()];
        feet = equipmentIds[KitType.BOOTS.getIndex()];
        neck = equipmentIds[KitType.AMULET.getIndex()];
        head = equipmentIds[KitType.HEAD.getIndex()];
        hand = equipmentIds[KitType.HANDS.getIndex()];
        ring = equipmentIds[KitType.ARMS.getIndex()];
        jaws = equipmentIds[KitType.JAW.getIndex()];
        hair = equipmentIds[KitType.HAIR.getIndex()];
    }

    public void write(int[] equipmentIds) {
        equipmentIds[KitType.WEAPON.getIndex()] = weap >= 0? weap : equipmentIds[KitType.WEAPON.getIndex()];
        equipmentIds[KitType.SHIELD.getIndex()] = shld >= 0? shld : equipmentIds[KitType.SHIELD.getIndex()];
        equipmentIds[KitType.TORSO.getIndex()] = body >= 0? body : equipmentIds[KitType.TORSO.getIndex()];
        equipmentIds[KitType.CAPE.getIndex()] = cape >= 0? cape : equipmentIds[KitType.CAPE.getIndex()];
        equipmentIds[KitType.LEGS.getIndex()] = legs >= 0? legs : equipmentIds[KitType.LEGS.getIndex()];
        equipmentIds[KitType.BOOTS.getIndex()] = feet >= 0? feet : equipmentIds[KitType.BOOTS.getIndex()];
        equipmentIds[KitType.AMULET.getIndex()] = neck >= 0? neck : equipmentIds[KitType.AMULET.getIndex()];
        equipmentIds[KitType.HEAD.getIndex()] = head >= 0? head : equipmentIds[KitType.HEAD.getIndex()];
        equipmentIds[KitType.HANDS.getIndex()] = hand >= 0? hand : equipmentIds[KitType.HANDS.getIndex()];
        equipmentIds[KitType.ARMS.getIndex()] = ring >= 0? ring : equipmentIds[KitType.ARMS.getIndex()];
        equipmentIds[KitType.JAW.getIndex()] = jaws >= 0? jaws : equipmentIds[KitType.JAW.getIndex()];
        equipmentIds[KitType.HAIR.getIndex()] = hair >= 0? hair : equipmentIds[KitType.HAIR.getIndex()];

    }

}

