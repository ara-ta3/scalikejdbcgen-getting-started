package models

import scalikejdbc._
import java.time.ZonedDateTime

case class Orders(
  id: Int,
  userId: Int,
  orderDate: Option[ZonedDateTime] = None,
  totalAmount: BigDecimal) {

  def save()(implicit session: DBSession = Orders.autoSession): Orders = Orders.save(this)(session)

  def destroy()(implicit session: DBSession = Orders.autoSession): Int = Orders.destroy(this)(session)

}


object Orders extends SQLSyntaxSupport[Orders] {

  override val tableName = "orders"

  override val columns = Seq("id", "user_id", "order_date", "total_amount")

  def apply(o: SyntaxProvider[Orders])(rs: WrappedResultSet): Orders = apply(o.resultName)(rs)
  def apply(o: ResultName[Orders])(rs: WrappedResultSet): Orders = new Orders(
    id = rs.get(o.id),
    userId = rs.get(o.userId),
    orderDate = rs.get(o.orderDate),
    totalAmount = rs.get(o.totalAmount)
  )

  val o = Orders.syntax("o")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Orders] = {
    withSQL {
      select.from(Orders as o).where.eq(o.id, id)
    }.map(Orders(o.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Orders] = {
    withSQL(select.from(Orders as o)).map(Orders(o.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Orders as o)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Orders] = {
    withSQL {
      select.from(Orders as o).where.append(where)
    }.map(Orders(o.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Orders] = {
    withSQL {
      select.from(Orders as o).where.append(where)
    }.map(Orders(o.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Orders as o).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    userId: Int,
    orderDate: Option[ZonedDateTime] = None,
    totalAmount: BigDecimal)(implicit session: DBSession = autoSession): Orders = {
    val generatedKey = withSQL {
      insert.into(Orders).namedValues(
        column.userId -> userId,
        column.orderDate -> orderDate,
        column.totalAmount -> totalAmount
      )
    }.updateAndReturnGeneratedKey.apply()

    Orders(
      id = generatedKey.toInt,
      userId = userId,
      orderDate = orderDate,
      totalAmount = totalAmount)
  }

  def batchInsert(entities: collection.Seq[Orders])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "userId" -> entity.userId,
        "orderDate" -> entity.orderDate,
        "totalAmount" -> entity.totalAmount))
    SQL("""insert into orders(
      user_id,
      order_date,
      total_amount
    ) values (
      {userId},
      {orderDate},
      {totalAmount}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Orders)(implicit session: DBSession = autoSession): Orders = {
    withSQL {
      update(Orders).set(
        column.id -> entity.id,
        column.userId -> entity.userId,
        column.orderDate -> entity.orderDate,
        column.totalAmount -> entity.totalAmount
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Orders)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Orders).where.eq(column.id, entity.id) }.update.apply()
  }

}
