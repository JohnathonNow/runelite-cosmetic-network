/*
 * Copyright (c) 2021, JohnathonNow <johnjwesthoff@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.cosmetics;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
@PluginDescriptor(
	name = "Cosmetics",
	description = "Allows users to customize their appearance",
	tags = {"cosmetics", "players"}
)
public class CosmeticsPlugin extends Plugin {
	public static String CHAT_COMMAND = "!cosmetics";
	private final int FREQUENCY = 3;

	@Inject
	private Client client;

	@Inject
	private CosmeticsConfig config;

	@Provides
	CosmeticsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CosmeticsConfig.class);
	}


	private boolean isPvp = false;
	private boolean wasPvp = false;
	private boolean enabled = false;
	private final CosmeticsCache cache = new CosmeticsCache();

	private final HashMap<String, int[]> preTransform = new HashMap<>();
	private final HashMap<String, int[]> postTransform = new HashMap<>();
	private int timer = 0;

	@Override
	protected void startUp()
	{
		enabled = true;
		process();
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (enabled && event.getMessage().toLowerCase().startsWith(CHAT_COMMAND) && !config.apiKey().isEmpty()) {
			for (Player p : client.getPlayers()) {
				if (p.getName()!= null && p.getName().equals(event.getName())) {
					wipe(p.getName(), p.getPlayerComposition().getEquipmentIds());
					cache.save(new CosmeticsPlayer(p), config.apiKey());
					break;
				}
			}
		}
	}

	@Override
	protected void shutDown()
	{
		enabled = false;
		process();
	}

	@Subscribe
	public void onGameTick(GameTick gt)
	{
		timer++;
		if ((isPvp == wasPvp && timer < FREQUENCY) || !enabled) {
			return;
		}
		timer = 0;
		process();
		cache.clear();
	}

	private void wipe(String name, int[] equipmentIds) {
		if (postTransform.containsKey(name) && !Arrays.equals(postTransform.get(name), equipmentIds)) {
			preTransform.put(name, equipmentIds.clone());
		}
		int[] newIds = preTransform.get(name);
		System.arraycopy(newIds, 0, equipmentIds, 0, newIds.length);
	}

	private void process() {
		try {
			ArrayList<String> allNames = new ArrayList<>();
			for (Player player : client.getPlayers()) {
				allNames.add(player.getName());
				PlayerComposition comp = player.getPlayerComposition();
				int[] equipmentIds = comp.getEquipmentIds();
				String name = player.getName();
				if (!preTransform.containsKey(name)) {
					preTransform.put(name, equipmentIds.clone());
				}
				if (isPvp || !enabled) {
					//in PvP we should _not_ show cosmetics
					wipe(name, equipmentIds);
				} else {
					if (postTransform.containsKey(name) && !Arrays.equals(postTransform.get(name), equipmentIds)) {
						preTransform.put(name, equipmentIds.clone());
					}
					CosmeticsPlayer p = cache.getCosmetics(player.getName());
					if (p != null) {
						p.write(equipmentIds);
					}
					postTransform.put(name, equipmentIds.clone());
				}
				comp.setHash();
			}
			cache.fillCache(allNames.toArray(new String[0]));
		}
		catch (Exception e) {
			log.debug("Sad: " + e.toString());
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {
		wasPvp = isPvp;
		isPvp = client.getVar(Varbits.PVP_SPEC_ORB) != 0;
	}

	@Subscribe
	public void onPlayerChanged(PlayerChanged event) {
		timer = FREQUENCY;
	}
}
