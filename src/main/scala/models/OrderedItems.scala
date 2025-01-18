package models

import scalikejdbc._

case class OrderedItems(
  id: Int,
  orderId: Int,
  itemId: Int,
  quantity: Int,
  priceAtPurchase: BigDecimal) {

  def save()(implicit session: DBSession = OrderedItems.autoSession): OrderedItems = OrderedItems.save(this)(session)

  def destroy()(implicit session: DBSession = OrderedItems.autoSession): Int = OrderedItems.destroy(this)(session)

}


object OrderedItems extends SQLSyntaxSupport[OrderedItems] {

  override val tableName = "ordered_items"

  override val columns = Seq("id", "order_id", "item_id", "quantity", "price_at_purchase")

  def apply(oi: SyntaxProvider[OrderedItems])(rs: WrappedResultSet): OrderedItems = apply(oi.resultName)(rs)
  def apply(oi: ResultName[OrderedItems])(rs: WrappedResultSet): OrderedItems = new OrderedItems(
    id = rs.get(oi.id),
    orderId = rs.get(oi.orderId),
    itemId = rs.get(oi.itemId),
    quantity = rs.get(oi.quantity),
    priceAtPurchase = rs.get(oi.priceAtPurchase)
  )

  val oi = OrderedItems.syntax("oi")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[OrderedItems] = {
    withSQL {
      select.from(OrderedItems as oi).where.eq(oi.id, id)
    }.map(OrderedItems(oi.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[OrderedItems] = {
    withSQL(select.from(OrderedItems as oi)).map(OrderedItems(oi.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(OrderedItems as oi)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[OrderedItems] = {
    withSQL {
      select.from(OrderedItems as oi).where.append(where)
    }.map(OrderedItems(oi.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[OrderedItems] = {
    withSQL {
      select.from(OrderedItems as oi).where.append(where)
    }.map(OrderedItems(oi.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(OrderedItems as oi).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    orderId: Int,
    itemId: Int,
    quantity: Int,
    priceAtPurchase: BigDecimal)(implicit session: DBSession = autoSession): OrderedItems = {
    val generatedKey = withSQL {
      insert.into(OrderedItems).namedValues(
        column.orderId -> orderId,
        column.itemId -> itemId,
        column.quantity -> quantity,
        column.priceAtPurchase -> priceAtPurchase
      )
    }.updateAndReturnGeneratedKey.apply()

    OrderedItems(
      id = generatedKey.toInt,
      orderId = orderId,
      itemId = itemId,
      quantity = quantity,
      priceAtPurchase = priceAtPurchase)
  }

  def batchInsert(entities: collection.Seq[OrderedItems])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "orderId" -> entity.orderId,
        "itemId" -> entity.itemId,
        "quantity" -> entity.quantity,
        "priceAtPurchase" -> entity.priceAtPurchase))
    SQL("""insert into ordered_items(
      order_id,
      item_id,
      quantity,
      price_at_purchase
    ) values (
      {orderId},
      {itemId},
      {quantity},
      {priceAtPurchase}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: OrderedItems)(implicit session: DBSession = autoSession): OrderedItems = {
    withSQL {
      update(OrderedItems).set(
        column.id -> entity.id,
        column.orderId -> entity.orderId,
        column.itemId -> entity.itemId,
        column.quantity -> entity.quantity,
        column.priceAtPurchase -> entity.priceAtPurchase
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: OrderedItems)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(OrderedItems).where.eq(column.id, entity.id) }.update.apply()
  }

}
