package co.ke.jamboapps.roadtrip.db

import com.dbflow5.annotation.Column
import com.dbflow5.annotation.PrimaryKey
import com.dbflow5.annotation.Table
import com.dbflow5.config.database
import com.dbflow5.query.delete
import com.dbflow5.query.select
import com.dbflow5.structure.save


@Table(database = AppDb::class)
class User(
    @PrimaryKey(autoincrement = true)
    var id: Int = 0,

    @Column
    var userCode: Int = 0,

    @Column
    var userId: String = "",

    @Column
    var userName: String = "",

    @Column
    var email: String = "",

    @Column
    var userStatus: Int = 0,

    @Column
    var devId: String = "",
) {
    companion object {
        fun get(): User? {
            return (select from User::class).querySingle(AppDb.getDb())
        }

        fun create(id: String, code: Int, devId: String) {
            delete<User>().execute(AppDb.getDb())

            val user = User()
            user.userId = id
            user.userCode = code
            user.devId = devId
            user.save(AppDb.getDb())
        }
    }
}