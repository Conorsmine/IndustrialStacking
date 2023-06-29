# IndustrialStacking

## Description
IndustrialStacking is a plugin, which allows certain machines to be "stacked".  
The machines are specified in the `config.yml` file of the plugin, but currently contain the following mods:
- [Industrial Foregoing](https://www.curseforge.com/minecraft/mc-mods/industrial-foregoing)
- [Compact Void Miners](https://www.curseforge.com/minecraft/mc-mods/compact-void-miners)

The `config.yml` contains a short explanation of each stacked machine's influence, mostly just increasing the base machine's speed.

## Setup
IndustrialStacking is mostly a "plug and play" kind of plugin, with no major setup required; Although it requires the following plugin to work:
- [NBT-API](https://www.spigotmc.org/resources/nbt-api.7939/)

Furthermore, an `id_Offset` value has to be specified in the `config.yml` file for each mod. This is due to the id of blocks being
dependent on the number of mods present on any given server.  
To determine the `id_Offset` value, take any stackable machine and break it. Take the difference between the received item and the expected id
of the broken block.

## Features
IndustrialStacking provides many *quality of life* features and configurations, such as:
- **Max stack sizes**: Each machine has a `maxStackSize` value attributed in the `config.yml`.
It determines the highest amount a machine can be stacked too. Set to 0 to disable stacking for this machine type.
- **Info**: The `info` commands provides a great amount of information about the inner workings of the plugin.
- **Reload**: Use the `reload` commands to apply any changes on the fly.
- **Highlighting**: The `view` command provides highlighting of stacked machines in crowded areas.
- **Listing**: The `list` commands provide many useful options to query for stacked machines.
- **Removal**: Removal of a stacked machine can either be achieved by breaking the machine block *or* by shifting left-clicking the block with an empty hand.
