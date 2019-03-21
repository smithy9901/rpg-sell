package ru.rpgsell;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class rpgsellCmd implements CommandExecutor {

	Main plugin;

	public rpgsellCmd(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args) {
		if (!cs.hasPermission("rpgsell.admin")) {
			cs.sendMessage("§cУ вас нет прав на использование этой команды!");
			return true;
		}
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				cs.sendMessage("§aConfig.yml перезагружен!");
				plugin.reloadItems();
				return true;
			} else if (args[0].equalsIgnoreCase("setprice")) {
				if (args.length > 1) {
					if (!(cs instanceof Player)) {
						cs.sendMessage("Вы должны быть игроком!");
						return true;
					}
					Player p = (Player) cs;
					if (p.getInventory().getItemInMainHand() != null) {
						if (p.getInventory().getItemInMainHand().getType() != null) {
							if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
								// item holding
								ItemStack inHand = p.getInventory().getItemInMainHand();
								double price = Double.parseDouble(args[1]);
								
								Optional<CustomItem> result = plugin.findCustomItem(inHand);
								if(result.isPresent()) {
									plugin.getCustomItems().remove(result.get());
									p.sendMessage("§aУспешно обновленный элемент:");
								}else {
									p.sendMessage("§aУспешно добавленный предмет:");
								}
								CustomItem i = new CustomItem(p.getInventory().getItemInMainHand(), price);
								plugin.addCustomItem(i);
								plugin.saveMainConfig();
								
								p.sendMessage(plugin.getCustomItemDescription(i, 1).stream().toArray(String[]::new));
								return true;
							}
						}
					}
					cs.sendMessage("§cВам нужно держать предмет в руке!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("open")) {
				if (args.length > 1) {
					String playername = args[1];
					Player p = Bukkit.getPlayer(playername);
					if(p != null) {
						plugin.openShop(p);
						cs.sendMessage("§cМагазин открыт.");
						return true;
					}else {
						cs.sendMessage("§cИгрок не в сети.");
						return true;
					}
				}else {
					cs.sendMessage("§cВам необходимо указать имя игрока в команде!");
					return true;
				}
			}
		}

		showHelp(cs);

		return true;
	}

	private void showHelp(CommandSender cs) {
		cs.sendMessage("§c -- rpgsell v" + plugin.getDescription().getVersion() + " помощь: --");
		cs.sendMessage("§e/rpgsell §creload §r- перезагружает конфиг");
		cs.sendMessage("§e/rpgsell §csetprice <price> §r- ставит цену на уникальное имя, лор, прочность и зачарование");
		cs.sendMessage("§e/rpgsell §copen <player> §r- открыть игроку магазин");
		cs.sendMessage("§cНастраиваемые в данный момент пользовательские элементы (с данными NBT): §a" + plugin.getCustomItemCount());
	}

}
