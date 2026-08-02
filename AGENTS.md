# AGENTS.md

## Project

**DropDummyItems** (`dropdummyitems`) — a Minecraft **1.21 client-side Fabric mod** that tracks a
configurable list of "junk/dummy" items and drops them all at once, or automatically whenever
the inventory fills up. Built on the **malilib** library (masa) for config, hotkeys, and the GUI.

- Language: Java 21, Yarn mappings (`1.21+build.1`)
- Build: Gradle 8.10.2 + fabric-loom 1.7-SNAPSHOT, fabric-loader 0.19.3
- Depends on: `malilib >= 0.20.2` (from `https://masa.dy.fi/maven`)
- Entrypoint: `dev.froyln.dropitems.DropItems` (client only, see `fabric.mod.json`)

## Build & run

```bash
./gradlew build        # outputs build/libs/dropdummyitems-<ver>.jar (remapped)
./gradlew runClient    # launches the mod client
```

- **Testing:** run `./gradlew build` after every change — this is the mod's test command
  (there is no test suite; a green build is the check).
- The jar you ship is `build/libs/dropdummyitems-*.jar`; `build/devlibs/` holds the dev/sources jars.
- `processResources` expands `${version}` in `fabric.mod.json` from `gradle.properties`.
- No tests or linters are configured; there is no lint/typecheck step.

## Source layout

All code lives under `src/main/java/dev/froyln/dropitems/`:

- `DropItems.java` — `ClientModInitializer`. Registers the config handler, keybind provider,
  and the client tick handler. Only wiring, no logic.
- `Reference.java` — constants: `MOD_ID`, `MOD_NAME`, `MOD_VERSION`.
- `config/ConfigData.java` — malilib `IConfigHandler`: loads/saves `config/dropdummyitems.json`
  via Gson, iterating `Configs.getAllConfigs()`.
- `config/Configs.java` — all config/hotkey declarations (`DUMMY_ITEMS`, `OPEN_CONFIGS`,
  `DROP_ALL`, `ADD_HELD`, `REMOVE_HELD`, `TOGGLE_AUTO_DROP`, `ALERT_INVENTORY_FULL`) and the
  `getAllConfigs()` / `getAllHotkeys()` lists used by load/save and the GUI.
- `config/FeatureToggle.java` — enum of boolean toggles (one currently: auto-drop on full
  inventory). Add new toggles as enum constants; they surface automatically in
  `getAllConfigs()` and the GUI tweaks tab.
- `config/GuiConfigs.java` — malilib `GuiConfigsBase` with four tabs:
  Tweaks / Lists / Hotkeys / Alerts.
- `event/InputHandler.java` — malilib `IKeybindProvider`. Wires each hotkey's callback to
  `DropHandler`; do NOT register a hotkey here without adding it to `Configs.getAllHotkeys()`.
- `tweaks/DropHandler.java` — `IClientTickHandler` and the core logic (see below).

## How it works (DropHandler)

- `onClientTick` runs every client tick: when the `TWEAK_AUTO_DROP_DUMMY_ON_FULL` toggle is on
  and `isInventoryFull()`, it auto-drops. When inventory flips to full and the
  `ALERT_INVENTORY_FULL` toggle is on, it shows an action bar message (once per fill, tracked
  by `wasInventoryFull`).
- Drops are performed by `clickSlot(0, slot.id, 1, SlotActionType.THROW, ...)` on the
  **player's own inventory screen handler**. There is no `syncId == 0` guard: the
  `PlayerScreenHandler` keeps syncId 0 both open and closed, so `dropAllDummyItems()` drops
  immediately on the hotkey press without opening any screen. If another container (e.g. a
  chest) is open, the click is silently ignored by the client's mismatched-container check, so
  items are never thrown from a chest.
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
- Translations live in `src/main/resources/assets/dropdummyitems/lang/en_us.json`; every new GUI
  string or hotkey category key needs an entry there.
- Any config added to `Configs` must be added to `getAllConfigs()` (or `getAllHotkeys()` for
  hotkeys) or it won't be saved/loaded or shown in the GUI.
- Version the mod in `gradle.properties` (`mod_version`) and `Reference.MOD_VERSION` — they are
  not kept in sync automatically.
