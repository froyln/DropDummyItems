# AGENTS.md

## Project

**DropShitItems** (`dropitems`) — a Minecraft **1.21 client-side Fabric mod** that tracks a
configurable list of "junk/dummy" items and drops them all at once, or automatically whenever
the inventory fills up. Built on the **malilib** library (masa) for config, hotkeys, and the GUI.

- Language: Java 21, Yarn mappings (`1.21+build.1`)
- Build: Gradle 8.10.2 + fabric-loom 1.7-SNAPSHOT, fabric-loader 0.19.3
- Depends on: `malilib >= 0.20.2` (from `https://masa.dy.fi/maven`)
- Entrypoint: `dev.froyln.dropitems.DropItems` (client only, see `fabric.mod.json`)

## Build & run

```bash
./gradlew build        # outputs build/libs/dropitems-<ver>.jar (remapped)
./gradlew runClient    # launches the mod client
```

- The jar you ship is `build/libs/dropitems-*.jar`; `build/devlibs/` holds the dev/sources jars.
- `processResources` expands `${version}` in `fabric.mod.json` from `gradle.properties`.
- No tests or linters are configured; there is no lint/typecheck step.

## Source layout

All code lives under `src/main/java/dev/froyln/dropitems/`:

- `DropItems.java` — `ClientModInitializer`. Registers the config handler, keybind provider,
  and the client tick handler. Only wiring, no logic.
- `Reference.java` — constants: `MOD_ID`, `MOD_NAME`, `MOD_VERSION`.
- `config/ConfigData.java` — malilib `IConfigHandler`: loads/saves `config/dropitems.json`
  via Gson, iterating `Configs.getAllConfigs()`.
- `config/Configs.java` — all config/hotkey declarations (`DUMMY_ITEMS`, `OPEN_CONFIGS`,
  `DROP_ALL`, `ADD_HELD`, `REMOVE_HELD`, `TOGGLE_AUTO_DROP`) and the `getAllConfigs()` /
  `getAllHotkeys()` lists used by load/save and the GUI.
- `config/FeatureToggle.java` — enum of boolean toggles (one currently: auto-drop on full
  inventory). Add new toggles as enum constants; they surface automatically in
  `getAllConfigs()` and the GUI tweaks tab.
- `config/GuiConfigs.java` — malilib `GuiConfigsBase` with three tabs: Tweaks / Lists / Hotkeys.
- `event/InputHandler.java` — malilib `IKeybindProvider`. Wires each hotkey's callback to
  `DropHandler`; do NOT register a hotkey here without adding it to `Configs.getAllHotkeys()`.
- `tweaks/DropHandler.java` — `IClientTickHandler` and the core logic (see below).

## How it works (DropHandler)

- `onClientTick` runs every client tick: fires a pending manual drop, and — when the
  `TWEAK_AUTO_DROP_DUMMY_ON_FULL` toggle is on and `isInventoryFull()` — auto-drops.
- Drops are performed by `clickSlot(0, slot.id, 1, SlotActionType.THROW, ...)` on the
  **player's own inventory screen handler (`syncId == 0`)** — the code guards on this, and
  `dropAllDummyItems()` opens a fresh `InventoryScreen` and defers the drop to the next tick
  (`manualDropPending`) so the syncId is valid.
- Only main + hotbar slots are considered (`slot.inventory == playerInventory && index < 36`);
  armor/offhand are never touched.
- `DUMMY_ITEMS` entries are `Identifier` strings (`minecraft:dirt`); parsed with
  `Identifier.tryParse`, matched against `Registries.ITEM.getId(stack.getItem())`.
- `addHeldItem()` / `removeHeldItem()` mutate the dummy list from the held stack and call
  `ConfigManager.getInstance().onConfigsChanged(...)` to persist.

## Conventions & gotchas

- Follow the malilib idioms used here: config classes as static fields, GUI built via
  `GuiConfigsBase` + `ButtonListener`, hotkey callbacks returning `true`.
- Style: braces on their own line (the codebase deliberately avoids `net.fabricmc.fabric-api`;
  only fabric-loader + malilib are used).
- Translations live in `src/main/resources/assets/dropitems/lang/en_us.json`; every new GUI
  string or hotkey category key needs an entry there.
- Any config added to `Configs` must be added to `getAllConfigs()` (or `getAllHotkeys()` for
  hotkeys) or it won't be saved/loaded or shown in the GUI.
- Version the mod in `gradle.properties` (`mod_version`) and `Reference.MOD_VERSION` — they are
  not kept in sync automatically.
