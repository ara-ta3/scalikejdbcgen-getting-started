package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class OrderedItemsSpec extends Specification {

  "OrderedItems" should {

    val oi = OrderedItems.syntax("oi")

    "find by primary keys" in new AutoRollback {
      val maybeFound = OrderedItems.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = OrderedItems.findBy(sqls.eq(oi.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = OrderedItems.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = OrderedItems.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = OrderedItems.findAllBy(sqls.eq(oi.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = OrderedItems.countBy(sqls.eq(oi.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = OrderedItems.create(orderId = 123, itemId = 123, quantity = 123, priceAtPurchase = new java.math.BigDecimal("1"))
      created should not(beNull)
    }
    "save a record" in new AutoRollback {
      val entity = OrderedItems.findAll().head
      // TODO modify something
      val modified = entity
      val updated = OrderedItems.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = OrderedItems.findAll().head
      val deleted = OrderedItems.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = OrderedItems.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = OrderedItems.findAll()
      entities.foreach(e => OrderedItems.destroy(e))
      val batchInserted = OrderedItems.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
