package me.duncte123.hirobot;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class ReactionHelpers {
    // reaction -> role
    private static final TLongLongMap ROLES_MAP = new TLongLongHashMap();

    static {
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
    }

    public static void applyRole(long emoteId, Member member) {
        final Guild guild = member.getGuild();
        final Role role = guild.getRoleById(ROLES_MAP.get(emoteId));

        if (role == null) {
            return;
        }

        guild.addRoleToMember(member, role).queue();
    }

    public static void removeRole(long emoteId, Member member) {
        final Guild guild = member.getGuild();
        final Role role = guild.getRoleById(ROLES_MAP.get(emoteId));

        if (role == null) {
            return;
        }

        guild.removeRoleFromMember(member, role).queue();
    }
}
