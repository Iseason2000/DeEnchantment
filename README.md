# 负魔书

> 给原版增加一些对立附魔，有好有坏

## 附魔突变机制

附魔有概率突变，应用于 **附魔台附魔物品**、**生成的怪物身上的装备**、**村民交易**、**钓鱼**、**猪灵交易**，概率可调、每一种都可以单独开关。

突变附魔可以在铁砧中像原版附魔一样正常使用。

## 铁砧机制（可开关）

添加了此插件会对铁砧使用经验消耗作出修改，具体公式为 ：**最终经验 = 附魔等级增量 + 修复次数 X 2 +1**

最终表现为与原版经验略有出入，但不会太大（变得简单）。

增加**突破等级上限**的开关（**默认关，可在配置开启**）可以在铁砧中合并出高于原版附魔的等级（**经验超过39**在客户端会显示**过于昂贵**，但实际经验够还是**能附魔**，会**显示需要的经验在聊天栏**）

## 附魔对照

| 英文名| 中文名 | 描述 | 英文名2| 中文名2| 描述2|
| --------------------- | ---------- | :--------------------------: | ----------- | ---------- | -------------------------- |
| protection| 保护 |减少多数的伤害| de_protection| 保护不了 | 增加玩家受到的伤害 |
| fire_protection | 火焰保护 | 减少火焰伤害 | de_fire_protection | 火焰灼烧 | 受到攻击时有几率使自己着火 |
| feather_falling | 摔落保护 | 减少跌落伤害 | de_feather_falling | 摔落骨折 | 增加玩家受到的掉落伤害并附加缓慢效果 |
| blast_protection| 爆炸保护 | 减少爆炸伤害 | de_blast_protection| 瞬间爆炸 | 受到攻击时有几率产生爆炸 |
| projectile_protection | 弹射物保护 |减少来源于弹射物的伤害| de_projectile_protection | 弹射物吸引 | 主动吸引附近的弹射物 |
| respiration | 水下呼吸 | 延长水下呼吸时间 | de_respiration | 水下窒息 | 增加玩家水下氧气消耗的速度 |
| aqua_affinity | 水下速掘 | 加快水下挖掘速度 | de_aqua_affinity | 水下慢掘 | 减缓玩家在水下挖掘方块的速度 |
| thorns| 荆棘 |给予攻击者伤害| de_thorns| 负荆请罪 | 移动时有概率受到伤害 |
| depth_strider | 深海探索者 | 增加水下行走速度 | de_depth_strider | 旱鸭子 | 减少水下视野 |
| frost_walker| 冰霜行者 | 允许水上行走 | de_frost_walker| 熔岩行者 | 允许在岩浆上行走 |
| binding_curse | 绑定诅咒 |被诅咒物品穿在身上后除非玩家处于创造模式，否则无法卸下| de_binding_curse | 灵魂绑定 | 物品不会死亡掉落 |
| sharpness | 锋利 | 增加近战攻击伤害 | de_sharpness | 磨钝 | 减少攻击伤害 |
| smite | 亡灵杀手 |对亡灵生物造成额外伤害| de_smite | 亡灵救赎 | 对亡灵生物伤害减少（可以为负） |
| bane_of_arthropods| 截肢杀手 |对节肢生物造成额外伤害| de_bane_of_arthropods| 节肢救星 | 对截肢生物伤害减少（可以为负） |
| knockback | 击退 | 增加击退距离 | de_knockback | 退击 | 攻击时会让自己后跳 |
| fire_aspect | 火焰附加 |使目标着火| fire_aspect| 引火烧身 | 攻击者会让自己着火 |
| looting | 抢夺 |生物能掉落更多物品| de_looting | 知足 | 生物掉落物品有概率消失 |
| sweeping| 横扫之刃 | 增加横扫攻击伤害 | de_sweeping| 横扫失败 | 概率取消横扫 |
| efficiency| 效率 | 加快挖掘速度 | de_efficiency| 低效 | 挖掘方块时有概率失败                 |
| silk_touch| 精准采集 | 被开采的方块掉落本身 | de_silk_touch| 彻底粉碎 | 挖掘不会产生掉落物 |
| unbreaking| 耐久 | 减少物品掉耐久的几率 | de_unbreaking| 易损 | 增加物品掉的耐久 |
| fortune | 时运 | 增加方块掉落 | de_fortune | 时运不济 | 有概率不掉物品 |
| power | 力量 | 增加弓箭伤害 | de_power | 虚弱 | 减少弓箭伤害及箭矢速度 |
| punch | 冲击 |增加弓箭的击退距离| de_punch | 吸引 | 吸引目标 |
| flame | 火矢 |箭矢使目标着火| de_flame | 神速 | 箭矢速度增加 |
| infinity| 无限 | 射箭不会消耗普通箭矢 | de_infinity| 多重 | 一次发射3支箭，但只消耗1支 |
| luck_of_the_sea | 海之眷顾 | 提高钓鱼时获得宝藏的几率 | de_luck_of_the_sea | 海之嫌弃 | 减少钓鱼时获得宝藏的几率 |
| lure| 钓饵 | 提高鱼咬钩的速度 | de_lure| 过期钓饵 | 减少鱼咬钩的速度 |
| loyalty | 忠诚 | 使掷出后的三叉戟返回，魔咒等级越高，三叉戟返回的用时越短 | de_loyalty | 背叛 | 三叉戟有概率会成为附近生物的武器 |
| impaling| 穿刺 |对水生生物‌‌[仅Java版]造成额外伤害| de_impaling| 刺穿 | 对非水生生物造成额外伤害 |
| riptide | 激流 | 将玩家向掷出三叉戟的方向发射，仅在水中或雨中生效 | de_riptide | 逆流 | 方向相反 |
| channeling| 引雷 |在雨天召唤闪电攻击生物，仅在雷暴时生效| de_channeling| 避雷针 | 命中目标后雷劈自己 |
| multishot | 多重射击 | 消耗一支箭可射出三只箭矢 | de_multishot | 连珠 | 消耗3支箭矢进行3次连续射击 |
| quick_charge| 快速填充 | 减少弩的填装时间 | de_quick_charge| 慢速填充 | 增加填充时间 |
| piercing| 穿透 |使箭矢穿过多个实体| de_piercing| 反弹 | 箭矢往回弹 |
| mending | 经验修补 |损耗经验以修补工具的耐久度| de_mending | 经验反哺 | 将耐久转换为经验 |
| vanishing_curse | 消失诅咒 | 被诅咒物品会在玩家死亡时消失 | de_vanishing_curse | 永存祝福 | 物品作为掉落物时不会消失 |
| soul_speed| 灵魂疾行 |提高玩家在灵魂沙和灵魂土上的移动速度。| de_soul_speed| 疾行 | 给予玩家永久的速度效果 |

## 部分效果截图

![beipan](C:\Users\Iseason\Desktop\beipan.jpg)
![ronyanxingzhe](C:\Users\Iseason\Desktop\ronyanxingzhe.jpg)
![duochong](C:\Users\Iseason\Desktop\duochong.jpg)
![tanshewuxiying](C:\Users\Iseason\Desktop\tanshewuxiying.jpg)

## 配置

~~~ yml
# 应用开关 true | false

# 是否能够在铁砧中使用
Anvil: false
# 是否在有战利品的箱子中出现
Chest: true
# 是否可以通过附魔台获得
EnchantTable: true
# 是否应用于自然生成的怪物身上
Mobs: true
# 是否应用于村民交易
Villager: true
# 是否应用于钓鱼获取
Fishing: true

# 特性设置 true | false
# 是否突破等级上限
LevelUnlimited: false
# 是否开启控制台精简模式：开启后不会出现 所有附魔的名字
CleanConsole: false

#附魔:
# Enable:开/关
# DisplayName: 显示名称
# Chance :出现概率(0~1)
DE_PROTECTION:
  Enable: true
  DisplayName: "&7保护不能"
  Chance: 0.2
  ...
~~~

## 命令

~~~ 
/deenchantment reload   -----重载配置
/deenchantment add [附魔名称] [等级] 给手上的物品添加附魔（仅限本插件的附魔）
/deenchantment give [附魔名称] [等级] 给与自己该附魔的附魔书（仅限本插件的附魔）
~~~

