package xyz.erupt.upms.handler;

import org.springframework.stereotype.Component;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;
import xyz.erupt.core.cache.EruptCache;
import xyz.erupt.core.cache.EruptCacheLRU;
import xyz.erupt.core.util.EruptAssert;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.upms.constant.FetchConst;
import xyz.erupt.upms.model.EruptDictItem;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YuePeng
 * date 2020/12/16 18:00
 */
@Component
public class DictCodeChoiceFetchHandler implements ChoiceFetchHandler {

    @Resource
    private EruptDao eruptDao;

    private final EruptCache<List<VLModel>> dictCache = new EruptCacheLRU<>(500);

    @Override
    public List<VLModel> fetch(String[] params) {
        EruptAssert.notNull(params, DictCodeChoiceFetchHandler.class.getSimpleName() + " → params[0] must dict → code");
        return dictCache.getAndSet(DictCodeChoiceFetchHandler.class.getName() + ":" + params[0],
                params.length == 2 ? Long.parseLong(params[1]) : FetchConst.DEFAULT_CACHE_TIME, () ->
                        eruptDao.queryEntityList(EruptDictItem.class, "eruptDict.code = '" + params[0] + "'", null)
                                .stream().map((item) -> new VLModel(item.getCode(), item.getName())).collect(Collectors.toList()));
    }

}
