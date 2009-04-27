package hr.ivan.util.cache

import net.liftweb.util.Log

import net.sf.ehcache.CacheException
import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element

class CacheEventListener extends net.sf.ehcache.event.CacheEventListener with Cloneable {

    override def clone() : Object = super.clone

    def dispose() = {
        Log.info("ehcache :: dispose")
    }

    def notifyElementEvicted(cache : Ehcache, elem : Element) = {
        Log.info("ehcache :: notifyElementEvicted " + cache.getName()
                 + " " + elem.getObjectKey() + " " + elem.getObjectValue())
    }

    def notifyRemoveAll(cache : Ehcache) = {
        Log.info("ehcache :: notifyRemoveAll")
    }

    def notifyElementExpired(cache : Ehcache, elem : Element) {
        Log.info("ehache :: notifyElementExpired " + cache.getName()
                 + " " + elem.getObjectKey() + " " + elem.getObjectValue());
    }

    @throws(classOf[CacheException])
    def notifyElementPut(cache : Ehcache, elem : Element) {
        Log.info("ehcache :: notifyElementPut " + cache.getName() + " "
                 + elem.getObjectKey() + " " + elem.getObjectValue());
    }

    @throws(classOf[CacheException])
    def notifyElementRemoved(cache : Ehcache, elem : Element) {
    	Log.info("ehcache :: notifyElementRemoved " + cache.getName()
                 + " " + elem.getObjectKey() + " " + elem.getObjectValue());
    }

    @throws(classOf[CacheException])
    def notifyElementUpdated(cache : Ehcache, elem : Element) {
        Log.info("ehcache :: notifyElementUpdated " + cache.getName()
                 + " " + elem.getObjectKey() + " " + elem.getObjectValue())
    }

}
