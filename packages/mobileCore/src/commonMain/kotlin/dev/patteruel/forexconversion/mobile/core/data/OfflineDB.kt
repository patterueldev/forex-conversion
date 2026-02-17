package dev.patteruel.forexconversion.mobile.core.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.ConstructedBy
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

// Entity representing a stored conversion rate between two currencies.
@Entity(tableName = "rates", primaryKeys = ["from_currency", "to_currency"])
data class RateEntity(
    @ColumnInfo(name = "from_currency") val fromCurrency: String,
    @ColumnInfo(name = "to_currency") val toCurrency: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = 0
)

@Dao
interface RateDao {
    @Query("SELECT * FROM rates WHERE from_currency = :from AND to_currency = :to")
    suspend fun getRate(from: String, to: String): RateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rate: RateEntity)

    @Query("DELETE FROM rates WHERE from_currency = :from AND to_currency = :to")
    suspend fun delete(from: String, to: String)
}

// Expect object for platform-specific Room database constructor
expect object PlatformOfflineDatabase : RoomDatabaseConstructor<OfflineDatabase>

// Room Database declaration. Platform modules must provide the SQLite driver and set it on the Room builder
@Database(entities = [RateEntity::class], version = 2, exportSchema = false)
@ConstructedBy(PlatformOfflineDatabase::class)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao
}
