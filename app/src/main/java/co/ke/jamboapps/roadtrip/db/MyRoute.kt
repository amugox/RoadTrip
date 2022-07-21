package co.ke.jamboapps.roadtrip.db

import com.dbflow5.annotation.Column
import com.dbflow5.annotation.PrimaryKey
import com.dbflow5.annotation.Table
import com.dbflow5.query.select
import com.dbflow5.structure.save
import com.dbflow5.structure.update
import java.util.*

@Table(database = AppDb::class)
class MyRoute(
    @PrimaryKey(autoincrement = true)
    var id: Int = 0,

    @Column
    var code: Int = 0,

    @Column
    var routeId: String = "",

    @Column
    var routeName: String = "",

    @Column
    var details: String = "",

    @Column
    var myRoute: Int = 0,

    @Column
    var createdOn: Date? = null,

    @Column
    var markStat: Int = 0,
) {
    fun save() {
        this.save(AppDb.getDb())
    }

    fun update() {
        this.update(AppDb.getDb())
    }

    override fun toString(): String =
        "Code:$code, Title:$routeName, Details:$details, Mark Status:$markStat"

    companion object {

        fun get(code: Int): MyRoute? =
            (select from MyRoute::class where (MyRoute_Table.code.eq(code))).querySingle(AppDb.getDb())

        fun getList(): MutableList<MyRoute> =
            (select from MyRoute::class orderBy (MyRoute_Table.code.desc())).queryList(AppDb.getDb())

    }
}