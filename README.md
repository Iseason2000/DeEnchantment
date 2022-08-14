# 负魔书

<h4 align="center">☄️ 开源的负魔插件, 使用 Kotlin 语言.</h4>
<p align="center">
    <a href="https://www.codefactor.io/repository/github/iseason2000/deenchantment" alt="CodeFactor Score">
        <img src="https://www.codefactor.io/repository/github/iseason2000/deenchantment/badge"/>
    </a>
    <a href="https://www.mcbbs.net/thread-1198268-1-1.html">
        <img alt="mcbbs" src="https://img.shields.io/badge/mcbbs-deenchantment-brightgreen"/>
    </a>
    <a href="https://bstats.org/plugin/bukkit/DeEnchantment/13440" alt="bstats servers">
        <img src="https://img.shields.io/bstats/servers/13440?color=brightgreen"/>
    </a>
    <a href="https://bstats.org/plugin/bukkit/DeEnchantment/13440" alt="bstats players">
        <img src="https://img.shields.io/bstats/players/13440?color=brightgreen"/>
    </a>
    <a href="https://github.com/Iseason2000/DeEnchantment" alt="source code">
        <img src="https://img.shields.io/badge/source-code-brightgreen"/>
    </a>
</p>

> 给原版增加一些对立附魔，有好有坏

V2 重构了80%以上的代码，大部提升性能与功能

## V2特点

* 附魔描述
* 支持热重载
* 大幅提升性能
* 高度自定义消息
* 旧版数据迁移支持
* 自定义负魔各种细节
* 配置自动重载(某些功能仍需要手动重载)

## 兼容插件

* EcoEnchants
* Slimefun4 自动袪魔、负魔机
* ExcellentEnchants
* 其他各种附魔插件，如需兼容请提issue

## 附魔对照

**部分负魔功能较V1有变化，请注意**
![img](https://user-images.githubusercontent.com/65019366/182375428-b02a48ea-8b45-49f2-b6b4-a425c46fd74a.png)

## 部分效果截图

![duochong](https://user-images.githubusercontent.com/65019366/117104859-72182880-adaf-11eb-8259-ed838d76ef1f.jpg)
![beipan](https://user-images.githubusercontent.com/65019366/117104865-76444600-adaf-11eb-9536-b5c32a4b41ae.jpg)
![ronyanxingzhe](https://user-images.githubusercontent.com/65019366/117104866-76dcdc80-adaf-11eb-93ea-fb88d13311af.jpg)
![tanshewuxiying](https://user-images.githubusercontent.com/65019366/117104867-780e0980-adaf-11eb-8bbd-a6d2c637bbcd.jpg)

## 配置

~~~ yml
# 是否能够在铁砧中使用
anvil: true

# 是否应用于砂轮祛魔
grindstone: true

# 是否可以通过附魔台获得
enchant: true

# 是否在有战利品的箱子中出现
chestLoot: true

# 是否应用于自然生成的怪物身上
spawn: true

# 是否应用于交易
trade: true

# 是否应用于钓鱼获取
fishing: true

# 是否应用于生物给予（猪灵交易、村庄英雄等生物丢物品行为）
reward: true

# 是否突破等级上限
levelUnlimited: false

# 是否允许铁砧过于昂贵仍能附魔，如果开启突破等级上限建议开启
tooExpensive: false

# 是否开启控制台精简模式：开启后不会出现 所有附魔的名字
cleanConsole: false

# 是否显示负魔描述
allowDescription: true

~~~

## 单一负魔配置例子，每个负魔都有区别

~~~ yaml

# 是否启用
enable: true

# 负魔名称
translate-name: §a灵魂绑定

# 负魔描述
description: §8 - 绑定玩家的灵魂仅允许使用

# 负魔目标,在此挑选: https://bukkit.windit.net/javadoc/org/bukkit/enchantments/EnchantmentTarget.html
target: BREAKABLE

# 负魔变异概率 0~1
chance: 0.2

# 负魔最大等级
max-level: 1

# 互相冲突的负魔
conflicts:
- DE_VANISHING_CURSE

# 当有灵魂绑定的物品消耗耐久时将绑定为该玩家，并发送消息
bindMessage: '&a您的装备已绑定您的灵魂'

# 不能使用时发送的消息
ownerMessage: '&c你不能使用绑定了别人灵魂的装备!'

# 防止被他人用于铁砧
denyAnvil: true

# 防止被他人用于砂轮
denyGrindStone: true

# 防止被他人捡起
denyPickup: true

# 描述中用于替换玩家名字的占位符
placeHolder: 玩家

~~~

## 命令

~~~ 

命令全部默认OP权限
deenchantment 缩写 de、den 权限:deenchantment.
/deenchantment give [player] [name] <level> 给予玩家特定负魔书 权限:deenchantment.give
/deenchantment random [type] [player] <level> 给予玩家随机负魔,不指定等级则随机，不超最大等级 权限:deenchantment.random
/deenchantment add [name] <level> 将特定负魔添加到手上的东西上 权限:deenchantment.add
/deenchantment update 更新手上装备的负魔描述及名称 权限:deenchantment.update
/deenchantment reload 重新注册负魔 权限:deenchantment.reload
/deenchantment purification [player] 将玩家手上物品的负魔转为正常的附魔 权限:deenchantment.purification
/deenchantment migrate 将v1版本的旧配置迁移到新版 权限:deenchantment.migrate

~~~

## 构建插件

./gradlew buildplugin

![](https://bstats.org/signatures/bukkit/DeEnchantment.svg)
