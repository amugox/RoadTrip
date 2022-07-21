package co.ke.jamboapps.roadtrip.db

import com.dbflow5.annotation.Column
import com.dbflow5.annotation.PrimaryKey
import com.dbflow5.annotation.Table
import com.dbflow5.query.and
import com.dbflow5.query.delete
import com.dbflow5.query.select
import com.dbflow5.structure.save

@Table(database = AppDb::class)
class RouteMark(
    @PrimaryKey(autoincrement = true)
    var id: Int = 0,

    @Column
    var code: Int = 0,

    @Column
    var type: Int = 2,

    @Column
    var lat: Double = 0.0,

    @Column
    var lon: Double = 0.0,

    @Column
    var text: String = "",

    @Column
    var stat: Int = 0,

    @Column
    var assistCode: Int = 0,
) {
    fun save() {
        this.save(AppDb.getDb())
    }

    companion object {
        fun clearMarks(route: Int) {
            delete<RouteMark>().where(RouteMark_Table.code.eq(route)).execute(AppDb.getDb())
        }

        fun getByRoute(route: Int): MutableList<RouteMark> =
            (select from RouteMark::class where (RouteMark_Table.code.eq(route)) orderBy (RouteMark_Table.id.asc())).queryList(
                AppDb.getDb()
            )

        fun getWaypoints(route: Int): MutableList<RouteMark> =
            (select from RouteMark::class where (RouteMark_Table.code.eq(route) and (RouteMark_Table.type.eq(2))) orderBy (RouteMark_Table.id.asc())).queryList(
                AppDb.getDb()
            )

        fun getStartMark(routeCode: Int): RouteMark? =
            (select from RouteMark::class where (RouteMark_Table.code.eq(routeCode)
                    and (RouteMark_Table.type.eq(
                1
            )))).querySingle(AppDb.getDb())

        fun getEndMark(routeCode: Int): RouteMark? =
            (select from RouteMark::class where (RouteMark_Table.code.eq(routeCode)
                    and (RouteMark_Table.type.eq(
                3
            )))).querySingle(AppDb.getDb())
    }
}