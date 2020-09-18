package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import org.bukkit.entity.Player;

public final class ClanHelpCommand extends ClanCommand {

    public ClanHelpCommand() {
        super("help");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length == 1) {
            if (args[0].equals("2")) {
                this.sendHelp(player, 1);
            } else {
                this.sendHelp(player, 0);
            }
        } else {
            this.sendHelp(player, 0);
        }
    }

    private void sendHelp(Player player, int pageId) {
        if (pageId == 1) {
            this.utils.sendMessage(player, "§fClan-Hilfe §8┃ §8(§f2§8/§f2§8)");
            this.utils.sendMessage(player, " §8/§fcc §8<§fNachricht§8> §8● §7Schreibe in den Clan-Chat");
            this.utils.sendMessage(player, " §8/§fclan info §8● §7Informationen über deinen aktuellen Clan");
            this.utils.sendMessage(player, " §8/§fclan uinfo §8● §7Informationen über den Clan eines Spielers");
            this.utils.sendMessage(player, " §8/§fclan tinfo §8● §7Informationen über einen Clankürzel");
            this.utils.sendMessage(player, " §8/§fclan delete §8● §7Lösche deinen Clan");
            this.utils.sendMessage(player, " §8/§fclan join §8● §7Nimm eine Claneinladung an");
            this.utils.sendMessage(player, " §8/§fclan deny §8● §7Lehne eine Claneinladung ab");
            this.utils.sendMessage(player, " §8/§fclan jump §8● §7Springe einem Clanmitglied nach");
        } else {
            this.utils.sendMessage(player, "§fClan-Hilfe §8┃ §8(§f1§8/§f2§8)");
            this.utils.sendMessage(player, " §8/§fclan create §8● §7Erstelle einen Clan");
            this.utils.sendMessage(player, " §8/§fclan rename §8● §7Benenne deinen Clan um");
            this.utils.sendMessage(player, " §8/§fclan invite §8● §7Lade einen Spieler in den Clan ein");
            this.utils.sendMessage(player, " §8/§fclan kick §8● §7Kicke einen SPieler aus deinem Clan");
            this.utils.sendMessage(player, " §8/§fclan promote §8● §7Befördere einen Clan-Member");
            this.utils.sendMessage(player, " §8/§fclan demote §8● §7Degradiere einen Clan-Mod");
            this.utils.sendMessage(player, " §8/§fclan toggle §8● §7Stelle ein ob du Claneinladungen erhalten möchtest");
            this.utils.sendMessage(player, " §8/§fclan party §8● §7Lade alle Clanmember in eine Party ein");
            this.utils.sendMessage(player, " §8/§fclan leave §8● §7Verlasse einen Clan");
        }
    }
}
