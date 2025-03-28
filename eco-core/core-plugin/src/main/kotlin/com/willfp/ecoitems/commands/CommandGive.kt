package com.willfp.ecoitems.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.drops.DropQueue
import com.willfp.ecoitems.items.EcoItems
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil

class CommandGive(plugin: EcoPlugin) : Subcommand(plugin, "give", "ecoitems.command.give", false) {
    private val numbers = listOf("1", "2", "3", "4", "5", "10", "32", "64")

    override fun onExecute(sender: CommandSender, args: List<String>) {
        val player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
        val ecoItem = notifyNull(EcoItems.getByID(args.getOrNull(1)), "invalid-item")
        val amount = args.getOrNull(2)?.toIntOrNull() ?: 1

        val message = plugin.langYml.getMessage("give-success")
            .replace("%item%", ecoItem.id.key)
            .replace("%recipient%", player.name)
        sender.sendMessage(message)

        val item = ecoItem.itemStack.clone() // Clone to prevent modifying the template
        item.amount = amount

        addItemToInventory(player, item)
    }

    private fun addItemToInventory(player: Player, item: ItemStack) {
        val inventory = player.inventory

        for (slot in 0 until inventory.size) {
            val currentItem = inventory.getItem(slot)
            if (currentItem != null && currentItem.isSimilar(item)) {
                val newAmount = currentItem.amount + item.amount
                if (newAmount <= currentItem.maxStackSize) {
                    currentItem.amount = newAmount
                    return
                } else {
                    item.amount = newAmount - currentItem.maxStackSize
                    currentItem.amount = currentItem.maxStackSize
                }
            }
        }

        // Try adding the remaining items normally
        val leftover = inventory.addItem(item)
        if (leftover.isNotEmpty()) {
            DropQueue(player)
                .addItem(item)
                .forceTelekinesis()
                .push()
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.isEmpty()) {
            return EcoItems.values().map { it.id.key }
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                completions
            )
            return completions
        }

        if (args.size == 2) {
            val itemNames = EcoItems.values().map { it.id.key }
            StringUtil.copyPartialMatches(args[1], itemNames, completions)
            completions.sort()
            return completions
        }

        if (args.size == 3) {
            StringUtil.copyPartialMatches(args[2], numbers, completions)
            return completions
        }

        return emptyList()
    }
}
