@file:Suppress("unused", "DEPRECATION", "MemberVisibilityCanBePrivate")

package top.iseason.bukkittemplate.utils.bukkit


import com.google.common.base.Enums
import com.google.gson.Gson
import io.github.bananapuncher714.nbteditor.NBTEditor
import io.github.bananapuncher714.nbteditor.NBTEditor.NBTCompound
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.CreatureSpawner
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Axolotl
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TropicalFish
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.*
import org.bukkit.map.MapView
import org.bukkit.material.MaterialData
import org.bukkit.material.SpawnEgg
import org.bukkit.potion.*
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.math.abs


/**
 * bukkit的物品相关工具
 */
object ItemUtils {

    /**
     * 修改ItemMeta
     */
    inline fun <T : ItemStack> T.applyMeta(block: ItemMeta.() -> Unit): T {
        val itemMeta = itemMeta ?: return this
        block(itemMeta)
        this.itemMeta = itemMeta
        return this
    }

    /**
     * 减少物品数量，如果小于0则物品变为空气
     */
    fun ItemStack.subtract(count: Int) {
        val i = amount - count
        if (i <= 0) type = Material.AIR
        else amount = i
    }

    /**
     * 增加物品数量，返回溢出的数量
     */
    fun ItemStack.add(count: Int): Int {
        val i = amount + count
        return if (i >= maxStackSize) {
            amount = maxStackSize
            i - maxStackSize
        } else {
            amount = i
            0
        }
    }

    /**
     * 检查材质是否是空气
     */
    fun Material.checkAir(): Boolean = when (this.name) {
        "AIR",
        "VOID_AIR",
        "CAVE_AIR",
        "LEGACY_AIR" -> true

        else -> false
    }

    /**
     * 检查物品是否是空气.null也为空气
     */
    fun ItemStack?.checkAir(): Boolean = if (this == null) true else type.checkAir()

    /**
     * 物品转化为字节
     */
    fun ItemStack.toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use {
            it.writeObject(this)
        }
        val gzipStream = ByteArrayOutputStream()
        GZIPOutputStream(gzipStream).use { it.write(outputStream.toByteArray()) }
        return gzipStream.toByteArray()
    }

    /**
     * 物品转为BASE64字符串
     */
    fun ItemStack.toBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())

    /**
     * 转为Base64字符串
     */
    fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)

    /**
     * Base64字符串 转为 ByteArray
     */
    fun String.base64ToByteArray(): ByteArray = Base64.getDecoder().decode(this)

    /**
     * 一组物品转化为字节
     */
    fun Collection<ItemStack>.toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use {
            it.writeInt(size)
            for (item in this) {
                it.writeObject(item)
            }
        }
        val gzipStream = ByteArrayOutputStream()
        GZIPOutputStream(gzipStream).use { it.write(outputStream.toByteArray()) }
        return gzipStream.toByteArray()
    }

    /**
     * 背包物品转化为字节，保存位置
     */
    fun Map<Int, ItemStack>.toByteArrays(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use {
            forEach { (index, itemStack) ->
                it.writeInt(index)
                it.writeObject(itemStack)
            }
        }
        val gzipStream = ByteArrayOutputStream()
        GZIPOutputStream(gzipStream).use { it.write(outputStream.toByteArray()) }
        return gzipStream.toByteArray()
    }

    /**
     * 背包物品转化为Base64字符串，保存位置
     */
    fun Map<Int, ItemStack>.toBase64() = toByteArrays().toBase64()

    /**
     * 一组物品转化为BASE64字符串
     */
    fun Collection<ItemStack>.toBase64(): String = this.toByteArray().toBase64()

    /**
     * 物品转为Json文本
     */
    fun ItemStack.toJson(): String = NBTEditor.getNBTCompound(this).toJson()

    /**
     * 序列化为bukkit支持的配置
     * @param allowNested 是否允许嵌套解析(比如潜影盒)，只对容器有效，为false时将容器内容转为base64储存
     */
    fun ItemStack.toSection(allowNested: Boolean = true): YamlConfiguration {
        val yaml = YamlConfiguration()
        yaml["material"] = type.toString()
        if (amount != 1) yaml["amount"] = amount
        if (!hasItemMeta()) return yaml
        // 额外的NBt
        val toJson = NBTEditor.getNBTCompound(this, "tag").toJson()
//        println(toJson)
        val json = Gson().fromJson(toJson, Map::class.java).toMutableMap()
        this.durability
        with(itemMeta!!) {
            // 名字
            if (hasDisplayName()) yaml["name"] = displayName
            // lore
            if (hasLore()) yaml["lore"] = lore
            // 耐久
            val durability = this@toSection.durability
            if (durability != 0.toShort()) {
                yaml["damage"] = durability
                json.remove("Damage")
            }
            // 附魔
            if (hasEnchants()) {
                yaml.createSection("enchants", enchants.mapKeys {
                    val namespacedKey = it.key.key
                    if (namespacedKey.namespace == NamespacedKey.MINECRAFT) namespacedKey.key else namespacedKey.toString()
                })
                json.remove("Enchantments")
            }
            // flags
            val itemFlags = itemFlags
            if (itemFlags.isNotEmpty()) {
                yaml["flags"] = itemFlags.map { it.name }
                json.remove("HideFlags")
            }
            when (this) {
                // 附魔书附魔
                is EnchantmentStorageMeta ->
                    if (hasStoredEnchants()) {
                        yaml.createSection("stored-enchants",
                            storedEnchants.mapKeys { it.key.key })
                        json.remove("StoredEnchantments")
                    }


                // 头颅
                is SkullMeta -> {
                    val texture = NBTEditor.getTexture(this@toSection)
                    if (texture != null) yaml["skull"] = texture
                    else if (hasOwner()) yaml["skull-owner"] = owner
                    json.remove("SkullOwner")
                }
                // 皮革
                is LeatherArmorMeta -> yaml["color"] = color.toRGBString()
                // 药水
                is PotionMeta -> {
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_9)) {
                        yaml["base-effect"] =
                            "${basePotionData.type.name},${basePotionData.isExtended},${basePotionData.isUpgraded}"
                        if (customEffects.isNotEmpty())
                            yaml["effects"] = customEffects.map { it.toEffectString() }
                        if (hasColor()) yaml["color"] = color!!.toRGBString()
                    } else if (durability != 0.toShort()) {
                        val potion = Potion.fromItemStack(this@toSection)
                        yaml["base-effect"] = "${potion.type.name},${potion.hasExtendedDuration()},${potion.isSplash}"
                    }
                    json.remove("Potion")
                    json.remove("CustomPotionEffects")
                    json.remove("CustomPotionColor")
                }

                is BlockStateMeta -> {
                    val blockState = blockState
                    if (blockState is CreatureSpawner) {
                        yaml["spawner"] = blockState.spawnedType.name
                    } else if (blockState is InventoryHolder) {
                        if (allowNested) {
                            val createSection = yaml.createSection("inventory")
                            blockState.inventory.forEachIndexed { index, item ->
                                if (item == null) return@forEachIndexed
                                createSection[index.toString()] = item.toSection()
                            }
                        } else {
                            yaml["inventory"] = buildMap {
                                blockState.inventory.forEachIndexed { index, item ->
                                    if (item == null) return@forEachIndexed
                                    this[index] = item
                                }
                            }.toBase64()
                        }
                    }
                    json.remove("BlockEntityTag")
                }

                is FireworkMeta -> {
                    yaml["power"] = power
                    for ((index, effect) in effects.withIndex()) {
                        val fwc: ConfigurationSection = yaml.getConfigurationSection("effects.$index")!!
                        fwc["type"] = effect.type.name
                        fwc["flicker"] = effect.hasFlicker()
                        fwc["trail"] = effect.hasTrail()
                        val colors = fwc.createSection("colors")
                        colors["base"] = effect.colors.map { it.toRGBString() }
                        colors["fade"] = effect.fadeColors.map { it.toRGBString() }
                    }
                    json.remove("Fireworks")
                }

                is BookMeta -> {
                    val bookInfo = yaml.createSection("book")
                    bookInfo["title"] = title
                    bookInfo["author"] = author
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_9)) {
                        bookInfo["generation"] = generation?.name
                    }
                    bookInfo["pages"] = pages
                    json.remove("generation")
                    json.remove("author")
                    json.remove("title")
                    json.remove("pages")
                }

                is MapMeta -> {
                    val mapSection = yaml.createSection("map")
                    mapSection["scaling"] = isScaling
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)) {
                        if (hasLocationName()) mapSection["location"] = locationName
                        if (hasColor()) {
                            mapSection["color"] = color?.toRGBString()
                        }
                    }
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_14)) {
                        if (hasMapView()) {
                            val mapView: MapView = mapView!!
                            val view = mapSection.createSection("view")
                            view["scale"] = mapView.scale.toString()
                            view["world"] = mapView.world?.name
                            view["center"] = "${mapView.centerX},${mapView.centerZ}"
                            view["locked"] = mapView.isLocked
                            view["tracking-position"] = mapView.isTrackingPosition
                            view["unlimited-tracking"] = mapView.isUnlimitedTracking
                        }
                    }
                    json.remove("map")
                    json.remove("Decorations")
                }

            }
            //老版本刷怪蛋 1.13 以下
            if (NBTEditor.getMinecraftVersion().lessThanOrEqualTo(NBTEditor.MinecraftVersion.v1_13)) {
                if (NBTEditor.getMinecraftVersion().lessThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)) {
                    if (data is SpawnEgg) yaml["creature"] = (data as SpawnEgg).spawnedType.getName()
                } else if (this is SpawnEggMeta) {
                    yaml["creature"] = spawnedType.getName()
                }
                json.remove("EntityTag")
            }

            // 1.11以上
            if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)) {
                // 无法破坏
                if (isUnbreakable) {
                    yaml["unbreakable"] = true
                    json.remove("Unbreakable")
                }
                // 旗帜
                if (this is BannerMeta) {
                    yaml.createSection(
                        "banner",
                        patterns.associate { it.pattern.name to it.color.name })
                    json.remove("Patterns")

                }

            }
            // 1.14 以上
            if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_14)) {
                if (hasAttributeModifiers()) {
                    val mutableMapOf = mutableMapOf<String, Any>()
                    attributeModifiers!!.forEach { t, u ->
                        val serialize = mutableMapOf<String, String>()
                        serialize["attribute"] = t.name
                        serialize["uuid"] = u.uniqueId.toString()
                        serialize["operation"] = u.operation.name
                        serialize["amount"] = u.amount.toString()
                        if (u.slot != null) serialize["slot"] = u.slot!!.name
                        mutableMapOf[u.name] = serialize
                    }
                    yaml.createSection("attributes", mutableMapOf)
                    json.remove("AttributeModifiers")
                }
                // 模型
                if (hasCustomModelData()) {
                    yaml["custom-model-data"] = customModelData
                    json.remove("custom_model_data")
                }
                when (this) {
                    //弩
                    is CrossbowMeta -> {
                        if (chargedProjectiles.isNotEmpty()) {
                            for ((i, projectiles) in chargedProjectiles.withIndex()) {
                                yaml["projectiles.$i"] = projectiles.toSection()
                            }
                            json.remove("ChargedProjectiles")
                            json.remove("Charged")
                        }
                    }
                    // 热带鱼桶
                    is TropicalFishBucketMeta -> {
                        yaml["pattern"] = pattern.name
                        yaml["color"] = bodyColor.name
                        yaml["pattern-color"] = patternColor.name
                        json.remove("BucketVariantTag")
                        json.remove("EntityTag")
                    }
                    // 迷之炖菜
                    is SuspiciousStewMeta -> {
                        yaml["effects"] = customEffects.map { it.toEffectString() }
                        json.remove("Effects")
                    }
                }
            }
        }
        if (NBTEditor.getMinecraftVersion()
                .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_16) && this is CompassMeta
        ) {
            val subSection: ConfigurationSection = yaml.createSection("lodestone")
            subSection["tracked"] = isLodestoneTracked
            json.remove("LodestoneTracked")
            if (hasLodestone()) {
                val location = lodestone
                subSection["location"] = location!!.toLocationString()
            }
            json.remove("LodestoneDimension")
            json.remove("LodestonePos")
        }
        if (NBTEditor.getMinecraftVersion()
                .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_17) && this is AxolotlBucketMeta
        ) {
            if (hasVariant()) yaml["variant"] = variant.toString()
            json.remove("Variant")
        }
        json.remove("display")
        //写入数据
        if (json.isNotEmpty()) {
            fun deepFor(map: Map<*, *>, config: ConfigurationSection) {
                for ((k, v) in map) {
                    if (v is Map<*, *>) {
                        deepFor(v, config.createSection(k.toString()))
                    } else {
                        if ((v is Double) && (abs(v - Math.round(v)) < Double.MIN_VALUE)) {
                            config.set(k.toString(), v.toInt())
                        } else if (v is String && v.endsWith('d')) {
                            val double = runCatching {
                                v.substring(0, v.length - 1).toDouble()
                            }.getOrNull()
                            if (double == null) config.set(k.toString(), v)
                            else
                                config.set(k.toString(), double)
                        } else {
                            config.set(k.toString(), v)
                        }
                    }
                }
            }
            deepFor(json, yaml.createSection("nbt"))
//            yaml["nbt"] = json
        }
        return yaml
    }

    /**
     * 从配置反序列化ItemStack
     * @param allowNested 是否允许嵌套解析(比如潜影盒)，只对容器有效，为false时将容器内容转为base64储存
     */
    fun fromSection(section: ConfigurationSection, allowNested: Boolean = true): ItemStack? {
        val material = Material.getMaterial(section.getString("material")!!.uppercase()) ?: return null
        var item = ItemStack(material)
        //处理头颅
        val url = section.getString("skull")
        if (url != null) item = NBTEditor.getHead(url)
        item.amount = section.getInt("amount", 1)
        item.durability = section.getInt("damage", 0).toShort()
        item.applyMeta {
            section.getString("name")?.also { setDisplayName(it.toColor()) }
            section.getStringList("lore").also { if (it.isNotEmpty()) lore = it.toColor() }
            section.getConfigurationSection("enchants")?.getValues(false)?.forEach { (t, u) ->
                val enchant = matchEnchant(t) ?: return@forEach
                val level = u as? Int ?: return@forEach
                addEnchant(enchant, level, true)
            }
            section.getStringList("flags").forEach {
                addItemFlags(runCatching { ItemFlag.valueOf(it.uppercase()) }.getOrElse { return@forEach })
            }
            // 解析meta
            when (this) {
                // 附魔书附魔
                is EnchantmentStorageMeta -> section.getConfigurationSection("stored-enchants")?.getValues(false)
                    ?.forEach { (t, u) ->
                        val enchant = matchEnchant(t) ?: return@forEach
                        val level = u as? Int ?: return@forEach
                        addStoredEnchant(enchant, level, true)
                    }
                // 皮革
                is LeatherArmorMeta -> section.getString("color")?.also { setColor(fromColorStr(it)) }
                // 药水
                is PotionMeta -> {
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_9)) {
                        section.getString("base-effect")?.also {
                            val split = it.trim().split(',')
                            val type =
                                runCatching { PotionType.valueOf(split[0].uppercase()) }.getOrElse { return@also }
                            basePotionData = PotionData(
                                type,
                                split.getOrNull(1)?.toBoolean() ?: false,
                                split.getOrNull(2)?.toBoolean() ?: false
                            )
                        }
                        section.getStringList("effects").forEach { ef ->
                            val effect = fromEffectString(ef) ?: return@forEach
                            addCustomEffect(effect, true)
                        }
                        section.getString("color")?.also { color = fromColorStr(it) }
                    } else if (item.durability != 0.toShort()) {
                        section.getString("base-effect")?.also {
                            val split = it.split(',')
                            val type = runCatching { PotionType.valueOf(split[0]) }.getOrElse { return@also }
                            val extended = runCatching { split[1].toBoolean() }.getOrElse { return@also }
                            val upgraded = runCatching { split[2].toBoolean() }.getOrElse { return@also }
                            basePotionData = PotionData(type, extended, upgraded)
                        }
                    }
                }

                is BlockStateMeta -> {
                    val blockState = blockState
                    if (blockState is CreatureSpawner) {
                        val type =
                            section.getString("spawner")?.let { runCatching { EntityType.valueOf(it) }.getOrNull() }
                        if (type != null) blockState.spawnedType = type
                    } else if (blockState is InventoryHolder) {
                        if (allowNested) {
                            val configurationSection = section.getConfigurationSection("inventory")
                            configurationSection?.getKeys(false)?.forEach {
                                val itemSection = configurationSection.getConfigurationSection(it) ?: return@forEach
                                val toInt = it.toInt()
                                val fromSection = fromSection(itemSection, true) ?: return@forEach
                                blockState.inventory.setItem(toInt, fromSection)
                            }
                        } else {
                            section.getString("inventory")?.also {
                                for ((index, i) in fromBase64ToMap(it)) {
                                    blockState.inventory.setItem(index, i)
                                }
                            }
                        }
                    }
                    this.blockState = blockState
                }

                is FireworkMeta -> {
                    power = section.getInt("power", 1)
                    val effectSection = section.getConfigurationSection("effects")
                    effectSection?.getKeys(false)?.forEach { key ->
                        val effects = section.getConfigurationSection(key)!!
                        val type =
                            runCatching { FireworkEffect.Type.valueOf(effects.getString("type")!!) }.getOrElse { return@forEach }
                        addEffect(
                            FireworkEffect.builder()
                                .with(type)
                                .flicker(effects.getBoolean("flicker"))
                                .trail(effects.getBoolean("trail"))
                                .withColor(effects.getStringList("colors.base").map { fromColorStr(it) })
                                .withFade(effects.getStringList("colors.fade").map { fromColorStr(it) })
                                .build()
                        )
                    }
                }

                is BookMeta -> {
                    val book = section.getConfigurationSection("book")
                    if (book != null) {
                        book.getString("title")?.also { title = it.toColor() }
                        book.getString("author")?.also { author = it.toColor() }
                        pages = book.getStringList("pages").toColor()
                    }
                    if (book != null && NBTEditor.getMinecraftVersion()
                            .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_9)
                    ) {
                        book.getString("generation")?.also {
                            generation = kotlin.runCatching { BookMeta.Generation.valueOf(it.uppercase()) }.getOrNull()
                        }
                    }
                }

                is MapMeta -> {
                    val mapSection = section.getConfigurationSection("map")
                    isScaling = mapSection?.getBoolean("scaling") ?: false
                    if (mapSection != null && NBTEditor.getMinecraftVersion()
                            .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)
                    ) {
                        mapSection.getString("location")?.also { locationName = it.toColor() }
                        mapSection.getString("color")?.also { color = fromColorStr(it) }
                    }
                    if (mapSection != null && NBTEditor.getMinecraftVersion()
                            .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_14)
                    ) {
                        mapSection.getConfigurationSection("view")?.also {
                            runCatching {
                                val mapView = Bukkit.createMap(Bukkit.getWorld(it.getString("world")!!)!!)
                                mapView.scale = MapView.Scale.valueOf(it.getString("scale")!!)
                                mapView.centerX = it.getString("center")!!.split(',')[0].toInt()
                                mapView.centerZ = it.getString("center")!!.split(',')[1].toInt()
                                mapView.isLocked = it.getBoolean("locked")
                                mapView.isTrackingPosition = it.getBoolean("tracking-position")
                                mapView.isUnlimitedTracking = it.getBoolean("unlimited-tracking")
                                setMapView(mapView)
                            }
                        }
                    }
                }
            }
            if (NBTEditor.getMinecraftVersion().lessThanOrEqualTo(NBTEditor.MinecraftVersion.v1_13)) {
                if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)) {
                    if (this is SpawnEggMeta) {
                        val creatureName = section.getString("creature")
                        if (creatureName != null) {
                            val creature = Enums.getIfPresent(
                                EntityType::class.java, creatureName.uppercase()
                            )
                            if (creature.isPresent) spawnedType = creature.get()
                        }
                    }
                } else {
                    val data: MaterialData? = item.data
                    if (data is SpawnEgg) {
                        val creatureName = section.getString("creature")
                        if (creatureName != null) {
                            val creature = Enums.getIfPresent(
                                EntityType::class.java, creatureName.uppercase()
                            )
                            if (creature.isPresent) data.spawnedType = creature.get()
                            item.data = data
                        }
                    }
                }
            }
            if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_14)
            ) {
                val section2 = section.getConfigurationSection("attributes")
                section2?.getKeys(false)?.forEach { name ->
                    val section3 = section2.getConfigurationSection(name)!!
                    val attribute = runCatching {
                        Attribute.valueOf(
                            section3.getString("attribute")!!.uppercase()
                        )
                    }.getOrElse { return@forEach }
                    val operation = runCatching {
                        AttributeModifier.Operation.valueOf(section3.getString("operation")!!.uppercase())
                    }.getOrElse { return@forEach }
                    val amount = section3.getDouble("amount")
                    val slot =
                        kotlin.runCatching { EquipmentSlot.valueOf(section3.getString("slot")!!.uppercase()) }
                            .getOrNull()
                    val uuid =
                        runCatching { UUID.fromString(section3.getString("uuid")!!) }.getOrElse { UUID.randomUUID() }
                    addAttributeModifier(attribute, AttributeModifier(uuid, name, amount, operation, slot))
                }
            }
            if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_14)) {
                val modelData = section.getInt("custom-model-data")
                if (modelData != 0) setCustomModelData(modelData)
                if (this is CrossbowMeta) {
                    val section1 = section.getConfigurationSection("projectiles")
                    if (section1 != null) {
                        for (projectiles in section1.getKeys(false)) {
                            val projectile = fromSection(section1.getConfigurationSection(projectiles)!!)
                            if (projectile != null) {
                                addChargedProjectile(projectile)
                            }
                        }
                    }
                } else if (this is TropicalFishBucketMeta) {
                    val color =
                        Enums.getIfPresent(DyeColor::class.java, section.getString("color") ?: "").or(DyeColor.WHITE)
                    val patternColor =
                        Enums.getIfPresent(DyeColor::class.java, section.getString("pattern-color") ?: "")
                            .or(DyeColor.WHITE)
                    val pattern =
                        Enums.getIfPresent(TropicalFish.Pattern::class.java, section.getString("pattern") ?: "")
                            .or(TropicalFish.Pattern.BETTY)
                    bodyColor = color
                    setPatternColor(patternColor)
                    setPattern(pattern)
                }
            }
            if (NBTEditor.getMinecraftVersion()
                    .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_15) && this is SuspiciousStewMeta
            ) {
                for (effects in section.getStringList("effects")) {
                    val fromEffectString = fromEffectString(effects)
                    if (fromEffectString != null)
                        addCustomEffect(fromEffectString, true)
                }
            }
            if (NBTEditor.getMinecraftVersion()
                    .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_16) && this is CompassMeta
            ) {

                val lodestoneSection = section.getConfigurationSection("lodestone")
                if (lodestoneSection != null) {
                    isLodestoneTracked = lodestoneSection.getBoolean("tracked")
                    lodestoneSection.getString("lodestone")?.let { lodestone = fromLocationString(it) }
                }

            }
            if (NBTEditor.getMinecraftVersion()
                    .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_17) && this is AxolotlBucketMeta
            ) {
                val variantStr = section.getString("variant")
                if (variantStr != null) {
                    val variantE: Axolotl.Variant =
                        Enums.getIfPresent(Axolotl.Variant::class.java, variantStr.uppercase())
                            .or(Axolotl.Variant.BLUE)
                    variant = variantE
                }

            }
        }
        // 处理nbt
        val nbtSection = section.getConfigurationSection("nbt") ?: return item
        val map = mutableMapOf<String, Any>()
        fun deepFor(config: ConfigurationSection, map: MutableMap<String, Any>) {
            for ((key, value) in config.getValues(false)) {
                if (value is ConfigurationSection) {
                    val newMap = mutableMapOf<String, Any>()
                    map[key] = newMap
                    deepFor(value, newMap)
                    continue
                }
                map[key] = value
            }
        }
        deepFor(nbtSection, map)
        val nbtCompound = NBTEditor.getNBTCompound(item, "tag")
        map.forEach { (k, v) ->
            if (v is Map<*, *>) {
                val toJson = NBTCompound.fromJson(Gson().toJson(v))
                nbtCompound.set(toJson, k)
            } else
                nbtCompound.set(v, k)
        }
        item = NBTEditor.set(item, nbtCompound)
//        println(NBTEditor.getNBTCompound(item, "tag").toJson())
        return item
    }

    /**
     * 物品集合转为配置
     * @param allowNested 是否允许嵌套解析(比如潜影盒)，只对容器有效，为false时将容器内容转为base64储存
     */
    fun Collection<ItemStack>.toSection(allowNested: Boolean = true) = map { it.toSection(allowNested) }

    /**
     * 配置list转为物品集合，配合 ConfigurationSection::getList 方法使用
     * @param allowNested 是否允许嵌套解析(比如潜影盒)，只对容器有效，为false时将容器内容转为base64储存
     */
    fun fromSections(sections: List<*>, allowNested: Boolean = true): List<ItemStack> {
        if (sections.isEmpty()) return emptyList()
        return sections.mapNotNull { if (it is ConfigurationSection) fromSection(it, allowNested) else null }
    }

    /**
     * 带有序号的物品集合转为ConfigurationSection
     * @param allowNested 是否允许嵌套解析(比如潜影盒)，只对容器有效，为false时将容器内容转为base64储存
     */
    fun Map<Int, ItemStack>.toSection(allowNested: Boolean = true): ConfigurationSection {
        val yamlConfiguration = YamlConfiguration()
        forEach { (index, item) ->
            yamlConfiguration[index.toString()] = item.toSection(allowNested)
        }
        return yamlConfiguration
    }

    /**
     * ConfigurationSection转为带有序号的物品集合
     * @param allowNested 是否允许嵌套解析(比如潜影盒)，只对容器有效，为false时将容器内容转为base64储存
     */
    fun fromSectionToMap(section: ConfigurationSection, allowNested: Boolean = true): Map<Int, ItemStack> {
        val mutableMapOf = mutableMapOf<Int, ItemStack>()
        section.getKeys(false).forEach {
            kotlin.runCatching {
                mutableMapOf[it.toInt()] =
                    fromSection(section.getConfigurationSection(it)!!, allowNested) ?: return@forEach
            }
        }
        return mutableMapOf
    }

    /**
     * 字节转换为ItemStack
     */
    fun fromByteArray(bytes: ByteArray): ItemStack {
        GZIPInputStream(ByteArrayInputStream(bytes)).use { it1 ->
            BukkitObjectInputStream(it1).use { return it.readObject() as ItemStack }
        }
    }

    /**
     * BASE64字符串转为物品
     */
    fun fromBase64ToItemStack(base64: String) = fromByteArray(base64.base64ToByteArray())

    /**
     * 字节转换为一组List<ItemStack>
     */
    fun fromByteArrays(bytes: ByteArray): List<ItemStack> {
        GZIPInputStream(ByteArrayInputStream(bytes)).use { it1 ->
            val mutableListOf = mutableListOf<ItemStack>()
            BukkitObjectInputStream(it1).use {
                val size = it.readInt()
                for (i in 0 until size) {
                    mutableListOf.add(it.readObject() as ItemStack)
                }
            }
            return mutableListOf
        }
    }

    /**
     * BASE64字符串转为List<ItemStack>
     */
    fun fromBase64ToItems(base64: String) = fromByteArrays(base64.base64ToByteArray())

    /**
     * ByteArrays转为 Map<Int, ItemStack>
     */
    fun fromByteArraysToMap(bytes: ByteArray): Map<Int, ItemStack> {
        val map = mutableMapOf<Int, ItemStack>()
        GZIPInputStream(ByteArrayInputStream(bytes)).use { it1 ->
            BukkitObjectInputStream(it1).use {
                runCatching {
                    while (true) {
                        map[it.readInt()] = it.readObject() as ItemStack
                    }
                }
            }
        }
        return map
    }

    /**
     * BASE64字符串转为 Map<Int, ItemStack>
     */
    fun fromBase64ToMap(base64: String) = fromByteArraysToMap(base64.base64ToByteArray())

    /**
     * 由json字符串转ItemStack
     */
    fun fromJson(json: String): ItemStack = NBTEditor.getItemFromTag(NBTEditor.getNBTCompound(json))

    private fun Color.toRGBString(): String = "${red},${green},${blue}"
    private fun fromColorStr(str: String): Color {
        val split = str.trim().split(',')
        return Color.fromRGB(
            split.getOrNull(0)?.toInt() ?: 0,
            split.getOrNull(0)?.toInt() ?: 0,
            split.getOrNull(0)?.toInt() ?: 0
        )
    }

    private fun Location.toLocationString() = "${world},$x,$y,$z"
    private fun fromLocationString(str: String): Location {
        val split = str.trim().split(',')
        val world = Bukkit.getWorld(split[0])
        return Location(
            world,
            str.getOrNull(1)?.toDouble() ?: 0.0,
            str.getOrNull(2)?.toDouble() ?: 0.0,
            str.getOrNull(3)?.toDouble() ?: 0.0
        )
    }

    private fun PotionEffect.toEffectString(): String = "${type.name},${duration},${amplifier}"

    private fun fromEffectString(str: String): PotionEffect? {
        val split = str.trim().split(',')
        val type = runCatching { PotionEffectType.getByName(split[0]) }.getOrNull() ?: return null
        val duration = runCatching { split[1].toInt() }.getOrElse { return null }
        val amplifier = runCatching { split[2].toInt() }.getOrElse { return null }
        return PotionEffect(type, duration, amplifier)
    }

    /**
     * 由 NamespaceKey 获取对应的附魔
     */
    private fun matchEnchant(key: String): Enchantment? {
        val split = key.split(':')
        val k = if (split.size == 1) NamespacedKey.minecraft(key)
        else if (split.size == 2) NamespacedKey(
            split[0], split[1]
        ) else return null
        return Enchantment.getByKey(k)
    }

    /**
     * 模拟玩家背包检查是否能添加物品
     * @return 溢出的物品数量
     */
    fun Player.canAddItem(vararg itemStacks: ItemStack): Int {
        val createInventory = Bukkit.createInventory(this, 36)
        //将需要输入的物品合并
        val addItem = createInventory.addItem(*itemStacks)
        val sortedItems = createInventory.mapNotNull { it }
        createInventory.contents = inventory.storageContents
        val addItems = createInventory.addItem(*sortedItems.toTypedArray())
        return addItems.size + addItem.size
    }

    /**
     * 模拟玩家背包检查是否能添加物品
     * @return 溢出的物品数量
     */
    fun Player.canAddItem(itemStacks: Collection<ItemStack>): Int = canAddItem(*itemStacks.toTypedArray())

    /**
     * 模拟背包检查是否能添加物品
     * @return 溢出的物品数量
     */
    fun Inventory.canAddItem(vararg itemStacks: ItemStack): Int {
        val createInventory = Bukkit.createInventory(null, this.contents.size)
        val addItem = createInventory.addItem(*itemStacks)
        val sortedItems = createInventory.mapNotNull { it }
        createInventory.contents = this.contents
        val addItems = createInventory.addItem(*sortedItems.toTypedArray())
        return addItems.size + addItem.size
    }

    /**
     * 模拟背包检查是否能添加物品
     * @return 溢出的物品数量
     */
    fun Inventory.canAddItem(itemStacks: Collection<ItemStack>): Int = canAddItem(*itemStacks.toTypedArray())

    fun ItemStack.getDisplayName(): String? {
        if (!hasItemMeta() || !itemMeta!!.hasDisplayName()) return null
        return itemMeta!!.displayName
    }
}