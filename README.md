# IndustrialStacking

## Description
IndustrialStacking is a plugin, which allows certain machines to be "stacked".  
The machines are specified in the `config.yml` file of the plugin, but currently contain the following mods:
- [Industrial Foregoing](https://www.curseforge.com/minecraft/mc-mods/industrial-foregoing)
- [Compact Void Miners](https://www.curseforge.com/minecraft/mc-mods/compact-void-miners)

The `config.yml` contains a short explenation of each stacked machines influence, mostly just increasing the base machines speed.

## Setup
IndustrialStacking is mostly a "plug and play" kind of plugin, no major setup required; Although it requires following plugin to work:
- [NBT-API](https://www.spigotmc.org/resources/nbt-api.7939/)

And furthermore, an `id_Offset` value has to be specified in the `config.yml` file for each mod. This is due to the id of blocks being
dependent on the amount of mods present on any given server.  
To determine the `id_Offset` value, take any stackable machine and break it. Take the difference between the received item and the expected id
of the broken block.

## Features
IndustrialStacking provides many *quality of life* featues and configurations, such as:
- **Max stack sizes**: Each machine has a `maxStackSize` value attributed in the `config.yml`.
It determines the highest amount a machine can be stacked to. Set to 0 to disable stacking for this machine type.
- **Info**: The `reload` commands provide a great amount of information about the inner workings to the plugin.
- **Highlighting**: The `view` command provides highlighting of stacked machines in crowded areas.
- **Listing**: The `list` commands provide many useful options to query for stacked machines.
- **Removal**: Removal of a stacked machine can either be achieved by breaking the machine block *or* by shift left-clicking the block with an empty hand.
