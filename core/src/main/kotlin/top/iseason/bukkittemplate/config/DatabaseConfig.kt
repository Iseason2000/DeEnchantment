package top.iseason.bukkittemplate.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.configuration.ConfigurationSection
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.DisableHook
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.debug.debug
import top.iseason.bukkittemplate.debug.info
import java.io.File
import java.util.*

@FilePath("database.yml")
object DatabaseConfig : SimpleYAMLConfig() {

    @Key
    @Comment("", "修改完配置保存时是否自动重连数据库")
    var autoReload = true

    @Comment(
        "",
        "数据库驱动类型: 支持 MySQL、MariaDB、SQLite、Oracle、PostgreSQL、SQLServer",
        "如果你的 MySQL 总是连不上请将驱动类型改为 MariaDB，它支持连接到mysql"
    )
    @Key
    var database_type = "SQLite"

    @Comment("", "数据库地址")
    @Key
    var address = File(BukkitTemplate.getPlugin().dataFolder, "database.db").absoluteFile.toString()

    @Comment("", "数据库名")
    @Key
    var database_name = "database_${BukkitTemplate.getPlugin().name}"

    @Comment(
        "",
        "jdbcUrl 最后面的参数, 紧跟在database-name后面,请注意添加分隔符",
        "如果你的mysql提示 ssl 有关的信息请添加: '?useSSL=false'"
    )
    @Key
    var params = ""

    @Comment(
        "",
        "完整的jdbcUrl由 address、database-name、params 根据数据库类型拼接而来",
        "如果您发现拼接的url有误可以自定义url，留空则关闭"
    )
    @Key
    var custom_jdbcUrl = ""

    @Comment("", "数据库用户名，如果有的话")
    @Key
    var user = "user"

    @Comment("", "数据库密码，如果有的话")
    @Key
    var password = "password"

    @Key
    @Comment("", "连接池设置，不懂不要乱调, 配置解释: https://github.com/brettwooldridge/HikariCP")
    var data_source = ""

    @Key
    var data_source__autoCommit = true

    @Key
    var data_source__connectionTimeout = 30000L

    @Key
    var data_source__idleTimeout = 600000L

    @Key
    var data_source__keepaliveTime = 30000L

    @Key
    var data_source__maxLifetime = 1800000L

    @Key
    var data_source__connectionTestQuery = "SELECT 1"

    @Key
    var data_source__minimumIdle = 1

    @Key
    var data_source__maximumPoolSize = 5

    @Key
    var data_source__initializationFailTimeout = 1L

    @Key
    var data_source__isolateInternalQueries = false

    @Key
    var data_source__allowPoolSuspension = false

    @Key
    var data_source__readOnly = false

    @Key
    var data_source__registerMbeans = false

    @Key
    var data_source__connectionInitSql = ""

    @Key
    var data_source__transactionIsolation = ""

    @Key
    var data_source__validationTimeout = 5000L

    @Key
    var data_source__leakDetectionThreshold = 0L


    // table缓存
    private var tables: Array<out Table> = emptyArray()
    var isConnected = false
        private set
    private var isConnecting = false
    lateinit var connection: Database
        private set
    private var ds: HikariDataSource? = null

    init {
        DisableHook.addTask { closeDB() }
    }

    override fun onLoaded(section: ConfigurationSection) {
        isAutoUpdate = autoReload
        reConnected()
        if (tables.isNotEmpty()) {
            initTables(*tables)
        }
    }

    /**
     * 链接数据库
     */
    fun reConnected() {
        if (isConnecting) return
        info("&6数据库链接中...")
        isConnecting = true
        closeDB()
        runCatching {
            val runtimeManager = BukkitTemplate.getRuntimeManager()
                .addRepository("https://maven.aliyun.com/repository/public")
                .addRepository("https://repo.maven.apache.org/maven2/")
            val props = Properties().apply {
                setProperty("autoCommit", data_source__autoCommit.toString())
                setProperty("connectionTimeout", data_source__connectionTimeout.toString())
                setProperty("idleTimeout", data_source__idleTimeout.toString())
                setProperty("maxLifetime", data_source__maxLifetime.toString())
                setProperty("connectionTestQuery", data_source__connectionTestQuery)
                setProperty("minimumIdle", data_source__minimumIdle.toString())
                setProperty("maximumPoolSize", data_source__maximumPoolSize.toString())
                setProperty("isolateInternalQueries", data_source__isolateInternalQueries.toString())
                setProperty("allowPoolSuspension", data_source__allowPoolSuspension.toString())
                setProperty("readOnly", data_source__readOnly.toString())
                setProperty("registerMbeans", data_source__registerMbeans.toString())
                setProperty("connectionInitSql", data_source__connectionInitSql)
                setProperty("transactionIsolation", data_source__transactionIsolation)
                setProperty("leakDetectionThreshold", data_source__leakDetectionThreshold.toString())
            }
            val config = when (database_type) {
                "MySQL" -> HikariConfig(props).apply {
                    runtimeManager.downloadADependency("mysql:mysql-connector-java:8.0.30")
                    jdbcUrl = "jdbc:mysql://$address/$database_name$params"
                    //可能兼容旧的mysql驱动
                    driverClassName = "com.mysql.jdbc.Driver"
                }

                "MariaDB" -> HikariConfig(props).apply {
                    runtimeManager.downloadADependency("org.mariadb.jdbc:mariadb-java-client:3.1.3")
                    jdbcUrl = "jdbc:mariadb://$address/$database_name$params"
                    driverClassName = "org.mariadb.jdbc.Driver"
                }

                "SQLite" -> HikariConfig(props).apply {
                    runtimeManager.downloadADependencyAssembly("org.xerial:sqlite-jdbc:3.41.2.0")
                    jdbcUrl = "jdbc:sqlite:$address$params"
                    driverClassName = "org.sqlite.JDBC"
                }

//                "H2" -> HikariConfig().apply {
//                    dd.downloadDependency("com.h2database:h2:2.1.214")
//                    jdbcUrl = "jdbc:h2:$url/$dbName$params"
//                    driverClassName = "org.h2.Driver"
//                }

                "PostgreSQL" -> HikariConfig(props).apply {
                    runtimeManager.downloadADependency("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
                    jdbcUrl = "jdbc:pgsql://$address/$database_name$params"
                    driverClassName = "com.impossibl.postgres.jdbc.PGDriver"
                }

                "Oracle" -> HikariConfig(props).apply {
                    runtimeManager.downloadADependency("com.oracle.database.jdbc:ojdbc8:21.9.0.0")
                    jdbcUrl = "dbc:oracle:thin:@//$address/$database_name$params"
                    driverClassName = "oracle.jdbc.OracleDriver"
                }

                "SQLServer" -> HikariConfig(props).apply {
                    runtimeManager.downloadADependency("com.microsoft.sqlserver:mssql-jdbc:11.2.3.jre8")
                    jdbcUrl = "jdbc:sqlserver://$address;DatabaseName=$database_name$params"
                    driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
                }

                else -> throw Exception("错误的数据库类型!")
            }
            with(config) {
                if (this@DatabaseConfig.custom_jdbcUrl.isNotBlank())
                    jdbcUrl = this@DatabaseConfig.custom_jdbcUrl
                username = this@DatabaseConfig.user
                password = this@DatabaseConfig.password
                poolName = BukkitTemplate.getPlugin().name
                try {
                    validationTimeout = data_source__validationTimeout
                    initializationFailTimeout = data_source__initializationFailTimeout
                    keepaliveTime = data_source__keepaliveTime
                } catch (_: Throwable) {
                }
            }
            ds = HikariDataSource(config)
            connection = Database.connect(ds!!, databaseConfig = org.jetbrains.exposed.sql.DatabaseConfig.invoke {
                sqlLogger = MySqlLogger
            })
            isConnected = true
            info("&a数据库链接成功: &6$database_type")
        }.getOrElse {
            isConnected = false
            it.printStackTrace()
            info("&c数据库链接失败! 请检查数据库状态或数据库配置!")
        }
        isConnecting = false
    }

    /**
     * 关闭数据库
     */
    fun closeDB() {
        if (!isConnected) return
        runCatching {
            ds?.close()
            TransactionManager.closeAndUnregister(connection)
            isConnected = false
        }.getOrElse { it.printStackTrace() }
    }

    /**
     * 初始化表
     */
    fun initTables(vararg tables: Table) {
        if (!isConnected) return
        this.tables = tables
        runCatching {
            dbTransaction {
                SchemaUtils.createMissingTablesAndColumns(*tables)
//                SchemaUtils.create(*tables)
            }
        }.getOrElse { it.printStackTrace() }
    }

}

/**
 * varchar(255) 作为主键的table
 */
open class StringIdTable(name: String = "", columnName: String = "id") : IdTable<String>(name) {
    final override val id: Column<EntityID<String>> = varchar(columnName, 255).entityId()
    final override val primaryKey = PrimaryKey(id)
}

abstract class StringEntity(id: EntityID<String>) : Entity<String>(id)

abstract class StringEntityClass<out E : Entity<String>>(
    table: IdTable<String>,
    entityType: Class<E>? = null,
    entityCtor: ((EntityID<String>) -> E)? = null
) : EntityClass<String, E>(table, entityType, entityCtor)

object MySqlLogger : SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        debug("&6DEBUG SQL: &7${context.expandArgs(transaction)}")
    }
}

/**
 * 使用本插件数据库的事务
 */
fun <T> dbTransaction(statement: Transaction.() -> T) =
    transaction(DatabaseConfig.connection, statement)
