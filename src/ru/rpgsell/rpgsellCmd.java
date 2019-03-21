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
			cs.sendMessage("�c� ��� ��� ���� �� ������������� ���� �������!");
			return true;
		}
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				cs.sendMessage("�aConfig.yml ������������!");
				plugin.reloadItems();
				return true;
			} else if (args[0].equalsIgnoreCase("setprice")) {
				if (args.length > 1) {
					if (!(cs instanceof Player)) {
						cs.sendMessage("�� ������ ���� �������!");
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
									p.sendMessage("�a������� ����������� �������:");
								}else {
									p.sendMessage("�a������� ����������� �������:");
								}
								CustomItem i = new CustomItem(p.getInventory().getItemInMainHand(), price);
								plugin.addCustomItem(i);
								plugin.saveMainConfig();
								
								p.sendMessage(plugin.getCustomItemDescription(i, 1).stream().toArray(String[]::new));
								return true;
							}
						}
					}
					cs.sendMessage("�c��� ����� ������� ������� � ����!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("open")) {
				if (args.length > 1) {
					String playername = args[1];
					Player p = Bukkit.getPlayer(playername);
					if(p != null) {
						plugin.openShop(p);
						cs.sendMessage("�c������� ������.");
						return true;
					}else {
						cs.sendMessage("�c����� �� � ����.");
						return true;
					}
				}else {
					cs.sendMessage("�c��� ���������� ������� ��� ������ � �������!");
					return true;
				}
			}
		}

		showHelp(cs);

		return true;
	}

	private void showHelp(CommandSender cs) {
		cs.sendMessage("�c -- rpgsell v" + plugin.getDescription().getVersion() + " ������: --");
		cs.sendMessage("�e/rpgsell �creload �r- ������������� ������");
		cs.sendMessage("�e/rpgsell �csetprice <price> �r- ������ ���� �� ���������� ���, ���, ��������� � �����������");
		cs.sendMessage("�e/rpgsell �copen <player> �r- ������� ������ �������");
		cs.sendMessage("�c������������� � ������ ������ ���������������� �������� (� ������� NBT): �a" + plugin.getCustomItemCount());
	}

}
