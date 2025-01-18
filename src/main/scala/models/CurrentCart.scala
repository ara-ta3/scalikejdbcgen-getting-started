package models

import scalikejdbc._

case class CurrentCart(
  id: Int,
  userId: Int,
  itemId: Int,
  quantity: Int) {

  def save()(implicit session: DBSession = CurrentCart.autoSession): CurrentCart = CurrentCart.save(this)(session)

  def destroy()(implicit session: DBSession = CurrentCart.autoSession): Int = CurrentCart.destroy(this)(session)

}


object CurrentCart extends SQLSyntaxSupport[CurrentCart] {

  override val tableName = "current_cart"

  override val columns = Seq("id", "user_id", "item_id", "quantity")

  def apply(cc: SyntaxProvider[CurrentCart])(rs: WrappedResultSet): CurrentCart = apply(cc.resultName)(rs)
  def apply(cc: ResultName[CurrentCart])(rs: WrappedResultSet): CurrentCart = new CurrentCart(
    id = rs.get(cc.id),
    userId = rs.get(cc.userId),
    itemId = rs.get(cc.itemId),
    quantity = rs.get(cc.quantity)
  )

  val cc = CurrentCart.syntax("cc")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[CurrentCart] = {
    withSQL {
      select.from(CurrentCart as cc).where.eq(cc.id, id)
    }.map(CurrentCart(cc.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CurrentCart] = {
    withSQL(select.from(CurrentCart as cc)).map(CurrentCart(cc.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(CurrentCart as cc)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[CurrentCart] = {
    withSQL {
      select.from(CurrentCart as cc).where.append(where)
    }.map(CurrentCart(cc.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CurrentCart] = {
    withSQL {
      select.from(CurrentCart as cc).where.append(where)
    }.map(CurrentCart(cc.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(CurrentCart as cc).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    userId: Int,
    itemId: Int,
    quantity: Int)(implicit session: DBSession = autoSession): CurrentCart = {
    val generatedKey = withSQL {
      insert.into(CurrentCart).namedValues(
        column.userId -> userId,
        column.itemId -> itemId,
        column.quantity -> quantity
      )
    }.updateAndReturnGeneratedKey.apply()

    CurrentCart(
      id = generatedKey.toInt,
      userId = userId,
      itemId = itemId,
      quantity = quantity)
  }

  def batchInsert(entities: collection.Seq[CurrentCart])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "userId" -> entity.userId,
        "itemId" -> entity.itemId,
        "quantity" -> entity.quantity))
    SQL("""insert into current_cart(
      user_id,
      item_id,
      quantity
    ) values (
      {userId},
      {itemId},
      {quantity}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: CurrentCart)(implicit session: DBSession = autoSession): CurrentCart = {
    withSQL {
      update(CurrentCart).set(
        column.id -> entity.id,
        column.userId -> entity.userId,
        column.itemId -> entity.itemId,
        column.quantity -> entity.quantity
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: CurrentCart)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(CurrentCart).where.eq(column.id, entity.id) }.update.apply()
  }

}
