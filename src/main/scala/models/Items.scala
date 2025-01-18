package models

import scalikejdbc._

case class Items(
  id: Int,
  name: String,
  description: Option[String] = None,
  price: BigDecimal,
  stock: Option[Int] = None) {

  def save()(implicit session: DBSession = Items.autoSession): Items = Items.save(this)(session)

  def destroy()(implicit session: DBSession = Items.autoSession): Int = Items.destroy(this)(session)

}


object Items extends SQLSyntaxSupport[Items] {

  override val tableName = "items"

  override val columns = Seq("id", "name", "description", "price", "stock")

  def apply(i: SyntaxProvider[Items])(rs: WrappedResultSet): Items = apply(i.resultName)(rs)
  def apply(i: ResultName[Items])(rs: WrappedResultSet): Items = new Items(
    id = rs.get(i.id),
    name = rs.get(i.name),
    description = rs.get(i.description),
    price = rs.get(i.price),
    stock = rs.get(i.stock)
  )

  val i = Items.syntax("i")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Items] = {
    withSQL {
      select.from(Items as i).where.eq(i.id, id)
    }.map(Items(i.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Items] = {
    withSQL(select.from(Items as i)).map(Items(i.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Items as i)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Items] = {
    withSQL {
      select.from(Items as i).where.append(where)
    }.map(Items(i.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Items] = {
    withSQL {
      select.from(Items as i).where.append(where)
    }.map(Items(i.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Items as i).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: String,
    description: Option[String] = None,
    price: BigDecimal,
    stock: Option[Int] = None)(implicit session: DBSession = autoSession): Items = {
    val generatedKey = withSQL {
      insert.into(Items).namedValues(
        column.name -> name,
        column.description -> description,
        column.price -> price,
        column.stock -> stock
      )
    }.updateAndReturnGeneratedKey.apply()

    Items(
      id = generatedKey.toInt,
      name = name,
      description = description,
      price = price,
      stock = stock)
  }

  def batchInsert(entities: collection.Seq[Items])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "name" -> entity.name,
        "description" -> entity.description,
        "price" -> entity.price,
        "stock" -> entity.stock))
    SQL("""insert into items(
      name,
      description,
      price,
      stock
    ) values (
      {name},
      {description},
      {price},
      {stock}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Items)(implicit session: DBSession = autoSession): Items = {
    withSQL {
      update(Items).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.description -> entity.description,
        column.price -> entity.price,
        column.stock -> entity.stock
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Items)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Items).where.eq(column.id, entity.id) }.update.apply()
  }

}
