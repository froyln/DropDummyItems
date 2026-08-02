<div align="center">

# DropDummyItems

**Fabric mod that keeps a list of dummy items and throws them all away with a single keybind — or automatically whenever your inventory is full.**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47D?logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric%20Loader-0.19.3%2B-87CEEB?logo=fabric&logoColor=white)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=java&logoColor=white)](https://www.java.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## What is it?

When you dig a perimeter, most of what comes out is stone you don't want. You pick the items you don't care about, add them to a list, and a single keybind drops every one of them from your inventory at once — no opening your inventory, no dragging stacks out by hand. There's also a toggle that does it automatically the moment your inventory fills up, so you can keep digging without stopping.

## Features

- Drop every dummy item from your inventory and hotbar with one keybind.
- Auto-drop toggle: when your inventory is full, the dummy items throw themselves out.
- Add or remove the item in your hand from the list with a keybind, no config editing.
- Optional action bar message the moment your inventory becomes full.
- Only touches your own inventory — armor and offhand are never dropped, and nothing is ever thrown out of a chest.

## Requirements

- [Java](https://www.java.com/) 17 or higher
- [Minecraft](https://www.minecraft.net/) 1.20.1
- [Fabric Loader](https://fabricmc.net/) 0.15.11 or higher
- [malilib](https://modrinth.com/mod/malilib) 0.16.2 or higher

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/froyln/DropItems.git
   cd DropItems
   ```
2. Build the mod:
   ```bash
   ./gradlew build
   ```
3. Copy the jar to your mods folder:
   ```bash
   cp build/libs/dropdummyitems-*.jar ~/.minecraft/mods/
   ```

## Usage

Press `X + V` (default) to open the settings screen. From there you can edit the item list, rebind the hotkeys, and toggle the auto-drop and alert options. Items in the list accept bare ids, so `diamond` and `minecraft:diamond` are both fine.

| Hotkey | Default | Action |
| --- | --- | --- |
| Open Configs | `X + V` | Opens the settings screen |
| Drop All Dummy Items | unbound | Drops every dummy item from your inventory |
| Add Held Item | unbound | Adds the item in your main hand to the list |
| Remove Held Item | unbound | Removes the item in your main hand from the list |
| Toggle Auto-Drop | unbound | Turns auto-drop on full on or off |

## Dependencies

- [malilib](https://masa.dy.fi) 0.16.2 or higher
- Fabric Loader 0.15.11 or higher

The mod does not require Fabric API.

## Building from Source

Requires Java 17 or higher (Gradle is downloaded automatically by `gradlew`).

```bash
./gradlew clean build
```

The jar lands in `build/libs/dropdummyitems-*.jar`.

## License

[MIT](LICENSE) © froyln
