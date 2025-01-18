package models

import scalikejdbc._

case class Users(
  id: Int,
  username: String,
  email: String,
  passwordHash: String) {

  def save()(implicit session: DBSession = Users.autoSession): Users = Users.save(this)(session)

  def destroy()(implicit session: DBSession = Users.autoSession): Int = Users.destroy(this)(session)

}


object Users extends SQLSyntaxSupport[Users] {

  override val tableName = "users"

  override val columns = Seq("id", "username", "email", "password_hash")

  def apply(u: SyntaxProvider[Users])(rs: WrappedResultSet): Users = apply(u.resultName)(rs)
  def apply(u: ResultName[Users])(rs: WrappedResultSet): Users = new Users(
    id = rs.get(u.id),
    username = rs.get(u.username),
    email = rs.get(u.email),
    passwordHash = rs.get(u.passwordHash)
  )

  val u = Users.syntax("u")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Users] = {
    withSQL {
      select.from(Users as u).where.eq(u.id, id)
    }.map(Users(u.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Users] = {
    withSQL(select.from(Users as u)).map(Users(u.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Users as u)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Users] = {
    withSQL {
      select.from(Users as u).where.append(where)
    }.map(Users(u.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Users] = {
    withSQL {
      select.from(Users as u).where.append(where)
    }.map(Users(u.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Users as u).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    username: String,
    email: String,
    passwordHash: String)(implicit session: DBSession = autoSession): Users = {
    val generatedKey = withSQL {
      insert.into(Users).namedValues(
        column.username -> username,
        column.email -> email,
        column.passwordHash -> passwordHash
      )
    }.updateAndReturnGeneratedKey.apply()

    Users(
      id = generatedKey.toInt,
      username = username,
      email = email,
      passwordHash = passwordHash)
  }

  def batchInsert(entities: collection.Seq[Users])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(String, Any)]] = entities.map(entity =>
      Seq(
        "username" -> entity.username,
        "email" -> entity.email,
        "passwordHash" -> entity.passwordHash))
    SQL("""insert into users(
      username,
      email,
      password_hash
    ) values (
      {username},
      {email},
      {passwordHash}
    )""").batchByName(params.toSeq: _*).apply[List]()
  }

  def save(entity: Users)(implicit session: DBSession = autoSession): Users = {
    withSQL {
      update(Users).set(
        column.id -> entity.id,
        column.username -> entity.username,
        column.email -> entity.email,
        column.passwordHash -> entity.passwordHash
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Users)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Users).where.eq(column.id, entity.id) }.update.apply()
  }

}
