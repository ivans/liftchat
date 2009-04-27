package hr.ivan.util.cache

import java.util.Properties;

import net.liftweb.util.Log
import net.sf.ehcache.event.CacheEventListenerFactory;

class CacheEventListenerFactoryImpl extends CacheEventListenerFactory {

    override def createCacheEventListener(prop : Properties) : CacheEventListener = {
        Log.info(" ===> CacheEventListenerFactoryImpl <===")
        return new hr.ivan.util.cache.CacheEventListener();
    }

}
