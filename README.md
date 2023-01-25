# AttributeSystem II

插件永久免费

---

## 插件

| 说明   | 内容                                 |
|------|------------------------------------|
| 兼容版本 | 1.12.2+ (理论)                       |
| 硬依赖  | Pouvoir                            |
| 软依赖  | PlaceholderAPI Mythicmobs SkillAPI |

## 介绍

**AttributeSystem** 是基于 **TabooLib VI** & **Pouvoir** 编写的一款属性引擎插件  
奉单一职责的宗旨，本引擎仅提供属性 属性读取 属性实现框架，具体属性与功能需要通过 配置 / API 来实现

**AttributeSystem** 提供包括但不限于以下[**API**](http://doc.skillw.com/attsystem/):

- AttributeSystemAPI
- AttributeManager
- ConditionManager
- EquipmentDataManager
- AttributeDataManager
- OperationManager
- RealizeManager
- ReadPatternManager

你可以通过编写代码/脚本来拓展**AttributeSystem**的诸多功能

### 包括但不限于

#### 属性 (Attribute)

```kotlin
Attribute.createAttribute("example", ReadGroup) {
    names += "示例属性"
}.register()
```

```javascript
var Attribute = find('com.skillw.attsystem.api.attribute.Attribute')
var ReadGroup = static('ReadGroup')

//@Awake(Active)
function regAtt() {
    Attribute.createAttribute("example", ReadGroup, (builder) => {
        builder.names.add("示例属性")
    }).register()
}
```

#### 读取格式 (Read Pattern)

```kotlin
@AutoRegister
object MyReadPattern : ReadPattern("my_read_pattern") {
    override fun read(string: String, attribute: Attribute, entity: LivingEntity?, slot: String?): Status? {
        //code
    }

    override fun readNBT(map: Map<String, Any>, attribute: Attribute): Status? {
        //code
    }

    override fun placeholder(key: String, attribute: Attribute, status: Status, entity: LivingEntity?): Any? {
        //code
    }

    override fun stat(attribute: Attribute, status: Status, entity: LivingEntity?): TellrawJson {
        //code
    }
}
```

```javascript
//@ReadPattern(my_read_pattern)

function read(string, attribute, entity, slot
) {
    //code
}

function readNBT(map, attribute) {
    //code
}

function placeholder(key, attribute, status, entity) {
    //code
}

function stat(attribute, status, entity) {
    //code
}
```

#### 条件 (Condition)

```kotlin
@AutoRegister
object LevelCondition :
    BaseCondition("level", setOf("Lv\\.(?<value>\\d+)", "等级限制: (?<value>\\d+)"), ConditionType.ALL) {
    override fun condition(slot: String?, entity: LivingEntity?, matcher: Matcher, text: String): Boolean {
        entity ?: return true
        if (entity !is Player) return true
        val level = Coerce.toInteger(matcher.group("value"))
        return entity.level >= level
    }
}
```

```javascript
var Coere = static('Coere')

//@Condition(Level,ALL,Lv\.(?<value>\d+),等级限制: (?<value>\d+))
function level(slot, entity, matcher, text) {
    var hasEntity = entity != "null"
    if (!hasEntity) return true
    if (!(entity instanceof Player)) {
        return true
    }
    var level = Coerce.toInteger(matcher.group("value"))
    return entity.level >= level
}

```

<br/>

## Links

WIKI [http://blog.skillw.com/#sort=attsystem&doc=README.md](http://blog.skillw.com/#sort=attsystem&doc=README.md)

JavaDoc [http://doc.skillw.com/attsystem/](http://doc.skillw.com/attsystem/)

MCBBS [https://www.mcbbs.net/forum.php?mod=viewthread&tid=1307249](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1307249)

爱发电 [https://afdian.net/@glom\_](https://afdian.net/@glom_)
