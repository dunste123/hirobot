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
        ROLES_MAP.put(514293667041771531L, 0L);
        // Hunter
        ROLES_MAP.put(514294078570102784L, 0L);
        // Natsumi
        ROLES_MAP.put(514293667192766465L, 0L);
        // Yoichi
        ROLES_MAP.put(514293667595419663L, 0L);
        // Taiga
        ROLES_MAP.put(514293667507208193L, 0L);
        // Seto
        ROLES_MAP.put(514293667205349377L, 0L);
        // Felix
        ROLES_MAP.put(514293667247423498L, 0L);
        // Lee
        ROLES_MAP.put(586763383806754816L, 0L);
        // Eduard
        ROLES_MAP.put(514293667234709519L, 0L);
        // Kieran
        ROLES_MAP.put(652587591115472907L, 0L);
    }

    public static void applyRole(long emoteId, Member member) {
        final Guild guild = member.getGuild();
        final Role role = guild.getRoleById(ROLES_MAP.get(emoteId));

        if (role == null) {
            return;
        }

        guild.addRoleToMember(member, role).queue();
    }
}
