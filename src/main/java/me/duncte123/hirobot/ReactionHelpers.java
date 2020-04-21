/*
 * Custom bot for the Hiro Akiba fan server on discord
 * Copyright (C) 2020 Duncan "duncte123" Sterken
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.hirobot;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.io.File;
import java.io.IOException;

public class ReactionHelpers {
    // reaction -> role
    private static final TLongLongMap ROLES_MAP = new TLongLongHashMap();

    /*static {
        // Keitaro
        ROLES_MAP.put(514293667041771531L, 672512679625097269L);
        // Hunter
        ROLES_MAP.put(514294078570102784L, 672513213954392074L);
        // Natsumi
        ROLES_MAP.put(514293667192766465L, 672513281306525716L);
        // Yoichi
        ROLES_MAP.put(514293667595419663L, 672513040775643162L);
        // Taiga
        ROLES_MAP.put(514293667507208193L, 672514266988806172L);
        // Seto
        ROLES_MAP.put(514293667205349377L, 672513416455520287L);
        // Felix
        ROLES_MAP.put(514293667247423498L, 672513666897149982L);
        // Lee
        ROLES_MAP.put(586763383806754816L, 672513740951781425L);
        // Eduard
        ROLES_MAP.put(514293667234709519L, 672513848716296193L);
        // Kieran
        ROLES_MAP.put(652587591115472907L, 672514904976261186L);
        // Aiden
        ROLES_MAP.put(514293666936782850L, 672515881590587393L);
    }*/

    public static void load() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

        final ObjectNode d = mapper.readValue(new File("roles_map.json5"), ObjectNode.class);

        d.fieldNames().forEachRemaining((key) -> {
            final long longKey = Long.parseLong(key);

            ROLES_MAP.put(longKey, d.get(key).asLong());
        });
    }

    public static void applyRole(long emoteId, long userId, Guild guild) {
        final Role role = getRole(emoteId, guild);

        if (role == null) {
            return;
        }

        guild.addRoleToMember(userId, role).queue();
    }

    public static void removeRole(long emoteId, long userId, Guild guild) {
        final Role role = getRole(emoteId, guild);

        if (role == null) {
            return;
        }

        guild.removeRoleFromMember(userId, role).queue();
    }

    private static Role getRole(long emoteId, Guild guild) {
        final long roleId = ROLES_MAP.get(emoteId);

        if (roleId < 1) {
            return null;
        }

        return guild.getRoleById(roleId);
    }
}
