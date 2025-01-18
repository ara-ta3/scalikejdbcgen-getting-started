package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class ItemsSpec extends Specification {

  "Items" should {

    val i = Items.syntax("i")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Items.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Items.findBy(sqls.eq(i.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Items.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Items.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Items.findAllBy(sqls.eq(i.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Items.countBy(sqls.eq(i.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Items.create(name = "MyString", price = new java.math.BigDecimal("1"))
      created should not(beNull)
    }
    "save a record" in new AutoRollback {
      val entity = Items.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Items.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Items.findAll().head
      val deleted = Items.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Items.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Items.findAll()
      entities.foreach(e => Items.destroy(e))
      val batchInserted = Items.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
