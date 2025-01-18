package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.ZonedDateTime


class OrdersSpec extends Specification {

  "Orders" should {

    val o = Orders.syntax("o")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Orders.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Orders.findBy(sqls.eq(o.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Orders.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Orders.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Orders.findAllBy(sqls.eq(o.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Orders.countBy(sqls.eq(o.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Orders.create(userId = 123, totalAmount = new java.math.BigDecimal("1"))
      created should not(beNull)
    }
    "save a record" in new AutoRollback {
      val entity = Orders.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Orders.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Orders.findAll().head
      val deleted = Orders.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Orders.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Orders.findAll()
      entities.foreach(e => Orders.destroy(e))
      val batchInserted = Orders.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
