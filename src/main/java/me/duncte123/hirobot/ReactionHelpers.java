/*
 * Custom bot for the Hiro Akiba fan server on discord
 * Copyright (C) 2021 Duncan "duncte123" Sterken
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

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
    public static final JsonMapper MAPPER = JsonMapper.builder()
        .disable(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        )
        .enable(
            JsonReadFeature.ALLOW_TRAILING_COMMA,
            JsonReadFeature.ALLOW_JAVA_COMMENTS,
            JsonReadFeature.ALLOW_YAML_COMMENTS,
            JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES
        )
        .build();

    public static void load() throws IOException {
        final ObjectNode d = (ObjectNode) MAPPER.readTree(new File("./data/roles_map.json5"));

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
