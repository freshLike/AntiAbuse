# AntiSavedHotbarAbuse 🛡️

[![Version](https://img.shields.io/badge/version-1.0.9-blue.svg)](https://github.com/freshLike/AntiAbuse/releases)
[![Paper](https://img.shields.io/badge/Paper-1.21+-green.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)](LICENSE)

**AntiSavedHotbarAbuse** — мощный и легковесный плагин для Minecraft-серверов (Paper / Purpur / Spigot), предназначенный для обнаружения и нейтрализации нелегальных («читерских», «32k», «краш») предметов, которые игроки могут приносить из сохраненных хотбаров Креатива (**Saved Hotbars**) или сохранять в инвентарях и сундуках.

---

## 🌟 Основные возможности

- ⚔️ **Контроль уровней зачарований:**
  - Автоматическое изъятие предметов с завышенным уровнем зачарований (по умолчанию чары выше 50 уровня считаются нелегальными).
  - Поддержка проверки как предметов, так и зачарованных книг в инвентаре.
  - Опциональная блокировка любых чар выше ванильного максимума (`block-above-vanilla-max`).

- 🧪 **Защита от читерских зелий:**
  - Блокировка зелий с завышенным уровнем эффекта (например, Сила 255).
  - Блокировка зелий с бесконечной длительностью эффектов (`infinite`).
  - Ограничение максимальной длительности эффектов зелий.

- ⚡ **Контроль модификаторов атрибутов:**
  - Предотвращение использования предметов с гигантскими модификаторами (+9999 урона, скорости, максимального HP и т.д.).
  - Раздельная проверка плоских числовых модификаторов (`ADD_NUMBER`) и процентных бонусов (`ADD_SCALAR` / `MULTIPLY_SCALAR_1`).

- 📦 **Рекурсивная проверка шалкеров:**
  - Глубокое сканирование содержимого шалкеровых ящиков в инвентаре игрока. При обнаружении хотя бы одного нелегального предмета внутри шалкера ящик изымается.

- 🗿 **Защита от стакания тотемов:**
  - Ограничение стаков тотемов бессмертия до 1 штуки в слоте. Лишние тотемы из стака безопасно удаляются, предотвращая бессмертие в PvP.

- 🛑 **Блокировка запрещенных материалов и командных блоков:**
  - Полное изъятие запрещенных блоков (по умолчанию всех видов `COMMAND_BLOCK`).
  - Блокировка установки и открытия интерфейса командных блоков даже до момента сканирования инвентаря.

- 🔄 **Многоуровневая система сканирования:**
  - Проверка при входе игрока на сервер (`scan-on-join`).
  - Проверка при смене игрового режима (`scan-on-gamemode-change`).
  - Проверка личного сундука Края (`scan-ender-chest`).
  - Проверка перемещения, выбрасывания и подбора предметов.
  - Периодическое фоновое сканирование всех онлайн-игроков.

---

## 📋 Команды и права

### Команды:
| Команда | Алиас | Описание | Право |
|---|---|---|---|
| `/antisavedhotbar reload` | `/ash reload` | Перезагрузить конфигурацию плагина | `antisavedhotbar.reload` |
| `/antisavedhotbar scan` | `/ash scan` | Принудительно просканировать свой инвентарь | `antisavedhotbar.reload` |

### Права доступа (Permissions):
| Право | По умолчанию | Описание |
|---|---|---|
| `antisavedhotbar.bypass` | `op` | Полный обход всех проверок плагина на нелегальные предметы |
| `antisavedhotbar.reload` | `op` | Право на перезагрузку конфига и вызов команды `/ash scan` |

---

## ⚙️ Конфигурация (`config.yml`)

Плагин полностью настраивается через файл `config.yml`. Все параметры подробно прокомментированы:

```yaml
# Право для обхода всех проверок плагина
bypass-permission: "antisavedhotbar.bypass"

# Сканирование
scan-on-join: true
scan-on-gamemode-change: true
scan-ender-chest: true
only-scan-creative: false
block-creative-illegal-items: true
block-inventory-moves: true
block-drops-and-pickups: true
periodic-scan-interval-ticks: 100 # 5 секунд

# Зачарования
max-enchant-level: 50
block-above-vanilla-max: false

# Зелья
check-potions: true
max-potion-amplifier: 50
max-potion-duration-seconds: 9600
block-infinite-potion-effects: true

# Атрибуты
check-attributes: true
max-attribute-amount: 50.0
max-attribute-percent: 100.0

# Шалкеры и блоки
check-shulker-contents: true
blocked-materials:
  - COMMAND_BLOCK
  - CHAIN_COMMAND_BLOCK
  - REPEATING_COMMAND_BLOCK
block-command-block-place: true
block-command-block-interact: true
command-block-message: "&cКомандные блоки запрещено использовать!"

# Тотемы
block-totem-stacking: true
totem-message: "&cТотемы нельзя стакать! В слоте оставлен только 1 тотем."

# Оповещения
log-removals: true
send-message: true
message: "&cЭтот предмет заблокирован: нелегальные чары, эффекты зелий или атрибуты!"
```

---

## 🚀 Установка

1. Скачайте последний `.jar` файл плагина из вкладки [Releases](https://github.com/freshLike/AntiAbuse/releases).
2. Поместите файл в папку `plugins/` вашего сервера.
3. Перезагрузите сервер или используйте менеджер плагинов.
4. Настройте `plugins/AntiSavedHotbarAbuse/config.yml` под потребности вашего проекта.

---

## 🛠️ Сборка из исходного кода

Для сборки проекта требуется **JDK 21+** и **Maven**:

```bash
git clone https://github.com/freshLike/AntiAbuse.git
cd AntiAbuse
mvn clean package
```

Собранный файл будет находиться в директории `target/AntiSavedHotbarAbuse-1.0.9.jar`.
