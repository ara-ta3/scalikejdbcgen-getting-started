package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class CurrentCartSpec extends Specification {

  "CurrentCart" should {

    val cc = CurrentCart.syntax("cc")

    "find by primary keys" in new AutoRollback {
      val maybeFound = CurrentCart.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = CurrentCart.findBy(sqls.eq(cc.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = CurrentCart.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = CurrentCart.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = CurrentCart.findAllBy(sqls.eq(cc.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = CurrentCart.countBy(sqls.eq(cc.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = CurrentCart.create(userId = 123, itemId = 123, quantity = 123)
      created should not(beNull)
    }
    "save a record" in new AutoRollback {
      val entity = CurrentCart.findAll().head
      // TODO modify something
      val modified = entity
      val updated = CurrentCart.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = CurrentCart.findAll().head
      val deleted = CurrentCart.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = CurrentCart.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = CurrentCart.findAll()
      entities.foreach(e => CurrentCart.destroy(e))
      val batchInserted = CurrentCart.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
