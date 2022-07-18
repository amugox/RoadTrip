package co.ke.jamboapps.roadtrip.db

import com.dbflow5.annotation.Database
import com.dbflow5.config.DBFlowDatabase
import com.dbflow5.config.database
import com.dbflow5.query.delete

@Database(version = AppDb.VERSION)
abstract class AppDb : DBFlowDatabase() {
    companion object {
        const val VERSION = 1

        fun getDb() = database<AppDb>()

        fun clearDb() {
            val db = database<AppDb>()
            delete<User>().execute(db)
            delete<MyRoute>().execute(db)
        }
    }
}