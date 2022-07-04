package net.plazmix.coreconnector.direction.bukkit.listener;

import lombok.NonNull;
import net.plazmix.coreconnector.direction.bukkit.BukkitConnectorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

public final class TabCompleteListener implements Listener {

  // @EventHandler
  // public void onTabComplete(TabCompleteEvent event) {
  //     String buffer = event.getBuffer();

  //     if (buffer.startsWith("/") && !buffer.contains(" ")) {
  //         List<String> commands = BukkitConnectorPlugin.getCommandList()
  //                 .stream()
  //                 .filter(s -> s.startsWith(buffer.replaceFirst("/", "")))
  //                 .map(s -> "/" + s)
  //                 .collect(Collectors.toList());

  //         event.setCompletions(commands);

  //     } else {

  //         // TODO: Сделать сюда всех игроков из кора ок??
  //         List<String> players = Bukkit.getOnlinePlayers()
  //                 .stream()
  //                 .map(Player::getName)
  //                 .filter(s -> s.startsWith(substring(event.getBuffer())))
  //                 .collect(Collectors.toList());

  //         event.setCompletions(players);
  //     }
  // }

  // private String substring(@NonNull String buffer) {
  //     return buffer.substring(Math.min(buffer.lastIndexOf(" ") + 1, buffer.length()));
  // }
}
